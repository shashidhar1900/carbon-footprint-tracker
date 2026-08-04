import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import './Dashboard.css';

const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

export function Dashboard() {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const [isLoading, setIsLoading] = useState(true);
  const [feedback, setFeedback] = useState(null);
  const [totals, setTotals] = useState({ transport: 0, food: 0, energy: 0 });

  const fetchTotals = useCallback(async () => {
    setIsLoading(true);
    setFeedback(null);

    try {
      const [transportRes, foodRes, energyRes] = await Promise.allSettled([
        api.get(`/transport-service/api/transport/history/${username}/${year}/${month}`),
        api.get(`/food-service/api/food/history/${username}/${year}/${month}`),
        api.get(`/energy-service/api/energy/history/${username}/${year}/${month}`),
      ]);

      setTotals({
        transport: transportRes.status === 'fulfilled'
          ? transportRes.value.data?.totalTransportCarbonEmission || 0
          : 0,
        food: foodRes.status === 'fulfilled'
          ? foodRes.value.data?.totalFoodCarbonEmission || 0
          : 0,
        energy: energyRes.status === 'fulfilled'
          ? energyRes.value.data?.totalEnergyCarbonEmission || 0
          : 0,
      });
    } catch (error) {
      console.error('Failed to load dashboard totals:', error);
      setFeedback({ type: 'error', text: 'Unable to load this month\'s data' });
    } finally {
      setIsLoading(false);
    }
  }, [username, year, month]);

  useEffect(() => {
    fetchTotals();
  }, [fetchTotals]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    navigate('/');
  };

  const totalEmission = totals.transport + totals.food + totals.energy;

  return (
    <main className="dashboard-page">
      <div className="dashboard-header">
        <div>
          <h1>Welcome back, {username}</h1>
          <p>Here&apos;s your carbon footprint this month</p>
        </div>

        <div className="dashboard-header-actions">
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
          <button type="button" className="logout-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </div>

      {feedback && <p className={`dashboard-message ${feedback.type}`}>{feedback.text}</p>}

      <div className="total-emission-card">
        <p className="total-emission-label">Total carbon emission</p>
        <p className="total-emission-value">
          {isLoading ? 'Loading...' : `${totalEmission.toFixed(1)} kg CO2`}
        </p>
      </div>

      <section className="dashboard-section">
        <p className="dashboard-section-title">Log activity</p>
        <div className="dashboard-cards dashboard-cards-3">
          <button type="button" className="dashboard-card" onClick={() => navigate('/transport')}>
            <p className="dashboard-card-title">Transport</p>
            <p className="dashboard-card-value">
              {isLoading ? '...' : `${totals.transport.toFixed(1)} kg CO2`}
            </p>
          </button>

          <button type="button" className="dashboard-card" onClick={() => navigate('/food')}>
            <p className="dashboard-card-title">Food</p>
            <p className="dashboard-card-value">
              {isLoading ? '...' : `${totals.food.toFixed(1)} kg CO2`}
            </p>
          </button>

          <button type="button" className="dashboard-card" onClick={() => navigate('/energy')}>
            <p className="dashboard-card-title">Energy</p>
            <p className="dashboard-card-value">
              {isLoading ? '...' : `${totals.energy.toFixed(1)} kg CO2`}
            </p>
          </button>
        </div>
      </section>

      <section className="dashboard-section">
        <p className="dashboard-section-title">Community</p>
        <div className="dashboard-cards dashboard-cards-2">
          <button type="button" className="dashboard-card" onClick={() => navigate('/leaderboard')}>
            <p className="dashboard-card-title">Leaderboard</p>
            <p className="dashboard-card-subtitle">See top and bottom performers</p>
          </button>

          <button type="button" className="dashboard-card" onClick={() => navigate('/analytics')}>
            <p className="dashboard-card-title">Analytics</p>
            <p className="dashboard-card-subtitle">Trends across all categories</p>
          </button>
        </div>
      </section>
    </main>
  );
}
