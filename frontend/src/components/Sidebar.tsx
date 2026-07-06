import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Users, LogOut } from 'lucide-react';
import { removeToken, removeCurrentUser, getCurrentUser } from '../services/api';

interface SidebarProps {
  onLogout: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ onLogout }) => {
  const user = getCurrentUser();
  const navigate = useNavigate();

  const handleLogoutClick = () => {
    removeToken();
    removeCurrentUser();
    onLogout();
    navigate('/login');
  };

  const getInitials = (name: string) => {
    return name.slice(0, 2).toUpperCase();
  };

  return (
    <aside className="sidebar-container glass-panel">
      <div className="sidebar-brand">
        <div className="brand-logo">HR</div>
        <span className="brand-name">HRMS Portal</span>
      </div>

      <div className="user-profile-section">
        <div className="user-avatar-wrapper">
          <div className="user-avatar">{user ? getInitials(user.username) : 'U'}</div>
          <div className="avatar-ring"></div>
        </div>
        <div className="user-details">
          <div className="user-name">{user ? user.username : 'Guest User'}</div>
          <div className="user-role-badge">
            {user ? user.role.replace('ROLE_', '') : 'Visitor'}
          </div>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink 
          to="/dashboard" 
          className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
        >
          <LayoutDashboard size={20} />
          <span>Dashboard</span>
        </NavLink>
        <NavLink 
          to="/employees" 
          className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
        >
          <Users size={20} />
          <span>Employees</span>
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <button onClick={handleLogoutClick} className="logout-btn" id="btn-logout">
          <LogOut size={18} />
          <span>Sign Out</span>
        </button>
      </div>
    </aside>
  );
};
