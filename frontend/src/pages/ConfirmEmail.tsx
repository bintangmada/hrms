import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { CheckCircle2, XCircle, Loader2, LogIn } from 'lucide-react';
import { api } from '../services/api';

interface ConfirmEmailProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const ConfirmEmail: React.FC<ConfirmEmailProps> = ({ showToast }) => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const navigate = useNavigate();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!token) {
      setStatus('error');
      setMessage('Invalid verification token.');
      return;
    }

    const verify = async () => {
      try {
        const res = await api.auth.confirmEmail(token);
        if (res.success) {
          setStatus('success');
          setMessage(res.data || 'Your email has been verified successfully!');
          showToast('Email verified successfully! You can now sign in.', 'success');
        } else {
          setStatus('error');
          setMessage(res.message || 'Verification failed.');
        }
      } catch (err: any) {
        setStatus('error');
        setMessage(err.message || 'Something went wrong during verification.');
      }
    };

    verify();
  }, [token, showToast]);

  return (
    <main className="auth-page-container">
      <div className="auth-card glass-panel animate-slide-up text-center">
        {status === 'loading' && (
          <div className="status-indicator">
            <Loader2 className="spinner spinner-large" size={48} color="#6366f1" />
            <h2>Verifying Email...</h2>
            <p className="auth-subtitle">Please wait while we validate your activation token</p>
          </div>
        )}

        {status === 'success' && (
          <div className="status-indicator">
            <CheckCircle2 size={54} color="#10b981" className="status-icon" />
            <h2>Verification Successful!</h2>
            <p className="status-description">{message}</p>
            <button 
              onClick={() => navigate('/login')} 
              className="btn btn-primary btn-full"
              id="btn-confirm-login"
            >
              <LogIn size={18} />
              <span>Go to Login</span>
            </button>
          </div>
        )}

        {status === 'error' && (
          <div className="status-indicator">
            <XCircle size={54} color="#ef4444" className="status-icon" />
            <h2>Verification Failed</h2>
            <p className="status-description">{message}</p>
            <button 
              onClick={() => navigate('/login')} 
              className="btn btn-secondary btn-full"
              id="btn-confirm-back"
            >
              <span>Back to Login</span>
            </button>
          </div>
        )}
      </div>
    </main>
  );
};
