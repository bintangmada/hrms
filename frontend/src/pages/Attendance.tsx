import React, { useState } from 'react';
import { Clock, CheckCircle, AlertCircle, Calendar, ArrowUpRight, ArrowDownLeft, MapPin } from 'lucide-react';

interface AttendanceProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const Attendance: React.FC<AttendanceProps> = ({ showToast }) => {
  const [isCheckedIn, setIsCheckedIn] = useState(false);
  const [checkInTime, setCheckInTime] = useState<string | null>(null);
  const location = 'HQ - Jakarta Office';
  
  const [logs, setLogs] = useState([
    { date: '2026-07-10', checkIn: '08:52 AM', checkOut: '05:04 PM', status: 'Present', loc: 'HQ - Jakarta Office' },
    { date: '2026-07-09', checkIn: '08:45 AM', checkOut: '05:01 PM', status: 'Present', loc: 'HQ - Jakarta Office' },
    { date: '2026-07-08', checkIn: '08:58 AM', checkOut: '05:15 PM', status: 'Present', loc: 'HQ - Jakarta Office' },
    { date: '2026-07-07', checkIn: '09:05 AM', checkOut: '05:00 PM', status: 'Late', loc: 'HQ - Jakarta Office' },
    { date: '2026-07-06', checkIn: '08:50 AM', checkOut: '05:02 PM', status: 'Present', loc: 'HQ - Jakarta Office' }
  ]);

  const handleCheckIn = () => {
    const now = new Date();
    const timeStr = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    setCheckInTime(timeStr);
    setIsCheckedIn(true);
    showToast('Successfully clocked in!', 'success');
    
    // Prepend to logs
    const dateStr = now.toISOString().split('T')[0];
    setLogs(prev => [
      { date: dateStr, checkIn: timeStr, checkOut: '--', status: 'Active', loc: location },
      ...prev
    ]);
  };

  const handleCheckOut = () => {
    const now = new Date();
    const timeStr = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    setIsCheckedIn(false);
    showToast('Successfully clocked out!', 'success');

    // Update today's log in list
    const dateStr = now.toISOString().split('T')[0];
    setLogs(prev => 
      prev.map(log => 
        log.date === dateStr ? { ...log, checkOut: timeStr, status: 'Present' } : log
      )
    );
  };

  return (
    <div className="page-wrapper animate-fade-in">
      <header className="page-header">
        <div>
          <h1>Attendance Tracking</h1>
          <p className="page-subtitle">Track your daily working hours, manage geofencing verification, and review presence history</p>
        </div>
      </header>

      <section className="stats-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px', marginBottom: '24px' }}>
        
        {/* CHECK-IN CARD */}
        <div className="stat-card glass-panel-interactive" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 style={{ margin: 0 }}>Action Control</h3>
            <Clock size={20} className={isCheckedIn ? 'text-success animate-pulse' : 'text-muted'} />
          </div>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: 'rgba(255,255,255,0.6)' }}>
              <MapPin size={14} />
              <span>Current Geofence: <strong>{location}</strong></span>
            </div>
            
            <div style={{ display: 'flex', gap: '10px', marginTop: '4px' }}>
              {!isCheckedIn && !checkInTime ? (
                <button onClick={handleCheckIn} className="btn btn-primary" style={{ flex: 1, padding: '12px' }}>
                  <ArrowUpRight size={18} style={{ marginRight: 6 }} />
                  Clock In
                </button>
              ) : isCheckedIn ? (
                <button onClick={handleCheckOut} className="btn btn-danger" style={{ flex: 1, padding: '12px', background: '#ef4444', borderColor: '#ef4444', color: '#fff' }}>
                  <ArrowDownLeft size={18} style={{ marginRight: 6 }} />
                  Clock Out
                </button>
              ) : (
                <button disabled className="btn btn-secondary" style={{ flex: 1, padding: '12px', opacity: 0.7 }}>
                  Shift Completed
                </button>
              )}
            </div>
          </div>
        </div>

        {/* STATS 1 */}
        <div className="stat-card glass-panel-interactive">
          <div className="stat-icon-wrapper green">
            <CheckCircle size={24} />
          </div>
          <div className="stat-info">
            <h3>Punctual Rate</h3>
            <p className="stat-value">92%</p>
            <span className="stat-label">This month average</span>
          </div>
        </div>

        {/* STATS 2 */}
        <div className="stat-card glass-panel-interactive">
          <div className="stat-icon-wrapper amber">
            <AlertCircle size={24} />
          </div>
          <div className="stat-info">
            <h3>Late Check-ins</h3>
            <p className="stat-value">1</p>
            <span className="stat-label">Out of 20 active days</span>
          </div>
        </div>
      </section>

      {/* LOGS TABLE */}
      <div className="table-card glass-panel">
        <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.1)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={{ margin: 0, fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Calendar size={18} className="text-primary" />
            Recent Attendance Logs
          </h2>
        </div>

        <table className="data-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Location</th>
              <th>Clock In</th>
              <th>Clock Out</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log, idx) => (
              <tr key={idx} className="table-row">
                <td className="font-semibold">{log.date}</td>
                <td className="text-muted">{log.loc}</td>
                <td>{log.checkIn}</td>
                <td>{log.checkOut}</td>
                <td>
                  <span className={`badge ${log.status === 'Present' ? 'badge-success' : log.status === 'Late' ? 'badge-warning' : 'badge-info'}`}>
                    {log.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
