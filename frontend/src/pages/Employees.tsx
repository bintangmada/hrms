import React, { useEffect, useState } from 'react';
import { Plus, Search, Edit2, Trash2, X, Link2, Sparkles } from 'lucide-react';
import { api, EmployeeDto, UserDto, EmployeeRequest } from '../services/api';

interface EmployeesProps {
  showToast: (text: string, type: 'success' | 'error') => void;
}

export const Employees: React.FC<EmployeesProps> = ({ showToast }) => {
  const [employees, setEmployees] = useState<EmployeeDto[]>([]);
  const [users, setUsers] = useState<UserDto[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Search & Filter
  const [searchTerm, setSearchTerm] = useState('');
  
  // Modals state
  const [showFormModal, setShowFormModal] = useState(false);
  const [formType, setFormType] = useState<'create' | 'edit'>('create');
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<number | null>(null);
  
  // Form values
  const [nik, setNik] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [position, setPosition] = useState('');
  const [department, setDepartment] = useState('');
  const [joinDate, setJoinDate] = useState('');
  const [salary, setSalary] = useState('');
  const [userId, setUserId] = useState<string>(''); // empty string means no user linked

  const fetchEmployees = async () => {
    try {
      const data = await api.employees.getAll();
      setEmployees(data);
    } catch (err: any) {
      showToast(err.message || 'Failed to fetch employee profiles', 'error');
    }
  };

  const fetchUsers = async () => {
    try {
      const data = await api.users.getAll();
      setUsers(data);
    } catch (err: any) {
      // In case user endpoint is restricted for non-admin, just fallback to empty list
      setUsers([]);
    }
  };

  useEffect(() => {
    const init = async () => {
      setLoading(true);
      await Promise.all([fetchEmployees(), fetchUsers()]);
      setLoading(false);
    };
    init();
  }, []);

  const openCreateModal = () => {
    setFormType('create');
    setSelectedEmployeeId(null);
    setNik('');
    setFirstName('');
    setLastName('');
    setEmail('');
    setPhone('');
    setPosition('');
    setDepartment('');
    setJoinDate(new Date().toISOString().split('T')[0]);
    setSalary('');
    setUserId('');
    setShowFormModal(true);
  };

  const openEditModal = (emp: EmployeeDto) => {
    setFormType('edit');
    setSelectedEmployeeId(emp.id);
    setNik(emp.nik);
    setFirstName(emp.firstName);
    setLastName(emp.lastName || '');
    setEmail(emp.email);
    setPhone(emp.phone || '');
    setPosition(emp.position || '');
    setDepartment(emp.department || '');
    setJoinDate(emp.joinDate || '');
    setSalary(emp.salary ? String(emp.salary) : '');
    setUserId(emp.userId ? String(emp.userId) : '');
    setShowFormModal(true);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!nik || !firstName || !email) {
      showToast('NIK, First Name, and Email are required', 'error');
      return;
    }

    const payload: EmployeeRequest = {
      nik,
      firstName,
      lastName: lastName || undefined,
      email,
      phone: phone || undefined,
      position: position || undefined,
      department: department || undefined,
      joinDate: joinDate || undefined,
      salary: salary ? parseFloat(salary) : undefined,
      userId: userId ? parseInt(userId) : undefined
    };

    try {
      if (formType === 'create') {
        await api.employees.create(payload);
        showToast('Employee profile created successfully!', 'success');
      } else if (formType === 'edit' && selectedEmployeeId !== null) {
        await api.employees.update(selectedEmployeeId, payload);
        showToast('Employee profile updated successfully!', 'success');
      }
      setShowFormModal(false);
      fetchEmployees();
    } catch (err: any) {
      showToast(err.message || 'Action failed', 'error');
    }
  };

  const handleDeleteEmployee = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this employee profile? This will not delete the linked user account.')) {
      try {
        const msg = await api.employees.delete(id);
        showToast(msg || 'Employee profile deleted successfully', 'success');
        fetchEmployees();
      } catch (err: any) {
        showToast(err.message || 'Failed to delete employee profile', 'error');
      }
    }
  };

  const filteredEmployees = employees.filter(emp => {
    const query = searchTerm.toLowerCase();
    return (
      emp.nik.toLowerCase().includes(query) ||
      emp.firstName.toLowerCase().includes(query) ||
      (emp.lastName && emp.lastName.toLowerCase().includes(query)) ||
      emp.email.toLowerCase().includes(query) ||
      (emp.position && emp.position.toLowerCase().includes(query)) ||
      (emp.department && emp.department.toLowerCase().includes(query))
    );
  });

  return (
    <div className="page-wrapper animate-fade-in">
      <header className="page-header">
        <div>
          <h1>Employee Management</h1>
          <p className="page-subtitle">Configure corporate employee directory profiles and link system access</p>
        </div>
        <button onClick={openCreateModal} className="btn btn-primary" id="btn-create-employee">
          <Plus size={18} />
          <span>New Employee</span>
        </button>
      </header>

      <section className="search-bar-wrapper glass-panel">
        <Search className="search-icon" size={18} />
        <input 
          type="text" 
          placeholder="Search by NIK, name, position or department..." 
          className="search-input"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </section>

      {loading ? (
        <div className="dashboard-loader">
          <div className="spinner spinner-large"></div>
          <p>Fetching active profiles...</p>
        </div>
      ) : (
        <div className="table-card glass-panel">
          <table className="data-table">
            <thead>
              <tr>
                <th>NIK</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Position</th>
                <th>Department</th>
                <th>Linked Account</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.map(emp => (
                <tr key={emp.id} className="table-row">
                  <td className="font-semibold">{emp.nik}</td>
                  <td>{emp.firstName} {emp.lastName}</td>
                  <td className="text-muted">{emp.email}</td>
                  <td>{emp.position || '-'}</td>
                  <td>{emp.department || '-'}</td>
                  <td>
                    {emp.userId ? (
                      <span className="badge badge-success">
                        <Link2 size={12} style={{ marginRight: 4 }} />
                        {emp.username}
                      </span>
                    ) : (
                      <span className="badge badge-warning">Unlinked</span>
                    )}
                  </td>
                  <td className="text-right actions-cell">
                    <button 
                      onClick={() => openEditModal(emp)} 
                      className="action-btn edit-btn" 
                      title="Edit Profile"
                      id={`btn-edit-${emp.id}`}
                    >
                      <Edit2 size={14} />
                    </button>
                    <button 
                      onClick={() => handleDeleteEmployee(emp.id)} 
                      className="action-btn delete-btn" 
                      title="Delete Profile"
                      id={`btn-delete-${emp.id}`}
                    >
                      <Trash2 size={14} />
                    </button>
                  </td>
                </tr>
              ))}
              {filteredEmployees.length === 0 && (
                <tr>
                  <td colSpan={7} className="no-data-cell">
                    No active employee profiles found matching search criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Form Modal */}
      {showFormModal && (
        <div className="modal-backdrop animate-fade-in">
          <div className="modal-container glass-panel animate-scale-in">
            <header className="modal-header">
              <div className="modal-title-group">
                <Sparkles size={20} className="sparkle-icon" />
                <h2>{formType === 'create' ? 'Create Employee Profile' : 'Edit Employee Profile'}</h2>
              </div>
              <button onClick={() => setShowFormModal(false)} className="modal-close-btn">
                <X size={18} />
              </button>
            </header>

            <form onSubmit={handleFormSubmit} className="modal-form">
              <div className="form-grid">
                <div className="form-group">
                  <label className="form-label" htmlFor="nik">NIK (Nomor Induk Karyawan)</label>
                  <input
                    id="nik"
                    type="text"
                    className="form-input"
                    placeholder="e.g. 19940812"
                    value={nik}
                    onChange={(e) => setNik(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="firstName">First Name</label>
                  <input
                    id="firstName"
                    type="text"
                    className="form-input"
                    placeholder="e.g. John"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="lastName">Last Name</label>
                  <input
                    id="lastName"
                    type="text"
                    className="form-input"
                    placeholder="e.g. Doe"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="email">Email</label>
                  <input
                    id="email"
                    type="email"
                    className="form-input"
                    placeholder="e.g. john.doe@company.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="phone">Phone Number</label>
                  <input
                    id="phone"
                    type="text"
                    className="form-input"
                    placeholder="e.g. 08123456789"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="position">Position</label>
                  <input
                    id="position"
                    type="text"
                    className="form-input"
                    placeholder="e.g. Senior Software Engineer"
                    value={position}
                    onChange={(e) => setPosition(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="department">Department</label>
                  <input
                    id="department"
                    type="text"
                    className="form-input"
                    placeholder="e.g. IT Department"
                    value={department}
                    onChange={(e) => setDepartment(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="joinDate">Join Date</label>
                  <input
                    id="joinDate"
                    type="date"
                    className="form-input"
                    value={joinDate}
                    onChange={(e) => setJoinDate(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="salary">Salary (IDR)</label>
                  <input
                    id="salary"
                    type="number"
                    className="form-input"
                    placeholder="e.g. 12000000"
                    value={salary}
                    onChange={(e) => setSalary(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="userId">Link System Account</label>
                  <select
                    id="userId"
                    className="form-input"
                    value={userId}
                    onChange={(e) => setUserId(e.target.value)}
                  >
                    <option value="">-- No Account Linked --</option>
                    {users.map(u => (
                      <option key={u.id} value={u.id}>
                        {u.username} ({u.email})
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <footer className="modal-footer">
                <button 
                  type="button" 
                  onClick={() => setShowFormModal(false)} 
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  className="btn btn-primary"
                  id="btn-submit-form"
                >
                  <span>{formType === 'create' ? 'Create' : 'Save Changes'}</span>
                </button>
              </footer>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
