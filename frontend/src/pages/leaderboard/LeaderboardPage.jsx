import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import '../ServicePage.css';

const COUNT_OPTIONS = [5, 10, 20];

export function LeaderboardPage() {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  const [view, setView] = useState('top'); // 'top' | 'last'
  const [count, setCount] = useState(5);

  const [entries, setEntries] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [feedback, setFeedback] = useState(null);

  const fetchLeaderboard = useCallback(async () => {
    setIsLoading(true);
    setFeedback(null);
    try {
      const path = view === 'top'
        ? `/leaderboard-service/api/leaderboard/monthly/top/${count}`
        : `/leaderboard-service/api/leaderboard/monthly/last/${count}`;
      const response = await api.get(path);
      setEntries(response.data || []);
    } catch (error) {
      console.error('Failed to load leaderboard:', error);
      setFeedback({ type: 'error', text: 'Unable to load leaderboard' });
    } finally {
      setIsLoading(false);
    }
  }, [view, count]);

  useEffect(() => {
    fetchLeaderboard();
  }, [fetchLeaderboard]);

  return (
    <main className="service-page">
      <button type="button" className="back-link" onClick={() => navigate('/dashboard')}>
        ← Dashboard
      </button>

      <h1>Leaderboard</h1>

      {feedback && <p className={`service-message ${feedback.type}`}>{feedback.text}</p>}

      <section className="service-card">
        <div className="service-summary-row" style={{ marginBottom: 16 }}>
          <select value={view} onChange={(e) => setView(e.target.value)}>
            <option value="top">Top performers</option>
            <option value="last">Needs improvement</option>
          </select>
          <select value={count} onChange={(e) => setCount(Number(e.target.value))}>
            {COUNT_OPTIONS.map((n) => (
              <option key={n} value={n}>Top {n}</option>
            ))}
          </select>
        </div>

        {isLoading ? (
          <p className="service-empty">Loading...</p>
        ) : entries.length === 0 ? (
          <p className="service-empty">No data yet.</p>
        ) : (
          <table className="service-table">
            <thead>
              <tr>
                <th>Rank</th>
                <th>Username</th>
                <th style={{ textAlign: 'right' }}>Total emission</th>
              </tr>
            </thead>
            <tbody>
              {entries.map((entry, index) => (
                <tr
                  key={entry.username}
                  className={entry.username === username ? 'row-highlight' : ''}
                >
                  <td>{index + 1}</td>
                  <td>{entry.username}{entry.username === username ? ' (you)' : ''}</td>
                  <td style={{ textAlign: 'right' }}>{entry.totalEmission?.toFixed(1)} kg</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </main>
  );
}
