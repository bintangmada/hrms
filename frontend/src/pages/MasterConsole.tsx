import React, { useEffect, useState } from 'react';
import { 
  Terminal, Cpu, HardDrive, Shield, Users, RefreshCw, 
  Activity, Settings, Mail, Server, Database, Loader2, AlertCircle, CheckCircle
} from 'lucide-react';
import { api, SystemInfoResponse, UserDto } from '../services/api';

interface MasterConsoleProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const MasterConsole: React.FC<MasterConsoleProps> = ({ showToast }) => {
  const [activeTab, setActiveTab] = useState<'diagnostics' | 'users' | 'configs'>('diagnostics');
  const [systemInfo, setSystemInfo] = useState<SystemInfoResponse | null>(null);
  const [users, setUsers] = useState<UserDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [refreshing, setRefreshing] = useState<boolean>(false);

  const fetchData = async (isSilent = false) => {
    try {
      if (!isSilent) setLoading(true);
      else setRefreshing(true);

      const [infoData, usersData] = await Promise.all([
        api.system.getSystemInfo(),
        api.users.getAll()
      ]);

      setSystemInfo(infoData);
      setUsers(usersData);
    } catch (err: any) {
      showToast(err.message || 'Failed to fetch platform metrics', 'error');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const formatBytes = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getMemoryUsagePercent = (info: SystemInfoResponse) => {
    const used = info.totalMemoryBytes - info.freeMemoryBytes;
    return Math.round((used / info.maxMemoryBytes) * 100);
  };

  const getUsedMemory = (info: SystemInfoResponse) => {
    return info.totalMemoryBytes - info.freeMemoryBytes;
  };

  return (
    <div className="page-wrapper animate-fade-in">
      <header className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <div>
          <h1 style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <Terminal size={28} className="text-primary" />
            Master Control Center
          </h1>
          <p className="page-subtitle">Unified diagnostic board for system architecture, active users directory, and configuration validation.</p>
        </div>
        <button 
          onClick={() => fetchData(true)} 
          disabled={loading || refreshing}
          className="btn btn-secondary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 16px' }}
        >
          {refreshing ? (
            <Loader2 size={16} className="animate-spin" />
          ) : (
            <RefreshCw size={16} />
          )}
          <span>Refresh Console</span>
        </button>
      </header>

      {loading ? (
        <div className="dashboard-loader">
          <div className="spinner spinner-large"></div>
          <p>Analyzing system infrastructure...</p>
        </div>
      ) : !systemInfo ? (
        <div className="card glass-panel text-center" style={{ padding: '40px' }}>
          <AlertCircle size={48} className="text-error" style={{ margin: '0 auto 16px' }} />
          <h3>System Communication Failure</h3>
          <p>Could not retrieve diagnostic payload from backend server.</p>
        </div>
      ) : (
        <>
          {/* STATS MATRIX SUMMARY */}
          <div className="stats-grid">
            {/* DB Health Status Card */}
            <div className="stat-card glass-panel animate-scale-in">
              <div className={`stat-icon-wrapper ${systemInfo.dbStatus.startsWith('CONNECTED') ? 'green' : 'amber'}`}>
                <Database size={24} />
              </div>
              <div className="stat-info">
                <h3>Database Health</h3>
                <div className="stat-value" style={{ fontSize: '1.5rem', marginTop: '6px' }}>
                  {systemInfo.dbStatus.startsWith('CONNECTED') ? 'ONLINE' : 'OFFLINE'}
                </div>
                <span className="stat-label">PostgreSQL on host 172.17.0.1</span>
              </div>
            </div>

            {/* Memory Card */}
            <div className="stat-card glass-panel animate-scale-in" style={{ animationDelay: '0.1s' }}>
              <div className="stat-icon-wrapper blue">
                <HardDrive size={24} />
              </div>
              <div className="stat-info" style={{ flex: 1 }}>
                <h3>JVM Heap Memory</h3>
                <div className="stat-value">
                  {getMemoryUsagePercent(systemInfo)}%
                </div>
                <div className="dept-bar-track" style={{ height: '5px', margin: '8px 0', background: 'rgba(255,255,255,0.06)' }}>
                  <div 
                    className="dept-bar-fill" 
                    style={{ width: `${getMemoryUsagePercent(systemInfo)}%`, background: 'var(--primary-gradient)' }}
                  ></div>
                </div>
                <span className="stat-label">
                  {formatBytes(getUsedMemory(systemInfo))} used of {formatBytes(systemInfo.maxMemoryBytes)}
                </span>
              </div>
            </div>

            {/* CPU & Processing Card */}
            <div className="stat-card glass-panel animate-scale-in" style={{ animationDelay: '0.2s' }}>
              <div className="stat-icon-wrapper amber">
                <Cpu size={24} />
              </div>
              <div className="stat-info">
                <h3>Compute Power</h3>
                <div className="stat-value">
                  {systemInfo.cpuCores} Cores
                </div>
                <span className="stat-label">{systemInfo.activeThreads} running Java threads</span>
              </div>
            </div>

            {/* Total Users Card */}
            <div className="stat-card glass-panel animate-scale-in" style={{ animationDelay: '0.3s' }}>
              <div className="stat-icon-wrapper blue">
                <Users size={24} />
              </div>
              <div className="stat-info">
                <h3>System Accounts</h3>
                <div className="stat-value">
                  {systemInfo.activeUsersCount} Users
                </div>
                <span className="stat-label">{systemInfo.activeRolesCount} authorization roles</span>
              </div>
            </div>
          </div>

          {/* TAB SELECTION */}
          <div className="tab-navigation-wrapper glass-panel" style={{ padding: '6px', marginBottom: '24px', display: 'inline-flex', gap: '8px' }}>
            <button 
              onClick={() => setActiveTab('diagnostics')} 
              className={`tab-btn btn ${activeTab === 'diagnostics' ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '8px 16px', border: 'none' }}
            >
              <Activity size={16} style={{ marginRight: 8 }} />
              <span>Platform Diagnostics</span>
            </button>
            <button 
              onClick={() => setActiveTab('users')} 
              className={`tab-btn btn ${activeTab === 'users' ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '8px 16px', border: 'none' }}
            >
              <Users size={16} style={{ marginRight: 8 }} />
              <span>User Accounts Audit</span>
            </button>
            <button 
              onClick={() => setActiveTab('configs')} 
              className={`tab-btn btn ${activeTab === 'configs' ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '8px 16px', border: 'none' }}
            >
              <Settings size={16} style={{ marginRight: 8 }} />
              <span>Server Properties</span>
            </button>
          </div>

          {/* DIAGNOSTICS TAB PANEL */}
          {activeTab === 'diagnostics' && (
            <div className="dashboard-charts-section" style={{ gridTemplateColumns: '1.5fr 1fr', gap: '24px' }}>
              {/* JVM & Environment Details */}
              <div className="card glass-panel chart-card" style={{ padding: '24px' }}>
                <h2 style={{ display: 'flex', alignItems: 'center', gap: 8, margin: '0 0 20px 0' }}>
                  <Server size={18} className="text-primary" />
                  JVM & OS Specification
                </h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Operating System Name</span>
                    <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{systemInfo.osName}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>OS Version</span>
                    <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{systemInfo.osVersion}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Architecture</span>
                    <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{systemInfo.osArch}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Java (JVM) Version</span>
                    <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{systemInfo.jvmVersion}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Java Vendor</span>
                    <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{systemInfo.jvmVendor}</span>
                  </div>
                </div>
              </div>

              {/* Memory Diagnostics */}
              <div className="card glass-panel chart-card" style={{ padding: '24px' }}>
                <h2 style={{ display: 'flex', alignItems: 'center', gap: 8, margin: '0 0 20px 0' }}>
                  <HardDrive size={18} className="text-primary" />
                  JVM Memory Breakdown
                </h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Max Memory Allocation</span>
                    <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{formatBytes(systemInfo.maxMemoryBytes)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Total Active Heap Size</span>
                    <span style={{ fontWeight: 600, color: '#f3f4f6' }}>{formatBytes(systemInfo.totalMemoryBytes)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Unallocated Free Heap</span>
                    <span style={{ fontWeight: 600, color: '#10b981' }}>{formatBytes(systemInfo.freeMemoryBytes)}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '10px' }}>
                    <span style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>Used Heap Memory</span>
                    <span style={{ fontWeight: 600, color: '#ef4444' }}>{formatBytes(getUsedMemory(systemInfo))}</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* USER ACCOUNTS AUDIT TAB PANEL */}
          {activeTab === 'users' && (
            <div className="table-card glass-panel animate-fade-in">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Email Address</th>
                    <th>Clearance Role Badge</th>
                    <th>Verification Status</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map(u => (
                    <tr key={u.id} className="table-row">
                      <td style={{ color: 'rgba(255,255,255,0.4)', fontFamily: 'monospace' }}>#{u.id}</td>
                      <td className="font-semibold" style={{ color: u.username === 'masteradmin' ? '#818cf8' : '#f3f4f6' }}>
                        {u.username}
                        {u.username === 'masteradmin' && (
                          <span className="badge badge-success" style={{ fontSize: '0.6rem', padding: '1px 5px', marginLeft: 8 }}>Owner</span>
                        )}
                      </td>
                      <td className="text-muted">{u.email}</td>
                      <td>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                          {u.role.split(',').map((r, idx) => (
                            <span 
                              key={idx} 
                              className={`badge ${
                                r.trim() === 'ROLE_SUPER_ADMIN' ? 'badge-success' : 
                                r.trim() === 'ROLE_ADMIN' ? 'badge-warning' : 'badge-info'
                              }`} 
                              style={{ fontSize: '0.75rem' }}
                            >
                              {r.trim().replace('ROLE_', '')}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem' }}>
                          <CheckCircle size={14} className="text-green" style={{ color: '#10b981' }} />
                          <span style={{ color: '#10b981' }}>Pre-verified</span>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* CONFIGS TAB PANEL */}
          {activeTab === 'configs' && (
            <div className="card glass-panel" style={{ padding: '24px' }}>
              <h2 style={{ display: 'flex', alignItems: 'center', gap: 8, margin: '0 0 20px 0' }}>
                <Settings size={18} className="text-primary" />
                Active JVM Configuration Properties
              </h2>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '20px' }}>
                {/* Network & Ports */}
                <div style={{ border: '1px solid rgba(255,255,255,0.06)', borderRadius: '8px', padding: '16px', background: 'rgba(255,255,255,0.01)' }}>
                  <h4 style={{ margin: '0 0 12px 0', display: 'flex', alignItems: 'center', gap: '6px', color: '#a5b4fc' }}>
                    <Server size={14} /> Network
                  </h4>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '0.85rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: 'rgba(255,255,255,0.5)' }}>Server Port (Local):</span>
                      <span style={{ fontFamily: 'monospace' }}>{systemInfo.serverPort}</span>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: 'rgba(255,255,255,0.5)' }}>Context Prefix:</span>
                      <span style={{ fontFamily: 'monospace' }}>/api/v1</span>
                    </div>
                  </div>
                </div>

                {/* JWT Settings */}
                <div style={{ border: '1px solid rgba(255,255,255,0.06)', borderRadius: '8px', padding: '16px', background: 'rgba(255,255,255,0.01)' }}>
                  <h4 style={{ margin: '0 0 12px 0', display: 'flex', alignItems: 'center', gap: '6px', color: '#a5b4fc' }}>
                    <Shield size={14} /> Token Authentication
                  </h4>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '0.85rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: 'rgba(255,255,255,0.5)' }}>Secret Algorithm:</span>
                      <span style={{ fontFamily: 'monospace' }}>HMAC256</span>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: 'rgba(255,255,255,0.5)' }}>Expiration Limit:</span>
                      <span>{(systemInfo.jwtExpirationMs / 3600000).toFixed(1)} Hours ({systemInfo.jwtExpirationMs} ms)</span>
                    </div>
                  </div>
                </div>

                {/* Mail Server */}
                <div style={{ border: '1px solid rgba(255,255,255,0.06)', borderRadius: '8px', padding: '16px', background: 'rgba(255,255,255,0.01)' }}>
                  <h4 style={{ margin: '0 0 12px 0', display: 'flex', alignItems: 'center', gap: '6px', color: '#a5b4fc' }}>
                    <Mail size={14} /> Notification Engine (SMTP)
                  </h4>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '0.85rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: 'rgba(255,255,255,0.5)' }}>SMTP Host:</span>
                      <span style={{ fontFamily: 'monospace' }}>smtp.gmail.com</span>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ color: 'rgba(255,255,255,0.5)' }}>Auth Protocol:</span>
                      <span>STARTTLS (TLS Enabled)</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};
