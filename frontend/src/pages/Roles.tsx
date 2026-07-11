import React, { useEffect, useState } from 'react';
import { Shield, ShieldAlert, KeyRound, Plus, Edit2, Trash2, Save, Users, UserCheck, X, Sparkles, Lock, Loader2 } from 'lucide-react';
import { api, RoleDto, MenuDto, UserDto } from '../services/api';

interface RolesProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const Roles: React.FC<RolesProps> = ({ showToast }) => {
  const [activeTab, setActiveTab] = useState<'roles' | 'users'>('roles');
  
  // Data State
  const [roles, setRoles] = useState<RoleDto[]>([]);
  const [menus, setMenus] = useState<MenuDto[]>([]);
  const [users, setUsers] = useState<UserDto[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Selection State
  const [selectedRole, setSelectedRole] = useState<RoleDto | null>(null);
  const [selectedUser, setSelectedUser] = useState<UserDto | null>(null);
  
  // Permission Editor State (mapping: menuId -> { read: boolean, write: boolean, delete: boolean })
  const [permissionsState, setPermissionsState] = useState<Record<number, { read: boolean; write: boolean; delete: boolean }>>({});
  const [savingPermissions, setSavingPermissions] = useState(false);
  
  // Role Modal State
  const [showRoleModal, setShowRoleModal] = useState(false);
  const [roleFormType, setRoleFormType] = useState<'create' | 'edit'>('create');
  const [roleName, setRoleName] = useState('');
  const [roleDescription, setRoleDescription] = useState('');
  
  // User Role Modal State
  const [showUserModal, setShowUserModal] = useState(false);
  const [userSelectedRoles, setUserSelectedRoles] = useState<number[]>([]);
  const [savingUserRoles, setSavingUserRoles] = useState(false);

  // Fetch all initial data
  const fetchData = async () => {
    try {
      setLoading(true);
      const [allRoles, allMenus, allUsers] = await Promise.all([
        api.roles.getAll(),
        api.menus.getAll(),
        api.users.getAll()
      ]);
      setRoles(allRoles);
      setMenus(allMenus);
      setUsers(allUsers);
      
      // Auto-select first role if available
      if (allRoles.length > 0 && !selectedRole) {
        handleSelectRole(allRoles[0]);
      }
    } catch (err: any) {
      showToast(err.message || 'Failed to fetch settings data', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  // Fetch permissions for a specific role
  const handleSelectRole = async (role: RoleDto) => {
    setSelectedRole(role);
    try {
      const perms = await api.roleMenus.getByRoleId(role.id);
      
      // Map existing permissions to state
      const state: Record<number, { read: boolean; write: boolean; delete: boolean }> = {};
      
      // Pre-initialize menus with false
      menus.forEach(menu => {
        state[menu.id] = { read: false, write: false, delete: false };
      });
      
      // Overlay actual saved database permissions
      perms.forEach(p => {
        state[p.menuId] = {
          read: p.canRead === 1,
          write: p.canWrite === 1,
          delete: p.canDelete === 1
        };
      });
      
      setPermissionsState(state);
    } catch (err: any) {
      showToast(err.message || `Failed to fetch permissions for role ${role.name}`, 'error');
    }
  };

  // Toggle single permission checkbox
  const handlePermissionToggle = (menuId: number, type: 'read' | 'write' | 'delete') => {
    setPermissionsState(prev => {
      const current = prev[menuId] || { read: false, write: false, delete: false };
      return {
        ...prev,
        [menuId]: {
          ...current,
          [type]: !current[type]
        }
      };
    });
  };

  // Save current role permission mappings
  const handleSavePermissions = async () => {
    if (!selectedRole) return;
    setSavingPermissions(true);
    
    try {
      const promises = Object.entries(permissionsState).map(([menuIdStr, perms]) => {
        const menuId = parseInt(menuIdStr);
        return api.roleMenus.assign({
          roleId: selectedRole.id,
          menuId,
          canRead: perms.read ? 1 : 0,
          canWrite: perms.write ? 1 : 0,
          canDelete: perms.delete ? 1 : 0
        });
      });
      
      await Promise.all(promises);
      showToast(`Permissions updated successfully for ${selectedRole.name}`, 'success');
      // Refresh current role mapping
      handleSelectRole(selectedRole);
    } catch (err: any) {
      showToast(err.message || 'Failed to save role permissions', 'error');
    } finally {
      setSavingPermissions(false);
    }
  };

  // Open Create Role Modal
  const openCreateRoleModal = () => {
    setRoleFormType('create');
    setRoleName('ROLE_');
    setRoleDescription('');
    setShowRoleModal(true);
  };

  // Open Edit Role Modal
  const openEditRoleModal = (role: RoleDto, e: React.MouseEvent) => {
    e.stopPropagation(); // Avoid selecting role while clicking edit
    setRoleFormType('edit');
    setRoleName(role.name);
    setRoleDescription(role.description || '');
    setShowRoleModal(true);
  };

  // Handle Role Creation/Edition Submit
  const handleRoleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!roleName.trim()) {
      showToast('Role name is required', 'error');
      return;
    }

    // Standardize role name
    let formattedName = roleName.trim().toUpperCase();
    if (!formattedName.startsWith('ROLE_')) {
      formattedName = 'ROLE_' + formattedName;
    }

    try {
      if (roleFormType === 'create') {
        const newRole = await api.roles.create({ name: formattedName, description: roleDescription });
        showToast('Role created successfully!', 'success');
        setRoles(prev => [...prev, newRole]);
        handleSelectRole(newRole);
      } else if (roleFormType === 'edit' && selectedRole) {
        const updated = await api.roles.update(selectedRole.id, { name: formattedName, description: roleDescription });
        showToast('Role updated successfully!', 'success');
        setRoles(prev => prev.map(r => r.id === selectedRole.id ? updated : r));
        setSelectedRole(updated);
      }
      setShowRoleModal(false);
    } catch (err: any) {
      showToast(err.message || 'Action failed', 'error');
    }
  };

  // Handle Role Deletion
  const handleDeleteRole = async (role: RoleDto, e: React.MouseEvent) => {
    e.stopPropagation();
    if (role.name === 'ROLE_SUPER_ADMIN' || role.name === 'ROLE_ADMIN' || role.name === 'ROLE_STAFF') {
      showToast('System core roles cannot be deleted!', 'error');
      return;
    }

    if (window.confirm(`Are you sure you want to delete the role ${role.name}? This will remove all menu assignments and dissociate user accounts.`)) {
      try {
        await api.roles.delete(role.id);
        showToast('Role deleted successfully', 'success');
        setRoles(prev => prev.filter(r => r.id !== role.id));
        if (selectedRole?.id === role.id) {
          setSelectedRole(null);
          setPermissionsState({});
        }
      } catch (err: any) {
        showToast(err.message || 'Failed to delete role', 'error');
      }
    }
  };

  // Open User Role Edit Modal
  const openUserModal = (user: UserDto) => {
    setSelectedUser(user);
    // Parse user's current roles by name and map to IDs
    const userRoleNames = user.role.split(',').map(r => r.trim());
    const matchedIds = roles
      .filter(r => userRoleNames.includes(r.name))
      .map(r => r.id);
    
    setUserSelectedRoles(matchedIds);
    setShowUserModal(true);
  };

  // Toggle user role checkbox
  const handleUserRoleToggle = (roleId: number) => {
    setUserSelectedRoles(prev => 
      prev.includes(roleId) ? prev.filter(id => id !== roleId) : [...prev, roleId]
    );
  };

  // Save updated user roles mapping
  const handleSaveUserRoles = async () => {
    if (!selectedUser) return;
    if (userSelectedRoles.length === 0) {
      showToast('User must have at least one assigned role!', 'error');
      return;
    }

    setSavingUserRoles(true);
    try {
      const updatedUser = await api.users.updateRoles(selectedUser.id, userSelectedRoles);
      showToast(`Roles updated successfully for user ${selectedUser.username}`, 'success');
      
      // Update local users array state
      setUsers(prev => prev.map(u => u.id === selectedUser.id ? updatedUser : u));
      setShowUserModal(false);
    } catch (err: any) {
      showToast(err.message || 'Failed to update user roles', 'error');
    } finally {
      setSavingUserRoles(false);
    }
  };

  return (
    <div className="page-wrapper animate-fade-in">
      <header className="page-header">
        <div>
          <h1>Settings & Authorization</h1>
          <p className="page-subtitle">Configure Role-Based Access Control (RBAC), menu permission levels, and manage user accounts</p>
        </div>
      </header>

      {/* Navigation Tab Bar */}
      <div className="tab-navigation-wrapper glass-panel" style={{ padding: '6px', marginBottom: '24px', display: 'inline-flex', gap: '8px' }}>
        <button 
          onClick={() => setActiveTab('roles')} 
          className={`tab-btn btn ${activeTab === 'roles' ? 'btn-primary' : 'btn-secondary'}`}
          style={{ padding: '8px 16px', border: 'none' }}
        >
          <KeyRound size={16} style={{ marginRight: 8 }} />
          <span>Roles & Permissions</span>
        </button>
        <button 
          onClick={() => setActiveTab('users')} 
          className={`tab-btn btn ${activeTab === 'users' ? 'btn-primary' : 'btn-secondary'}`}
          style={{ padding: '8px 16px', border: 'none' }}
        >
          <Users size={16} style={{ marginRight: 8 }} />
          <span>User Role Assignments</span>
        </button>
      </div>

      {loading ? (
        <div className="dashboard-loader">
          <div className="spinner spinner-large"></div>
          <p>Fetching authorization configurations...</p>
        </div>
      ) : (
        <div className="roles-grid" style={{ display: 'grid', gridTemplateColumns: activeTab === 'roles' ? '1fr 1.5fr' : '1fr', gap: '24px' }}>
          
          {/* LEFT PANEL: ROLES LIST OR USERS TABLE */}
          {activeTab === 'roles' ? (
            <div className="card glass-panel" style={{ display: 'flex', flexDirection: 'column', height: 'fit-content' }}>
              <div className="card-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '12px' }}>
                <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Shield size={18} className="text-primary" />
                  System Roles
                </h3>
                <button onClick={openCreateRoleModal} className="btn btn-primary btn-sm" style={{ padding: '6px 12px', fontSize: '0.85rem' }}>
                  <Plus size={14} style={{ marginRight: 4 }} />
                  Add Role
                </button>
              </div>

              <div className="roles-list" style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {roles.map(role => {
                  const isSelected = selectedRole?.id === role.id;
                  const isSystemRole = ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_STAFF'].includes(role.name);
                  
                  return (
                    <div 
                      key={role.id} 
                      onClick={() => handleSelectRole(role)}
                      className={`role-item-card ${isSelected ? 'selected' : ''}`}
                      style={{
                        padding: '14px',
                        borderRadius: '8px',
                        cursor: 'pointer',
                        background: isSelected ? 'rgba(99, 102, 241, 0.15)' : 'rgba(255, 255, 255, 0.03)',
                        border: isSelected ? '1px solid #6366f1' : '1px solid rgba(255,255,255,0.06)',
                        transition: 'all 0.2s ease',
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center'
                      }}
                    >
                      <div style={{ flex: 1, paddingRight: '12px' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                          <span style={{ fontWeight: 600, color: isSelected ? '#a5b4fc' : '#f3f4f6' }}>{role.name}</span>
                          {isSystemRole && <span className="badge badge-success" style={{ fontSize: '0.65rem', padding: '2px 6px' }}>System</span>}
                        </div>
                        <p style={{ margin: 0, fontSize: '0.8rem', color: 'rgba(255, 255, 255, 0.6)', lineHeight: '1.3' }}>
                          {role.description || 'No description provided'}
                        </p>
                      </div>

                      <div style={{ display: 'flex', gap: '6px' }}>
                        <button 
                          onClick={(e) => openEditRoleModal(role, e)}
                          className="action-btn edit-btn" 
                          style={{ padding: '6px' }}
                          title="Edit Role Name/Desc"
                        >
                          <Edit2 size={12} />
                        </button>
                        {!isSystemRole && (
                          <button 
                            onClick={(e) => handleDeleteRole(role, e)}
                            className="action-btn delete-btn"
                            style={{ padding: '6px' }}
                            title="Delete Role"
                          >
                            <Trash2 size={12} />
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          ) : (
            /* USERS TABLE */
            <div className="table-card glass-panel">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Email Address</th>
                    <th>Assigned Roles</th>
                    <th className="text-right">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map(user => (
                    <tr key={user.id} className="table-row">
                      <td className="font-semibold">{user.username}</td>
                      <td className="text-muted">{user.email}</td>
                      <td>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                          {user.role.split(',').map((r, idx) => (
                            <span key={idx} className="badge badge-info" style={{ fontSize: '0.75rem' }}>
                              {r.trim().replace('ROLE_', '')}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="text-right">
                        <button 
                          onClick={() => openUserModal(user)} 
                          className="btn btn-primary btn-sm"
                          style={{ padding: '6px 12px', fontSize: '0.8rem', display: 'inline-flex', alignItems: 'center', gap: 6 }}
                          id={`btn-assign-${user.id}`}
                        >
                          <UserCheck size={14} />
                          <span>Assign Roles</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                  {users.length === 0 && (
                    <tr>
                      <td colSpan={4} className="no-data-cell">No system user accounts found.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}

          {/* RIGHT PANEL: PERMISSIONS CONFIGURATOR (Roles Tab Only) */}
          {activeTab === 'roles' && (
            <div className="card glass-panel" style={{ display: 'flex', flexDirection: 'column' }}>
              {selectedRole ? (
                <>
                  <div className="card-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '12px' }}>
                    <div>
                      <h3 style={{ margin: 0, color: '#f3f4f6' }}>
                        Menu Access for <span style={{ color: '#818cf8' }}>{selectedRole.name}</span>
                      </h3>
                      <p style={{ margin: '4px 0 0 0', fontSize: '0.8rem', color: 'rgba(255, 255, 255, 0.5)' }}>
                        Define fine-grained CRUD rules mapping to main dashboard modules
                      </p>
                    </div>
                    
                    <button 
                      onClick={handleSavePermissions} 
                      disabled={savingPermissions}
                      className="btn btn-primary"
                      style={{ padding: '8px 16px', display: 'flex', alignItems: 'center', gap: '6px' }}
                      id="btn-save-permissions"
                    >
                      {savingPermissions ? (
                        <>
                          <Loader2 size={16} className="animate-spin" />
                          <span>Saving...</span>
                        </>
                      ) : (
                        <>
                          <Save size={16} />
                          <span>Save Changes</span>
                        </>
                      )}
                    </button>
                  </div>

                  <div className="permissions-table-wrapper" style={{ overflowX: 'auto' }}>
                    <table className="data-table" style={{ width: '100%' }}>
                      <thead>
                        <tr>
                          <th style={{ width: '40%' }}>Navigation Module</th>
                          <th className="text-center" style={{ width: '20%' }}>Read Access</th>
                          <th className="text-center" style={{ width: '20%' }}>Create/Edit (Write)</th>
                          <th className="text-center" style={{ width: '20%' }}>Delete</th>
                        </tr>
                      </thead>
                      <tbody>
                        {menus.map(menu => {
                          const perm = permissionsState[menu.id] || { read: false, write: false, delete: false };
                          return (
                            <tr key={menu.id} className="table-row">
                              <td>
                                <div style={{ display: 'flex', flexDirection: 'column' }}>
                                  <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{menu.name}</span>
                                  <span style={{ fontSize: '0.7rem', color: '#9ca3af' }}>Path: {menu.path} | Code: {menu.code}</span>
                                </div>
                              </td>
                              
                              {/* READ CHECKBOX */}
                              <td className="text-center">
                                <label className="custom-toggle" style={{ display: 'inline-block', position: 'relative', cursor: 'pointer' }}>
                                  <input 
                                    type="checkbox"
                                    checked={perm.read}
                                    onChange={() => handlePermissionToggle(menu.id, 'read')}
                                    style={{
                                      width: '18px',
                                      height: '18px',
                                      accentColor: '#6366f1',
                                      cursor: 'pointer'
                                    }}
                                  />
                                </label>
                              </td>

                              {/* WRITE CHECKBOX */}
                              <td className="text-center">
                                <label className="custom-toggle" style={{ display: 'inline-block', position: 'relative', cursor: 'pointer' }}>
                                  <input 
                                    type="checkbox"
                                    checked={perm.write}
                                    onChange={() => handlePermissionToggle(menu.id, 'write')}
                                    style={{
                                      width: '18px',
                                      height: '18px',
                                      accentColor: '#6366f1',
                                      cursor: 'pointer'
                                    }}
                                  />
                                </label>
                              </td>

                              {/* DELETE CHECKBOX */}
                              <td className="text-center">
                                <label className="custom-toggle" style={{ display: 'inline-block', position: 'relative', cursor: 'pointer' }}>
                                  <input 
                                    type="checkbox"
                                    checked={perm.delete}
                                    onChange={() => handlePermissionToggle(menu.id, 'delete')}
                                    style={{
                                      width: '18px',
                                      height: '18px',
                                      accentColor: '#6366f1',
                                      cursor: 'pointer'
                                    }}
                                  />
                                </label>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>

                  <div style={{ marginTop: '20px', padding: '12px', background: 'rgba(239, 68, 68, 0.05)', border: '1px dashed rgba(239, 68, 68, 0.2)', borderRadius: '8px', display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <ShieldAlert size={18} className="text-error" style={{ flexShrink: 0 }} />
                    <p style={{ margin: 0, fontSize: '0.75rem', color: 'rgba(255,255,255,0.7)', lineHeight: '1.4' }}>
                      <strong>Important:</strong> Changing permissions will update user access on next refresh or routing transition. Standard employee notification emails will automatically send out to all users assigned to this role.
                    </p>
                  </div>
                </>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '60px 20px', color: 'rgba(255,255,255,0.4)' }}>
                  <Lock size={48} style={{ marginBottom: '16px', opacity: 0.5 }} />
                  <h3>No Role Selected</h3>
                  <p style={{ margin: 0, fontSize: '0.85rem', textAlign: 'center' }}>Select an active role from the left list to start configuring its dynamic permissions matrix.</p>
                </div>
              )}
            </div>
          )}

        </div>
      )}

      {/* ROLE FORM MODAL (CREATE / EDIT) */}
      {showRoleModal && (
        <div className="modal-backdrop animate-fade-in">
          <div className="modal-container glass-panel animate-scale-in" style={{ maxWidth: '450px' }}>
            <header className="modal-header">
              <div className="modal-title-group">
                <Sparkles size={20} className="sparkle-icon" />
                <h2>{roleFormType === 'create' ? 'Create Custom Role' : 'Edit Role Description'}</h2>
              </div>
              <button onClick={() => setShowRoleModal(false)} className="modal-close-btn">
                <X size={18} />
              </button>
            </header>

            <form onSubmit={handleRoleSubmit} className="modal-form">
              <div className="form-group" style={{ marginBottom: '16px' }}>
                <label className="form-label" htmlFor="role-name">Role Code Name</label>
                <input
                  id="role-name"
                  type="text"
                  className="form-input"
                  placeholder="e.g. ROLE_STAFF_LEAD"
                  value={roleName}
                  onChange={(e) => setRoleName(e.target.value)}
                  disabled={roleFormType === 'edit' && ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_STAFF'].includes(selectedRole?.name || '')}
                  required
                />
                <small style={{ display: 'block', marginTop: '4px', color: 'rgba(255,255,255,0.5)', fontSize: '0.75rem' }}>
                  Role codes should be all uppercase, beginning with <code>ROLE_</code> (e.g. ROLE_HR_MANAGER).
                </small>
              </div>

              <div className="form-group" style={{ marginBottom: '20px' }}>
                <label className="form-label" htmlFor="role-desc">Role Description</label>
                <textarea
                  id="role-desc"
                  className="form-input"
                  placeholder="Explain role coverage and typical duties"
                  value={roleDescription}
                  onChange={(e) => setRoleDescription(e.target.value)}
                  style={{ minHeight: '80px', resize: 'vertical' }}
                />
              </div>

              <footer className="modal-footer">
                <button 
                  type="button" 
                  onClick={() => setShowRoleModal(false)} 
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  className="btn btn-primary"
                  id="btn-submit-role"
                >
                  <span>{roleFormType === 'create' ? 'Create Role' : 'Save Changes'}</span>
                </button>
              </footer>
            </form>
          </div>
        </div>
      )}

      {/* USER ROLE ASSIGNMENT MODAL */}
      {showUserModal && selectedUser && (
        <div className="modal-backdrop animate-fade-in">
          <div className="modal-container glass-panel animate-scale-in" style={{ maxWidth: '480px' }}>
            <header className="modal-header">
              <div className="modal-title-group">
                <UserCheck size={20} className="text-primary" />
                <h2>Assign Roles for {selectedUser.username}</h2>
              </div>
              <button onClick={() => setShowUserModal(false)} className="modal-close-btn">
                <X size={18} />
              </button>
            </header>

            <div className="modal-body" style={{ padding: '16px 0' }}>
              <p style={{ margin: '0 0 16px 0', fontSize: '0.85rem', color: 'rgba(255,255,255,0.7)' }}>
                Select the system security clearance roles for <strong>{selectedUser.username}</strong> ({selectedUser.email}).
                Users can be assigned multiple roles, which will merge their menu permissions.
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', maxHeight: '250px', overflowY: 'auto', paddingRight: '4px' }}>
                {roles.map(role => {
                  const isChecked = userSelectedRoles.includes(role.id);
                  return (
                    <label 
                      key={role.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '12px',
                        padding: '12px',
                        borderRadius: '6px',
                        background: isChecked ? 'rgba(99, 102, 241, 0.08)' : 'rgba(255,255,255,0.02)',
                        border: isChecked ? '1px solid rgba(99, 102, 241, 0.4)' : '1px solid rgba(255,255,255,0.05)',
                        cursor: 'pointer',
                        userSelect: 'none'
                      }}
                    >
                      <input 
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => handleUserRoleToggle(role.id)}
                        style={{ width: '18px', height: '18px', accentColor: '#6366f1' }}
                      />
                      <div>
                        <div style={{ fontWeight: 600, fontSize: '0.9rem', color: isChecked ? '#a5b4fc' : '#f3f4f6' }}>{role.name}</div>
                        {role.description && <div style={{ fontSize: '0.75rem', color: 'rgba(255,255,255,0.5)', marginTop: '2px' }}>{role.description}</div>}
                      </div>
                    </label>
                  );
                })}
              </div>
            </div>

            <footer className="modal-footer" style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '16px', marginTop: '16px' }}>
              <button 
                type="button" 
                onClick={() => setShowUserModal(false)} 
                className="btn btn-secondary"
                disabled={savingUserRoles}
              >
                Cancel
              </button>
              <button 
                onClick={handleSaveUserRoles}
                className="btn btn-primary"
                disabled={savingUserRoles}
                id="btn-submit-user-roles"
              >
                {savingUserRoles ? (
                  <>
                    <Loader2 size={16} className="animate-spin" style={{ marginRight: 6 }} />
                    <span>Saving...</span>
                  </>
                ) : (
                  <span>Update User Roles</span>
                )}
              </button>
            </footer>
          </div>
        </div>
      )}

    </div>
  );
};
