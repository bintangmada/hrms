import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Sidebar } from './components/Sidebar';
import { Notification, ToastMessage } from './components/Notification';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { ConfirmEmail } from './pages/ConfirmEmail';
import { Dashboard } from './pages/Dashboard';
import { Employees } from './pages/Employees';
import { Roles } from './pages/Roles';
import { Attendance } from './pages/Attendance';
import { Finance } from './pages/Finance';
import { MasterConsole } from './pages/MasterConsole';
import { getToken, getCurrentUser, api, UserPermissionResponse } from './services/api';
import './App.css';

export const App: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!getToken());
  const [toast, setToast] = useState<ToastMessage | null>(null);
  const [permissions, setPermissions] = useState<UserPermissionResponse[]>([]);
  const [loadingPerms, setLoadingPerms] = useState<boolean>(false);
  
  const user = getCurrentUser();
  const isAdmin = user && (user.role.includes('ROLE_ADMIN') || user.role.includes('ROLE_SUPER_ADMIN'));
  const isSuperAdmin = user && user.role.includes('ROLE_SUPER_ADMIN');

  useEffect(() => {
    if (isAuthenticated) {
      setLoadingPerms(true);
      api.roleMenus.getMyPermissions()
        .then(data => {
          setPermissions(data);
        })
        .catch(err => {
          console.error("Failed to load user permissions", err);
        })
        .finally(() => {
          setLoadingPerms(false);
        });
    } else {
      setPermissions([]);
    }
  }, [isAuthenticated]);

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

  const hasMenuPermission = (menuCode: string) => {
    if (isAdmin) return true;
    return permissions.some(p => p.menuCode === menuCode && p.canRead === 1);
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
                  <Sidebar onLogout={handleLogout} permissions={permissions} />
                  <main className="main-content">
                    {loadingPerms ? (
                      <div className="dashboard-loader">
                        <div className="spinner spinner-large"></div>
                        <p>Loading security clearance...</p>
                      </div>
                    ) : (
                      <Routes>
                        <Route path="/dashboard" element={<Dashboard showToast={showToast} />} />
                        {(isAdmin || hasMenuPermission('EMPLOYEE')) && (
                          <Route path="/employees" element={<Employees showToast={showToast} />} />
                        )}
                        {(isAdmin || hasMenuPermission('ATTENDANCE')) && (
                          <Route path="/attendance" element={<Attendance showToast={showToast} />} />
                        )}
                        {(isAdmin || hasMenuPermission('FINANCE')) && (
                          <Route path="/finance" element={<Finance showToast={showToast} />} />
                        )}
                        {isAdmin && (
                          <Route path="/roles" element={<Roles showToast={showToast} />} />
                        )}
                        {isSuperAdmin && (
                          <Route path="/master-console" element={<MasterConsole showToast={showToast} />} />
                        )}
                        <Route path="*" element={<Navigate to="/dashboard" replace />} />
                      </Routes>
                    )}
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

