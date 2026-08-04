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

export function EnergyPage() {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const [units, setUnits] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [feedback, setFeedback] = useState(null);

  const [history, setHistory] = useState([]);
  const [isHistoryLoading, setIsHistoryLoading] = useState(true);

  const [monthlyTotal, setMonthlyTotal] = useState(null);
  const [isSummaryLoading, setIsSummaryLoading] = useState(true);

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const fetchHistory = useCallback(async () => {
    setIsHistoryLoading(true);
    try {
      const response = await api.get('/energy-service/api/energy/history');
      setHistory(response.data || []);
    } catch (error) {
      console.error('Failed to load energy history:', error);
    } finally {
      setIsHistoryLoading(false);
    }
  }, []);

  const fetchMonthlySummary = useCallback(async () => {
    setIsSummaryLoading(true);
    try {
      const response = await api.get(
        `/energy-service/api/energy/history/${username}/${year}/${month}`
      );
      setMonthlyTotal(response.data?.totalEnergyCarbonEmission ?? 0);
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

  const todayEntry = history.find((row) => row.date === TODAY);

  const handleAddOrUpdate = async (event, isUpdate) => {
    event.preventDefault();
    setIsSubmitting(true);
    setFeedback(null);

    try {
      const payload = { units: Number(units) };
      if (isUpdate) {
        await api.put('/energy-service/api/energy/update', payload);
        setFeedback({ type: 'success', text: 'Today\'s entry updated' });
      } else {
        await api.post('/energy-service/api/energy/add', payload);
        setFeedback({ type: 'success', text: 'Entry added' });
      }
      setUnits('');
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

  const handleDeleteConfirmed = async () => {
    setIsSubmitting(true);
    setFeedback(null);

    try {
      await api.delete('/energy-service/api/energy/delete');
      setFeedback({ type: 'success', text: "Today's entry deleted" });
      setShowDeleteConfirm(false);
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
      <button type="button" className="back-link" onClick={() => navigate('/dashboard')}>
        ← Dashboard
      </button>

      <h1>Energy</h1>

      {feedback && <p className={`service-message ${feedback.type}`}>{feedback.text}</p>}

      <section className="service-card">
        <p className="service-card-title">
          {todayEntry ? "Update today's energy usage" : 'Log energy usage'}
        </p>
        <form className="service-form">
          <div>
            <label htmlFor="units">Units (kWh)</label>
            <input
              id="units"
              name="units"
              type="number"
              min="0"
              step="0.1"
              placeholder="e.g. 180"
              value={units}
              onChange={(e) => setUnits(e.target.value)}
              required
            />
          </div>

          <div className="service-form-actions">
            {!todayEntry ? (
              <button
                type="submit"
                disabled={isSubmitting || !units}
                onClick={(e) => handleAddOrUpdate(e, false)}
              >
                Add entry
              </button>
            ) : (
              <>
                <button
                  type="button"
                  disabled={isSubmitting || !units}
                  onClick={(e) => handleAddOrUpdate(e, true)}
                >
                  Update entry
                </button>
                <button
                  type="button"
                  className="danger-button"
                  disabled={isSubmitting}
                  onClick={() => setShowDeleteConfirm(true)}
                >
                  Delete today&apos;s entry
                </button>
              </>
            )}
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
        <p className="service-card-title">Usage history</p>
        {isHistoryLoading ? (
          <p className="service-empty">Loading...</p>
        ) : history.length === 0 ? (
          <p className="service-empty">No entries yet.</p>
        ) : (
          <table className="service-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Units</th>
              </tr>
            </thead>
            <tbody>
              {history.map((row) => (
                <tr key={row.id}>
                  <td>{row.date}</td>
                  <td>{row.units} kWh</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {showDeleteConfirm && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-card">
            <h2>Delete today&apos;s entry?</h2>
            <p>This removes today&apos;s energy record. This can&apos;t be undone.</p>
            <div className="modal-actions">
              <button type="button" className="danger-button" disabled={isSubmitting} onClick={handleDeleteConfirmed}>
                {isSubmitting ? 'Deleting...' : 'Delete'}
              </button>
              <button type="button" onClick={() => setShowDeleteConfirm(false)} disabled={isSubmitting}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
