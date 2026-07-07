import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../services/api';

interface LoginProps {
  onLoginSuccess: () => void;
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const Login: React.FC<LoginProps> = ({ onLoginSuccess, showToast }) => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!usernameOrEmail || !password) {
      showToast('Please fill in all fields', 'error');
      return;
    }

    setLoading(true);
    try {
      const response = await api.auth.login({ usernameOrEmail, password });
      if (response.success) {
        showToast('Login successful! Welcome back.', 'success');
        onLoginSuccess();
        navigate('/dashboard');
      } else {
        showToast(response.message || 'Login failed', 'error');
      }
    } catch (err: any) {
      showToast(err.message || 'Invalid credentials or unverified email', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page-container animate-fade-in">
      <div className="auth-card">
        <header className="auth-header">
          <div className="auth-logo-text">HRMS</div>
          <h1 id="login-title">Sign In</h1>
          <p className="auth-subtitle">Welcome back. Enter your credentials to access your account.</p>
        </header>

        <form onSubmit={handleLogin} className="auth-form">
          <div className="form-group">
            <label className="form-label" htmlFor="usernameOrEmail">Username or Email</label>
            <input
              id="usernameOrEmail"
              type="text"
              className="form-input"
              placeholder="name@example.com"
              value={usernameOrEmail}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              className="form-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-full"
            disabled={loading}
            id="btn-submit-login"
          >
            {loading ? 'Signing In...' : 'Sign In'}
          </button>
        </form>

        <footer className="auth-footer">
          <p>
            Don't have an account?{' '}
            <Link to="/register" className="auth-link">
              Register as Staff
            </Link>
          </p>
        </footer>
      </div>
    </main>
  );
};
