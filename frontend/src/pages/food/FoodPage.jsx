import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import { getTodayDateString } from '../../utils/date';
import '../ServicePage.css';

const TODAY = getTodayDateString();

const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

const TYPES = ['VEG', 'NON_VEG', 'JUNK'];

const TYPE_LABELS = { VEG: 'Veg', NON_VEG: 'Non-veg', JUNK: 'Junk' };
const TYPE_CLASS = { VEG: 'badge-success', NON_VEG: 'badge-danger', JUNK: 'badge-warning' };

export function FoodPage() {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const [formData, setFormData] = useState({ type: TYPES[0], quantity: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [feedback, setFeedback] = useState(null);

  const [history, setHistory] = useState([]);
  const [isHistoryLoading, setIsHistoryLoading] = useState(true);

  const [monthlyTotal, setMonthlyTotal] = useState(null);
  const [isSummaryLoading, setIsSummaryLoading] = useState(true);

  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchHistory = useCallback(async () => {
    setIsHistoryLoading(true);
    try {
      const response = await api.get('/food-service/api/food/history');
      setHistory(response.data || []);
    } catch (error) {
      console.error('Failed to load food history:', error);
    } finally {
      setIsHistoryLoading(false);
    }
  }, []);

  const fetchMonthlySummary = useCallback(async () => {
    setIsSummaryLoading(true);
    try {
      const response = await api.get(
        `/food-service/api/food/history/${username}/${year}/${month}`
      );
      setMonthlyTotal(response.data?.totalFoodCarbonEmission ?? 0);
    } catch (error) {
      console.error('Failed to load monthly summary:', error);
      setMonthlyTotal(0);
    } finally {
      setIsSummaryLoading(false);
    }
  }, [username, year, month]);

  useEffect(() => {
    fetchHistory();
  }, [fetchHistory]);

  useEffect(() => {
    fetchMonthlySummary();
  }, [fetchMonthlySummary]);

  const todayEntryExists = history.some(
    (row) => row.type === formData.type && row.date === TODAY
  );

  const handleFormChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleAddOrUpdate = async (event, isUpdate) => {
    event.preventDefault();
    setIsSubmitting(true);
    setFeedback(null);

    try {
      const payload = { type: formData.type, quantity: Number(formData.quantity) };
      if (isUpdate) {
        await api.put('/food-service/api/food/update', payload);
        setFeedback({ type: 'success', text: 'Entry updated' });
      } else {
        await api.post('/food-service/api/food/add', payload);
        setFeedback({ type: 'success', text: 'Entry added' });
      }
      setFormData({ type: TYPES[0], quantity: '' });
      fetchHistory();
      fetchMonthlySummary();
    } catch (error) {
      console.error('Save failed:', error);
      setFeedback({
        type: 'error',
        text: error.response?.data || error.message || 'Something went wrong',
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEditRow = (row) => {
    setFormData({ type: row.type, quantity: String(row.quantity) });
    setFeedback(null);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const confirmDelete = (type) => {
    setDeleteTarget(type);
  };

  const handleDeleteConfirmed = async () => {
    if (!deleteTarget) return;
    setIsSubmitting(true);
    setFeedback(null);

    try {
      await api.delete(`/food-service/api/food/delete/${deleteTarget}`);
      setFeedback({ type: 'success', text: `Deleted ${TYPE_LABELS[deleteTarget]} entries` });
      setDeleteTarget(null);
      fetchHistory();
      fetchMonthlySummary();
    } catch (error) {
      console.error('Delete failed:', error);
      setFeedback({
        type: 'error',
        text: error.response?.data || error.message || 'Delete failed',
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="service-page">
      <div className="page-bg" aria-hidden="true">
        <div className="bg-pattern bg-pattern-food" />
        <div className="bg-blob bg-blob-1" />
        <div className="bg-blob bg-blob-2" />
      </div>

      <button type="button" className="back-link" onClick={() => navigate('/dashboard')}>
        ← Dashboard
      </button>

      <h1>Food</h1>

      {feedback && <p className={`service-message ${feedback.type}`}>{feedback.text}</p>}

      <section className="service-card">
        <p className="service-card-title">Log food consumption</p>
        <form className="service-form">
          <div className="service-form-row">
            <div>
              <label htmlFor="type">Type</label>
              <select id="type" name="type" value={formData.type} onChange={handleFormChange}>
                {TYPES.map((type) => (
                  <option key={type} value={type}>{TYPE_LABELS[type]}</option>
                ))}
              </select>
            </div>
            <div>
              <label htmlFor="quantity">Quantity (g)</label>
              <input
                id="quantity"
                name="quantity"
                type="number"
                min="0"
                step="1"
                placeholder="e.g. 250"
                value={formData.quantity}
                onChange={handleFormChange}
                required
              />
            </div>
          </div>

          <div className="service-form-actions">
            <button
              type="submit"
              disabled={isSubmitting || !formData.quantity}
              onClick={(e) => handleAddOrUpdate(e, false)}
            >
              Add entry
            </button>
            <button
              type="button"
              disabled={isSubmitting || !formData.quantity || !todayEntryExists}
              onClick={(e) => handleAddOrUpdate(e, true)}
              title={!todayEntryExists ? 'No entry for this type today yet' : undefined}
            >
              Update entry
            </button>
            <button
              type="button"
              className="danger-button"
              disabled={isSubmitting || !todayEntryExists}
              onClick={() => confirmDelete(formData.type)}
              title={!todayEntryExists ? 'No entry for this type today yet' : undefined}
            >
              Delete type
            </button>
          </div>
        </form>
      </section>

      <section className="service-card">
        <p className="service-card-title">Monthly summary</p>
        <div className="service-summary-row">
          <select value={month} onChange={(e) => setMonth(Number(e.target.value))}>
            {MONTHS.map((label, index) => (
              <option key={label} value={index + 1}>{label}</option>
            ))}
          </select>
          <select value={year} onChange={(e) => setYear(Number(e.target.value))}>
            {[year, year - 1, year - 2].map((y) => (
              <option key={y} value={y}>{y}</option>
            ))}
          </select>
          <div className="summary-total">
            {isSummaryLoading ? 'Loading...' : `${(monthlyTotal ?? 0).toFixed(1)} kg CO2`}
          </div>
        </div>
      </section>

      <section className="service-card">
        <p className="service-card-title">Consumption history</p>
        {isHistoryLoading ? (
          <p className="service-empty">Loading...</p>
        ) : history.length === 0 ? (
          <p className="service-empty">No entries yet.</p>
        ) : (
          <table className="service-table">
            <thead>
              <tr>
                <th>Type</th>
                <th>Quantity</th>
                <th>Date</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {history.map((row) => (
                <tr key={row.id}>
                  <td>
                    <span className={`badge ${TYPE_CLASS[row.type] || ''}`}>
                      {TYPE_LABELS[row.type] || row.type}
                    </span>
                  </td>
                  <td>{row.quantity} g</td>
                  <td>{row.date}</td>
                  <td className="service-table-actions">
                    {row.date === TODAY ? (
                      <>
                        <button type="button" onClick={() => handleEditRow(row)}>Edit</button>
                        <button type="button" className="danger-link" onClick={() => confirmDelete(row.type)}>
                          Delete
                        </button>
                      </>
                    ) : (
                      <span className="service-table-muted">—</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {deleteTarget && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-card">
            <h2>Delete {TYPE_LABELS[deleteTarget]} entries?</h2>
            <p>This removes all logged quantity for this type. This can&apos;t be undone.</p>
            <div className="modal-actions">
              <button type="button" className="danger-button" disabled={isSubmitting} onClick={handleDeleteConfirmed}>
                {isSubmitting ? 'Deleting...' : 'Delete'}
              </button>
              <button type="button" onClick={() => setDeleteTarget(null)} disabled={isSubmitting}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
