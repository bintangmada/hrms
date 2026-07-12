import React, { useState, useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Users, LogOut, Shield, Wallet, Clock, Terminal, ChevronLeft, ChevronRight } from 'lucide-react';
import { removeToken, removeCurrentUser, getCurrentUser, UserPermissionResponse } from '../services/api';

interface SidebarProps {
  onLogout: () => void;
  permissions: UserPermissionResponse[];
}

export const Sidebar: React.FC<SidebarProps> = ({ onLogout, permissions }) => {
  const user = getCurrentUser();
  const navigate = useNavigate();
  const [isCollapsed, setIsCollapsed] = useState<boolean>(() => {
    return localStorage.getItem('sidebar-collapsed') === 'true';
  });

  useEffect(() => {
    localStorage.setItem('sidebar-collapsed', String(isCollapsed));
  }, [isCollapsed]);

  const handleLogoutClick = () => {
    removeToken();
    removeCurrentUser();
    onLogout();
    navigate('/login');
  };

  const getInitials = (name: string) => {
    return name.slice(0, 2).toUpperCase();
  };

  const isAdmin = user && (user.role.includes('ROLE_ADMIN') || user.role.includes('ROLE_SUPER_ADMIN'));
  const isSuperAdmin = user && user.role.includes('ROLE_SUPER_ADMIN');

  const hasMenuPermission = (menuCode: string) => {
    if (isAdmin) return true;
    return permissions.some(p => p.menuCode === menuCode && p.canRead === 1);
  };

  return (
    <aside className={`sidebar-container glass-panel ${isCollapsed ? 'collapsed' : ''}`}>
      <button 
        className="sidebar-toggle-btn"
        onClick={() => setIsCollapsed(!isCollapsed)}
        aria-label={isCollapsed ? "Expand sidebar" : "Collapse sidebar"}
      >
        {isCollapsed ? <ChevronRight size={14} /> : <ChevronLeft size={14} />}
      </button>

      <div className="sidebar-brand">
        <div className="brand-logo">HR</div>
        <span className="brand-name">HRMS Portal</span>
      </div>

      <div 
        className="user-profile-section" 
        data-tooltip={user ? `${user.username} (${user.username === 'masteradmin' ? 'MASTER_ADMIN' : user.role.replace('ROLE_', '')})` : 'Guest User'}
      >
        <div className="user-avatar-wrapper">
          <div className="user-avatar">{user ? getInitials(user.username) : 'U'}</div>
        </div>
        <div className="user-details">
          <div className="user-name">{user ? user.username : 'Guest User'}</div>
          <div className="user-role-badge">
            {user ? (user.username === 'masteradmin' ? 'MASTER_ADMIN' : user.role.replace('ROLE_', '')) : 'Visitor'}
          </div>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink 
          to="/dashboard" 
          className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          data-tooltip="Dashboard"
        >
          <LayoutDashboard size={20} />
          <span>Dashboard</span>
        </NavLink>

        {(isAdmin || hasMenuPermission('EMPLOYEE')) && (
          <NavLink 
            to="/employees" 
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            id="nav-link-employees"
            data-tooltip="Employees"
          >
            <Users size={20} />
            <span>Employees</span>
          </NavLink>
        )}

        {(isAdmin || hasMenuPermission('ATTENDANCE')) && (
          <NavLink 
            to="/attendance" 
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            id="nav-link-attendance"
            data-tooltip="Attendance"
          >
            <Clock size={20} />
            <span>Attendance</span>
          </NavLink>
        )}

        {(isAdmin || hasMenuPermission('FINANCE')) && (
          <NavLink 
            to="/finance" 
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            id="nav-link-finance"
            data-tooltip="Finance"
          >
            <Wallet size={20} />
            <span>Finance</span>
          </NavLink>
        )}

        {isAdmin && (
          <NavLink 
            to="/roles" 
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            id="nav-link-roles"
            data-tooltip="Roles & Perms"
          >
            <Shield size={20} />
            <span>Roles & Perms</span>
          </NavLink>
        )}

        {isSuperAdmin && (
          <NavLink 
            to="/master-console" 
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            id="nav-link-master-console"
            data-tooltip="Master Console"
          >
            <Terminal size={20} />
            <span>Master Console</span>
          </NavLink>
        )}
      </nav>

      <div className="sidebar-footer">
        <button 
          onClick={handleLogoutClick} 
          className="logout-btn" 
          id="btn-logout"
          data-tooltip="Sign Out"
        >
          <LogOut size={18} />
          <span>Sign Out</span>
        </button>
      </div>
    </aside>
  );
};

