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
      const path =
        view === 'top'
          ? `/leaderboard-service/api/leaderboard/monthly/top/${count}`
          : `/leaderboard-service/api/leaderboard/monthly/last/${count}`;
      const response = await api.get(path);
      const leaderboardEntries = response.data || [];

      // Fetch user rank and append it to the leaderboard
      if (username) {
        const userRankResponse = await api.get(`/leaderboard-service/api/leaderboard/monthly/rank/${username}`);
        const userRank = userRankResponse.data;

        // Check if the user's rank is already in the list
        const isUserInList = leaderboardEntries.some((entry) => entry.username === username);
        if (!isUserInList) {
          leaderboardEntries.push(userRank);
        }
      }

      setEntries(leaderboardEntries);
    } catch (error) {
      console.error('Failed to load leaderboard:', error);
      setFeedback({ type: 'error', text: 'Unable to load leaderboard' });
    } finally {
      setIsLoading(false);
    }
  }, [view, count, username]);

  useEffect(() => {
    fetchLeaderboard();
  }, [fetchLeaderboard]);

  return (
    <main className="service-page">
      <div className="page-bg" aria-hidden="true">
        <div className="bg-pattern bg-pattern-leaderboard" />
        <div className="bg-blob bg-blob-1" />
        <div className="bg-blob bg-blob-2" />
      </div>

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
              <option key={n} value={n}>
                Top {n}
              </option>
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
              {entries.map((entry) => (
                <tr
                  key={entry.username}
                  className={entry.username === username ? 'row-highlight' : ''}
                >
                  <td>{entry.rank}</td> {/* Use the actual rank from the entry object */}
                  <td>
                    {entry.username}
                    {entry.username === username ? ' (you)' : ''}
                  </td>
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
