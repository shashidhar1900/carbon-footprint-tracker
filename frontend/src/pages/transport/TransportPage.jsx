import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axiosInstance";
import { getTodayDateString } from "../../utils/date";
import "../ServicePage.css";

const TODAY = getTodayDateString();

const MONTHS = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

const MODES = ["CAR", "BUS", "BIKE"];

export function TransportPage() {
  const navigate = useNavigate();
  const username = localStorage.getItem("username");

  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const [formData, setFormData] = useState({ mode: MODES[0], distance: "" });
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
      const response = await api.get(
        "/transport-service/api/transport/history",
      );
      setHistory((response.data || []).sort((a, b) => b.id - a.id));
    } catch (error) {
      console.error("Failed to load transport history:", error);
    } finally {
      setIsHistoryLoading(false);
    }
  }, []);

  const fetchMonthlySummary = useCallback(async () => {
    setIsSummaryLoading(true);
    try {
      const response = await api.get(
        `/transport-service/api/transport/history/${username}/${year}/${month}`,
      );
      setMonthlyTotal(response.data?.totalTransportCarbonEmission ?? 0);
    } catch (error) {
      console.error("Failed to load monthly summary:", error);
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
    (row) => row.mode === formData.mode && row.date === TODAY,
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
      const payload = {
        mode: formData.mode,
        distance: Number(formData.distance),
      };
      if (isUpdate) {
        await api.put("/transport-service/api/transport/update", payload);
        setFeedback({ type: "success", text: "Entry updated" });
      } else {
        await api.post("/transport-service/api/transport/add", payload);
        setFeedback({ type: "success", text: "Entry added" });
      }
      setFormData({ mode: MODES[0], distance: "" });
      fetchHistory();
      fetchMonthlySummary();
    } catch (error) {
      console.error("Save failed:", error);
      setFeedback({
        type: "error",
        text: error.response?.data || error.message || "Something went wrong",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEditRow = (row) => {
    setFormData({ mode: row.mode, distance: String(row.distance) });
    setFeedback(null);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const confirmDelete = (mode) => {
    setDeleteTarget(mode);
  };

  const handleDeleteConfirmed = async () => {
    if (!deleteTarget) return;
    setIsSubmitting(true);
    setFeedback(null);

    try {
      await api.delete(
        `/transport-service/api/transport/delete/${deleteTarget}`,
      );
      setFeedback({ type: "success", text: `Deleted ${deleteTarget} entries` });
      setDeleteTarget(null);
      fetchHistory();
      fetchMonthlySummary();
    } catch (error) {
      console.error("Delete failed:", error);
      setFeedback({
        type: "error",
        text: error.response?.data || error.message || "Delete failed",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="service-page">
      <div className="page-bg" aria-hidden="true">
        <div className="bg-pattern bg-pattern-transport" />
        <div className="bg-blob bg-blob-1" />
        <div className="bg-blob bg-blob-2" />
      </div>

      <button
        type="button"
        className="back-link"
        onClick={() => navigate("/dashboard")}
      >
        ← Dashboard
      </button>

      <h1>Transport</h1>

      {feedback && (
        <p className={`service-message ${feedback.type}`}>{feedback.text}</p>
      )}

      <section className="service-card">
        <p className="service-card-title">Log transport usage</p>
        <form className="service-form">
          <div className="service-form-row">
            <div>
              <label htmlFor="mode">Mode</label>
              <select
                id="mode"
                name="mode"
                value={formData.mode}
                onChange={handleFormChange}
              >
                {MODES.map((mode) => (
                  <option key={mode} value={mode}>
                    {mode}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label htmlFor="distance">Distance (km)</label>
              <input
                id="distance"
                name="distance"
                type="number"
                min="0"
                step="0.1"
                placeholder="e.g. 12.5"
                value={formData.distance}
                onChange={handleFormChange}
                required
              />
            </div>
          </div>

          <div className="service-form-actions">
            <button
              type="submit"
              disabled={isSubmitting || !formData.distance}
              onClick={(e) => handleAddOrUpdate(e, false)}
            >
              Add entry
            </button>
            <button
              type="button"
              disabled={isSubmitting || !formData.distance || !todayEntryExists}
              onClick={(e) => handleAddOrUpdate(e, true)}
              title={
                !todayEntryExists
                  ? "No entry for this mode today yet"
                  : undefined
              }
            >
              Update entry
            </button>
            <button
              type="button"
              className="danger-button"
              disabled={isSubmitting || !todayEntryExists}
              onClick={() => confirmDelete(formData.mode)}
              title={
                !todayEntryExists
                  ? "No entry for this mode today yet"
                  : undefined
              }
            >
              Delete mode
            </button>
          </div>
        </form>
      </section>

      <section className="service-card">
        <p className="service-card-title">Monthly summary</p>
        <div className="service-summary-row">
          <select
            value={month}
            onChange={(e) => setMonth(Number(e.target.value))}
          >
            {MONTHS.map((label, index) => (
              <option key={label} value={index + 1}>
                {label}
              </option>
            ))}
          </select>
          <select
            value={year}
            onChange={(e) => setYear(Number(e.target.value))}
          >
            {[year, year - 1, year - 2].map((y) => (
              <option key={y} value={y}>
                {y}
              </option>
            ))}
          </select>
          <div className="summary-total">
            {isSummaryLoading
              ? "Loading..."
              : `${(monthlyTotal ?? 0).toFixed(1)} kg CO2`}
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
          <div className="service-table-scroll">
            <table className="service-table">
              <thead>
                <tr>
                  <th>Mode</th>
                  <th>Distance</th>
                  <th>Date</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {history.map((row) => (
                  <tr key={row.id}>
                    <td>{row.mode}</td>
                    <td>{row.distance} km</td>
                    <td>{row.date}</td>
                    <td className="service-table-actions">
                      {row.date === TODAY ? (
                        <>
                          <button
                            type="button"
                            onClick={() => handleEditRow(row)}
                          >
                            Edit
                          </button>
                          <button
                            type="button"
                            className="danger-link"
                            onClick={() => confirmDelete(row.mode)}
                          >
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
          </div>
        )}
      </section>

      {deleteTarget && (
        <div className="modal-overlay" role="dialog" aria-modal="true">
          <div className="modal-card">
            <h2>Delete {deleteTarget} entries?</h2>
            <p>
              This removes all logged distance for this mode. This can&apos;t be
              undone.
            </p>
            <div className="modal-actions">
              <button
                type="button"
                className="danger-button"
                disabled={isSubmitting}
                onClick={handleDeleteConfirmed}
              >
                {isSubmitting ? "Deleting..." : "Delete"}
              </button>
              <button
                type="button"
                onClick={() => setDeleteTarget(null)}
                disabled={isSubmitting}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
