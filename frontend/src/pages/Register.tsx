import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, User, UserPlus, Sparkles } from 'lucide-react';
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
    <main className="auth-page-container">
      <div className="auth-card glass-panel animate-slide-up">
        <header className="auth-header">
          <div className="auth-brand-logo">
            <Sparkles size={28} className="sparkle-icon" />
          </div>
          <h1 id="register-title">Create Staff Account</h1>
          <p className="auth-subtitle">Get started with our HRMS portal by setting up your credentials</p>
        </header>

        <form onSubmit={handleRegister} className="auth-form">
          <div className="form-group">
            <label className="form-label" htmlFor="username">Username</label>
            <div className="input-with-icon">
              <User size={18} className="input-icon" />
              <input
                id="username"
                type="text"
                className="form-input icon-padding"
                placeholder="Choose a username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="email">Email Address</label>
            <div className="input-with-icon">
              <Mail size={18} className="input-icon" />
              <input
                id="email"
                type="email"
                className="form-input icon-padding"
                placeholder="Enter your corporate/personal email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
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
                placeholder="Create a strong password"
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
            id="btn-submit-register"
          >
            {loading ? (
              <span className="spinner"></span>
            ) : (
              <>
                <UserPlus size={18} />
                <span>Sign Up</span>
              </>
            )}
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
