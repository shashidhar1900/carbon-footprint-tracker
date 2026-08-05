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
  const [showPassword, setShowPassword] = useState(false);
  const [showRegisterPassword, setShowRegisterPassword] = useState(false);

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
      <div className="home-page-bg" aria-hidden="true">
        <div className="bg-pattern" />
        <div className="bg-blob bg-blob-1" />
        <div className="bg-blob bg-blob-2" />
      </div>

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
          <div className="password-field">
            <input
              id="password"
              name="password"
              type={showPassword ? 'text' : 'password'}
              placeholder="Enter password"
              value={formData.password}
              onChange={handleChange}
              required
            />
            <button
              type="button"
              className="password-toggle"
              onClick={() => setShowPassword((prev) => !prev)}
              aria-label={showPassword ? 'Hide password' : 'Show password'}
              tabIndex={-1}
            >
              {showPassword ? (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
                  <path d="M3 3l18 18M10.6 10.6a2 2 0 0 0 2.8 2.8M9.5 5.3A10.4 10.4 0 0 1 12 5c5 0 9 4 10.5 7-.6 1.1-1.4 2.3-2.4 3.3M6.5 6.5C4.6 7.8 3.1 9.6 1.5 12 3 15 7 19 12 19c1.4 0 2.7-.3 3.9-.8" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              ) : (
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
                  <path d="M1.5 12S5 5 12 5s10.5 7 10.5 7-3.5 7-10.5 7S1.5 12 1.5 12Z" strokeLinecap="round" strokeLinejoin="round" />
                  <circle cx="12" cy="12" r="3" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              )}
            </button>
          </div>

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
              <div className="password-field">
                <input
                  id="register-password"
                  name="password"
                  type={showRegisterPassword ? 'text' : 'password'}
                  placeholder="Create a password"
                  value={registerData.password}
                  onChange={handleRegisterChange}
                  required
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowRegisterPassword((prev) => !prev)}
                  aria-label={showRegisterPassword ? 'Hide password' : 'Show password'}
                  tabIndex={-1}
                >
                  {showRegisterPassword ? (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
                      <path d="M3 3l18 18M10.6 10.6a2 2 0 0 0 2.8 2.8M9.5 5.3A10.4 10.4 0 0 1 12 5c5 0 9 4 10.5 7-.6 1.1-1.4 2.3-2.4 3.3M6.5 6.5C4.6 7.8 3.1 9.6 1.5 12 3 15 7 19 12 19c1.4 0 2.7-.3 3.9-.8" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                  ) : (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
                      <path d="M1.5 12S5 5 12 5s10.5 7 10.5 7-3.5 7-10.5 7S1.5 12 1.5 12Z" strokeLinecap="round" strokeLinejoin="round" />
                      <circle cx="12" cy="12" r="3" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                  )}
                </button>
              </div>

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
