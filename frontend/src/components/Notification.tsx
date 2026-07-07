import React, { useEffect } from 'react';
import { AlertCircle, CheckCircle2, X } from 'lucide-react';

export interface ToastMessage {
  id: string;
  type: 'success' | 'error';
  text: string;
}

interface NotificationProps {
  message: ToastMessage | null;
  onClose: () => void;
}

export const Notification: React.FC<NotificationProps> = ({ message, onClose }) => {
  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => {
        onClose();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [message, onClose]);

  if (!message) return null;

  return (
    <div className={`toast-container animate-slide-up ${message.type}`}>
      <div className="toast-icon">
        {message.type === 'success' ? (
          <CheckCircle2 size={20} color="#10b981" />
        ) : (
          <AlertCircle size={20} color="#ef4444" />
        )}
      </div>
      <div className="toast-content">
        <p className="toast-text">{message.text}</p>
      </div>
      <button onClick={onClose} className="toast-close">
        <X size={16} />
      </button>
    </div>
  );
};
