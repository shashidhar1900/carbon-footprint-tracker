import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import './HomePage.css';

export function HomePage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [registerData, setRegisterData] = useState({ username: '', email: '', password: '' });
  const [isLoading, setIsLoading] = useState(false);
  const [feedback, setFeedback] = useState(null);
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleRegisterChange = (event) => {
    const { name, value } = event.target;
    setRegisterData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsLoading(true);
    setFeedback(null);

    try {
      const response = await api.post('/auth-service/api/auth/login', formData);
      const { token, username, role } = response.data;

      localStorage.setItem('token', token);
      localStorage.setItem('username', username);
      localStorage.setItem('role', role);

      setFeedback({ type: 'success', text: 'Login successful' });
      navigate('/dashboard');
    } catch (error) {
      console.error('Login failed:', error);
      setFeedback({
        type: 'error',
        text: error.response?.data || error.message || 'Unable to reach the server',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegisterSubmit = async (event) => {
    event.preventDefault();
    setIsLoading(true);
    setFeedback(null);

    try {
      const response = await api.post('/auth-service/api/auth/register', registerData);
      setFeedback({ type: 'success', text: response.data?.message || 'Registration successful' });
      setIsRegisterOpen(false);
      setRegisterData({ username: '', email: '', password: '' });
    } catch (error) {
      console.error('Registration failed:', error);
      setFeedback({
        type: 'error',
        text: error.response?.data || error.message || 'Unable to reach the server',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className={`home-page ${isRegisterOpen ? 'blurred' : ''}`}>
      <section className="auth-card">
        <h1>Welcome Back</h1>
        <p>Please sign in to continue</p>

        <form className="auth-form" onSubmit={handleSubmit}>
          <label htmlFor="username">Username</label>
          <input
            id="username"
            name="username"
            type="text"
            placeholder="Enter username"
            value={formData.username}
            onChange={handleChange}
            required
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            placeholder="Enter password"
            value={formData.password}
            onChange={handleChange}
            required
          />

          <div className="auth-actions">
            <button type="submit" disabled={isLoading}>
              {isLoading ? 'Logging in...' : 'Login'}
            </button>
            <button
              type="button"
              className="register-button"
              disabled={isLoading}
              onClick={() => setIsRegisterOpen(true)}
            >
              {isLoading ? 'Please wait...' : 'Register'}
            </button>
          </div>

          {feedback && <p className={`form-message ${feedback.type}`}>{feedback.text}</p>}
        </form>
      </section>

      {isRegisterOpen && (
        <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="register-title">
          <div className="modal-card">
            <div className="modal-header">
              <h2 id="register-title">Create an account</h2>
              <button type="button" className="modal-close" onClick={() => setIsRegisterOpen(false)}>
                ×
              </button>
            </div>

            <form className="auth-form register-form" onSubmit={handleRegisterSubmit}>
              <label htmlFor="register-username">Username</label>
              <input
                id="register-username"
                name="username"
                type="text"
                placeholder="Choose a username"
                value={registerData.username}
                onChange={handleRegisterChange}
                required
              />

              <label htmlFor="register-email">Email</label>
              <input
                id="register-email"
                name="email"
                type="email"
                placeholder="Enter your email"
                value={registerData.email}
                onChange={handleRegisterChange}
                required
              />

              <label htmlFor="register-password">Password</label>
              <input
                id="register-password"
                name="password"
                type="password"
                placeholder="Create a password"
                value={registerData.password}
                onChange={handleRegisterChange}
                required
              />

              <div className="auth-actions modal-actions">
                <button type="submit">Create account</button>
                <button type="button" className="register-button" onClick={() => setIsRegisterOpen(false)}>
                  Cancel
                </button>
              </div>
              {feedback && <p className={`form-message ${feedback.type}`}>{feedback.text}</p>}
            </form>
          </div>
        </div>
      )}
    </main>
  );
}
