import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../services/api';

interface RegisterProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const Register: React.FC<RegisterProps> = ({ showToast }) => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !email || !password) {
      showToast('Please fill in all fields', 'error');
      return;
    }

    setLoading(true);
    try {
      // Direct registration endpoint for public staff
      const response = await api.auth.register({
        username,
        email,
        password,
        roleIds: [] // Will default to ROLE_STAFF in the backend
      });

      if (response.success) {
        showToast('Registration successful! Please check your email to verify your account.', 'success');
        navigate('/login');
      } else {
        showToast(response.message || 'Registration failed', 'error');
      }
    } catch (err: any) {
      showToast(err.message || 'Registration failed. Check inputs.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page-container animate-fade-in">
      <div className="auth-card">
        <header className="auth-header">
          <div className="auth-logo-text">HRMS</div>
          <h1 id="register-title">Create Account</h1>
          <p className="auth-subtitle">Get started by setting up your staff credentials.</p>
        </header>

        <form onSubmit={handleRegister} className="auth-form">
          <div className="form-group">
            <label className="form-label" htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              className="form-input"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="email">Email Address</label>
            <input
              id="email"
              type="email"
              className="form-input"
              placeholder="name@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
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
            id="btn-submit-register"
          >
            {loading ? 'Creating Account...' : 'Sign Up'}
          </button>
        </form>

        <footer className="auth-footer">
          <p>
            Already have an account?{' '}
            <Link to="/login" className="auth-link">
              Sign In
            </Link>
          </p>
        </footer>
      </div>
    </main>
  );
};
