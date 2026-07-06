import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Sidebar } from './components/Sidebar';
import { Notification, ToastMessage } from './components/Notification';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { ConfirmEmail } from './pages/ConfirmEmail';
import { Dashboard } from './pages/Dashboard';
import { Employees } from './pages/Employees';
import { getToken } from './services/api';
import './App.css';

export const App: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!getToken());
  const [toast, setToast] = useState<ToastMessage | null>(null);

  const showToast = (text: string, type: 'success' | 'error') => {
    setToast({
      id: Math.random().toString(36).substring(2, 9),
      type,
      text,
    });
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    showToast('Signed out successfully', 'success');
  };

  return (
    <Router>
      <div className="app-container">
        <Notification message={toast} onClose={() => setToast(null)} />
        <Routes>
          {/* Public Auth Routes */}
          <Route 
            path="/login" 
            element={
              isAuthenticated ? (
                <Navigate to="/dashboard" replace />
              ) : (
                <Login onLoginSuccess={() => setIsAuthenticated(true)} showToast={showToast} />
              )
            } 
          />
          <Route 
            path="/register" 
            element={
              isAuthenticated ? (
                <Navigate to="/dashboard" replace />
              ) : (
                <Register showToast={showToast} />
              )
            } 
          />
          <Route 
            path="/confirm-email" 
            element={<ConfirmEmail showToast={showToast} />} 
          />

          {/* Protected Portal Routes */}
          <Route
            path="/*"
            element={
              isAuthenticated ? (
                <div className="app-layout">
                  <Sidebar onLogout={handleLogout} />
                  <main className="main-content">
                    <Routes>
                      <Route path="/dashboard" element={<Dashboard showToast={showToast} />} />
                      <Route path="/employees" element={<Employees showToast={showToast} />} />
                      <Route path="*" element={<Navigate to="/dashboard" replace />} />
                    </Routes>
                  </main>
                </div>
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
