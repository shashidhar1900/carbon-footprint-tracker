import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axiosInstance";
import "../ServicePage.css";

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

function sumBy(list, key) {
  return (list || []).reduce((total, item) => total + (item[key] || 0), 0);
}

export function AnalyticsPage() {
  const navigate = useNavigate();

  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const [isLoading, setIsLoading] = useState(true);
  const [feedback, setFeedback] = useState(null);

  const [totals, setTotals] = useState([]);
  const [categoryTotals, setCategoryTotals] = useState({
    transport: 0,
    food: 0,
    energy: 0,
  });

  const fetchAnalytics = useCallback(async () => {
    setIsLoading(true);
    setFeedback(null);

    try {
      const [totalRes, transportRes, foodRes, energyRes] =
        await Promise.allSettled([
          api.get(
            `/analytics-service/api/analytics/monthlyTotalEmission/${year}/${month}`,
          ),
          api.get(
            `/analytics-service/api/analytics/mothlyTransportEmission/${year}/${month}`,
          ),
          api.get(
            `/analytics-service/api/analytics/monthlyFoodEmission/${year}/${month}`,
          ),
          api.get(
            `/analytics-service/api/analytics/monthlyEnergyEmission/${year}/${month}`,
          ),
        ]);

      const totalList =
        totalRes.status === "fulfilled" ? totalRes.value.data || [] : [];
      const transportList =
        transportRes.status === "fulfilled"
          ? transportRes.value.data || []
          : [];
      const foodList =
        foodRes.status === "fulfilled" ? foodRes.value.data || [] : [];
      const energyList =
        energyRes.status === "fulfilled" ? energyRes.value.data || [] : [];

      setTotals(
        [...totalList].sort((a, b) => b.totalEmission - a.totalEmission),
      );
      setCategoryTotals({
        transport: sumBy(transportList, "totalTransportEmission"),
        food: sumBy(foodList, "totalFoodEmission"),
        energy: sumBy(energyList, "totalEnergyEmission"),
      });
    } catch (error) {
      console.error("Failed to load analytics:", error);
      setFeedback({ type: "error", text: "Unable to load analytics" });
    } finally {
      setIsLoading(false);
    }
  }, [year, month]);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  return (
    <main className="service-page">
      <div className="page-bg" aria-hidden="true">
        <div className="bg-pattern bg-pattern-analytics" />
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

      <h1>Analytics</h1>

      {feedback && (
        <p className={`service-message ${feedback.type}`}>{feedback.text}</p>
      )}

      <section className="service-card">
        <div className="service-summary-row" style={{ marginBottom: 16 }}>
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
        </div>

        <p className="service-card-title">Category breakdown (all users)</p>
        <div className="stat-grid">
          <div className="stat-box">
            <p className="stat-label">Transport</p>
            <p className="stat-value">
              {isLoading ? "..." : `${categoryTotals.transport.toFixed(1)} kg`}
            </p>
          </div>
          <div className="stat-box">
            <p className="stat-label">Food</p>
            <p className="stat-value">
              {isLoading ? "..." : `${categoryTotals.food.toFixed(1)} kg`}
            </p>
          </div>
          <div className="stat-box">
            <p className="stat-label">Energy</p>
            <p className="stat-value">
              {isLoading ? "..." : `${categoryTotals.energy.toFixed(1)} kg`}
            </p>
          </div>
        </div>
      </section>

      <section className="service-card">
        <p className="service-card-title">Total emission by user</p>
        {isLoading ? (
          <p className="service-empty">Loading...</p>
        ) : totals.length === 0 ? (
          <p className="service-empty">No data yet.</p>
        ) : (
          <div className="service-table-scroll">
            <table className="service-table">
              <thead>
                <tr>
                  <th>Username</th>
                  <th style={{ textAlign: "right" }}>Total emission</th>
                </tr>
              </thead>
              <tbody>
                {totals.map((row) => (
                  <tr key={row.username}>
                    <td>{row.username}</td>
                    <td style={{ textAlign: "right" }}>
                      {row.totalEmission?.toFixed(1)} kg
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </main>
  );
}
