import React, { useEffect, useState } from 'react';
import { Users, UserCheck, Briefcase } from 'lucide-react';
import { api, EmployeeDto, getCurrentUser } from '../services/api';

interface DashboardProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ showToast }) => {
  const [employees, setEmployees] = useState<EmployeeDto[]>([]);
  const [employeeProfile, setEmployeeProfile] = useState<EmployeeDto | null>(null);
  const [noProfileLinked, setNoProfileLinked] = useState<boolean>(false);
  const [loading, setLoading] = useState(true);

  const user = getCurrentUser();
  const isAdmin = user && (user.role.includes('ROLE_ADMIN') || user.role.includes('ROLE_SUPER_ADMIN'));

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (isAdmin) {
          const data = await api.employees.getAll();
          setEmployees(data);
        } else if (user) {
          try {
            const data = await api.employees.getByUserId(user.id);
            setEmployeeProfile(data);
          } catch (err: any) {
            // User exists but has no linked employee profile yet
            setNoProfileLinked(true);
          }
        }
      } catch (err: any) {
        showToast(err.message || 'Failed to fetch dashboard metrics', 'error');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [isAdmin, user, showToast]);

  const totalEmployees = employees.length;
  const linkedUsersCount = employees.filter(e => e.userId != null).length;
  const unlinkedUsersCount = totalEmployees - linkedUsersCount;

  // Department counts
  const deptCounts: { [key: string]: number } = {};
  employees.forEach(e => {
    const dept = e.department || 'Unassigned';
    deptCounts[dept] = (deptCounts[dept] || 0) + 1;
  });

  const activeDepartments = Object.keys(deptCounts).length;

  return (
    <div className="page-wrapper animate-fade-in">
      <header className="page-header">
        <div>
          <h1>HRMS Dashboard</h1>
          <p className="page-subtitle">
            {isAdmin 
              ? 'Real-time indicators, stats, and profile connections' 
              : 'Your employee portal and profile summary'}
          </p>
        </div>
      </header>

      {loading ? (
        <div className="dashboard-loader">
          <div className="spinner spinner-large"></div>
          <p>{isAdmin ? 'Compiling stats...' : 'Loading profile...'}</p>
        </div>
      ) : isAdmin ? (
        <>
          <section className="stats-grid">
            <div className="stat-card glass-panel-interactive">
              <div className="stat-icon-wrapper blue">
                <Users size={24} />
              </div>
              <div className="stat-info">
                <h3>Total Employees</h3>
                <p className="stat-value">{totalEmployees}</p>
                <span className="stat-label">Registered in database</span>
              </div>
            </div>

            <div className="stat-card glass-panel-interactive">
              <div className="stat-icon-wrapper green">
                <UserCheck size={24} />
              </div>
              <div className="stat-info">
                <h3>Linked Accounts</h3>
                <p className="stat-value">{linkedUsersCount}</p>
                <span className="stat-label">{unlinkedUsersCount} profiles unlinked</span>
              </div>
            </div>

            <div className="stat-card glass-panel-interactive">
              <div className="stat-icon-wrapper amber">
                <Briefcase size={24} />
              </div>
              <div className="stat-info">
                <h3>Departments</h3>
                <p className="stat-value">{activeDepartments}</p>
                <span className="stat-label">Active business units</span>
              </div>
            </div>
          </section>

          <section className="dashboard-charts-section">
            <div className="chart-card glass-panel">
              <h2>Department Breakdown</h2>
              <div className="dept-list">
                {Object.entries(deptCounts).map(([dept, count]) => {
                  const percentage = totalEmployees > 0 ? (count / totalEmployees) * 100 : 0;
                  return (
                    <div key={dept} className="dept-item">
                      <div className="dept-info">
                        <span className="dept-name">{dept}</span>
                        <span className="dept-count">{count} {count > 1 ? 'employees' : 'employee'}</span>
                      </div>
                      <div className="dept-bar-track">
                        <div 
                          className="dept-bar-fill" 
                          style={{ width: `${percentage}%` }}
                        ></div>
                      </div>
                    </div>
                  );
                })}
                {employees.length === 0 && (
                  <p className="text-muted">No department data to display.</p>
                )}
              </div>
            </div>

            <div className="chart-card glass-panel text-center">
              <h2>Profile Status Alignment</h2>
              <div className="radial-chart-wrapper">
                <svg viewBox="0 0 100 100" className="radial-chart">
                  {/* Empty base circle */}
                  <circle cx="50" cy="50" r="40" className="radial-circle-bg" />
                  
                  {/* Progress circle */}
                  {totalEmployees > 0 && (
                    <circle 
                      cx="50" 
                      cy="50" 
                      r="40" 
                      className="radial-circle-fg"
                      strokeDasharray={`${(linkedUsersCount / totalEmployees) * 251.2} 251.2`}
                    />
                  )}
                </svg>
                <div className="radial-chart-label">
                  <span className="percentage">
                    {totalEmployees > 0 ? Math.round((linkedUsersCount / totalEmployees) * 100) : 0}%
                  </span>
                  <span className="label">Linked Accounts</span>
                </div>
              </div>
              <p className="chart-description text-muted">
                Measures percentage of employees configured with an active user login account.
              </p>
            </div>
          </section>
        </>
      ) : noProfileLinked ? (
        <div className="profile-card">
          <h2 className="profile-title">Profile Connection Pending</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', lineHeight: '1.6' }}>
            Your staff account has been registered successfully, but it has not been linked to a corporate employee profile yet.
          </p>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '12px', lineHeight: '1.6' }}>
            Please contact your HR administrator to connect your username <strong>{user?.username}</strong> to your employee record.
          </p>
        </div>
      ) : employeeProfile ? (
        <div className="profile-card">
          <h2 className="profile-title">My Employee Profile</h2>
          <div className="profile-grid">
            <div className="profile-field">
              <label>NIK</label>
              <p>{employeeProfile.nik}</p>
            </div>
            <div className="profile-field">
              <label>Full Name</label>
              <p>{employeeProfile.firstName} {employeeProfile.lastName}</p>
            </div>
            <div className="profile-field">
              <label>Email</label>
              <p>{employeeProfile.email}</p>
            </div>
            <div className="profile-field">
              <label>Phone</label>
              <p>{employeeProfile.phone || '-'}</p>
            </div>
            <div className="profile-field">
              <label>Position</label>
              <p>{employeeProfile.position || '-'}</p>
            </div>
            <div className="profile-field">
              <label>Department</label>
              <p>{employeeProfile.department || '-'}</p>
            </div>
            <div className="profile-field">
              <label>Join Date</label>
              <p>{employeeProfile.joinDate || '-'}</p>
            </div>
            <div className="profile-field">
              <label>Salary</label>
              <p>
                {employeeProfile.salary ? `Rp ${employeeProfile.salary.toLocaleString('id-ID')}` : '-'}
              </p>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
};
