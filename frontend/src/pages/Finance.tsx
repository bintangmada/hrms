import { DollarSign, Download, FileText, CheckCircle, TrendingUp } from 'lucide-react';

interface FinanceProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const Finance: React.FC<FinanceProps> = ({ showToast }) => {
  const payslips = [
    { period: 'June 2026', basicSalary: 12000000, allowances: 1500000, deductions: 500000, status: 'Paid', date: '2026-06-25' },
    { period: 'May 2026', basicSalary: 12000000, allowances: 1500000, deductions: 500000, status: 'Paid', date: '2026-05-25' },
    { period: 'April 2026', basicSalary: 11500000, allowances: 1200000, deductions: 450000, status: 'Paid', date: '2026-04-25' },
    { period: 'March 2026', basicSalary: 11500000, allowances: 1200000, deductions: 450000, status: 'Paid', date: '2026-03-25' }
  ];

  const handleDownload = (period: string) => {
    showToast(`Downloading payslip for ${period}...`, 'success');
  };

  return (
    <div className="page-wrapper animate-fade-in">
      <header className="page-header">
        <div>
          <h1>Finance & Payroll</h1>
          <p className="page-subtitle">View payroll breakdowns, retrieve monthly payslips, and monitor benefit statements</p>
        </div>
      </header>

      <section className="stats-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px', marginBottom: '24px' }}>
        
        {/* STATS 1 */}
        <div className="stat-card glass-panel-interactive">
          <div className="stat-icon-wrapper blue">
            <DollarSign size={24} />
          </div>
          <div className="stat-info">
            <h3>Net Salary (June)</h3>
            <p className="stat-value">Rp 13.000.000</p>
            <span className="stat-label">Basic + Allowances - Tax/BPJS</span>
          </div>
        </div>

        {/* STATS 2 */}
        <div className="stat-card glass-panel-interactive">
          <div className="stat-icon-wrapper green">
            <TrendingUp size={24} />
          </div>
          <div className="stat-info">
            <h3>Annual Growth</h3>
            <p className="stat-value">+8.4%</p>
            <span className="stat-label">Salary increment index</span>
          </div>
        </div>

        {/* STATS 3 */}
        <div className="stat-card glass-panel-interactive">
          <div className="stat-icon-wrapper amber">
            <CheckCircle size={24} />
          </div>
          <div className="stat-info">
            <h3>Tax Status</h3>
            <p className="stat-value">PTKP K/0</p>
            <span className="stat-label">Verified tax class</span>
          </div>
        </div>
      </section>

      {/* PAYROLL LIST */}
      <div className="table-card glass-panel">
        <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.1)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={{ margin: 0, fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <FileText size={18} className="text-primary" />
            Payslip Archives
          </h2>
        </div>

        <table className="data-table">
          <thead>
            <tr>
              <th>Pay Period</th>
              <th>Basic Salary</th>
              <th>Allowances</th>
              <th>Deductions</th>
              <th>Net Take-Home Pay</th>
              <th>Status</th>
              <th className="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {payslips.map((slip, idx) => {
              const thp = slip.basicSalary + slip.allowances - slip.deductions;
              return (
                <tr key={idx} className="table-row">
                  <td className="font-semibold">{slip.period}</td>
                  <td>Rp {slip.basicSalary.toLocaleString('id-ID')}</td>
                  <td>Rp {slip.allowances.toLocaleString('id-ID')}</td>
                  <td className="text-error" style={{ color: '#ef4444' }}>- Rp {slip.deductions.toLocaleString('id-ID')}</td>
                  <td className="font-semibold text-primary" style={{ color: '#818cf8' }}>
                    Rp {thp.toLocaleString('id-ID')}
                  </td>
                  <td>
                    <span className="badge badge-success">
                      {slip.status}
                    </span>
                  </td>
                  <td className="text-right">
                    <button 
                      onClick={() => handleDownload(slip.period)}
                      className="btn btn-secondary btn-sm"
                      style={{ padding: '6px 12px', fontSize: '0.8rem', display: 'inline-flex', alignItems: 'center', gap: 6 }}
                    >
                      <Download size={14} />
                      <span>Download PDF</span>
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};
