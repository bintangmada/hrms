import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, LogIn, Sparkles } from 'lucide-react';
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
    <main className="auth-page-container">
      <div className="auth-card glass-panel animate-slide-up">
        <header className="auth-header">
          <div className="auth-brand-logo">
            <Sparkles size={28} className="sparkle-icon" />
          </div>
          <h1 id="login-title">HRMS Portal</h1>
          <p className="auth-subtitle">Sign in to manage employees, permissions and settings</p>
        </header>

        <form onSubmit={handleLogin} className="auth-form">
          <div className="form-group">
            <label className="form-label" htmlFor="usernameOrEmail">Username or Email</label>
            <div className="input-with-icon">
              <Mail size={18} className="input-icon" />
              <input
                id="usernameOrEmail"
                type="text"
                className="form-input icon-padding"
                placeholder="Enter username or email"
                value={usernameOrEmail}
                onChange={(e) => setUsernameOrEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">Password</label>
            <div className="input-with-icon">
              <Lock size={18} className="input-icon" />
              <input
                id="password"
                type="password"
                className="form-input icon-padding"
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-full"
            disabled={loading}
            id="btn-submit-login"
          >
            {loading ? (
              <span className="spinner"></span>
            ) : (
              <>
                <LogIn size={18} />
                <span>Sign In</span>
              </>
            )}
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
