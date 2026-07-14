import { useState, useEffect } from 'react';
import { getAllLoans, approveLoan, rejectLoan } from '../services/loanService';
import Navbar from '../components/Navbar';

const AdminDashboard = () => {
  const [loans, setLoans] = useState([]);
  const [filter, setFilter] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);

  const fetchLoans = async (status) => {
    setLoading(true);
    setError('');
    try {
      const data = await getAllLoans(status || undefined);
      setLoans(data);
    } catch (err) {
      setError('Error al cargar los prestamos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLoans(filter);
  }, [filter]);

  const handleApprove = async (id) => {
    setActionLoading(id);
    setError('');
    setSuccess('');
    try {
      await approveLoan(id);
      setSuccess(`Prestamo #${id} aprobado exitosamente`);
      fetchLoans(filter);
    } catch (err) {
      setError(err.response?.data?.message || 'Error al aprobar prestamo');
    } finally {
      setActionLoading(null);
    }
  };

  const handleReject = async (id) => {
    setActionLoading(id);
    setError('');
    setSuccess('');
    try {
      await rejectLoan(id);
      setSuccess(`Prestamo #${id} rechazado exitosamente`);
      fetchLoans(filter);
    } catch (err) {
      setError(err.response?.data?.message || 'Error al rechazar prestamo');
    } finally {
      setActionLoading(null);
    }
  };

  const getStatusBadge = (status) => {
    const badges = {
      PENDING: 'badge badge-pending',
      APPROVED: 'badge badge-approved',
      REJECTED: 'badge badge-rejected',
    };
    const labels = {
      PENDING: 'Pendiente',
      APPROVED: 'Aprobado',
      REJECTED: 'Rechazado',
    };
    return <span className={badges[status]}>{labels[status]}</span>;
  };

  return (
    <div>
      <Navbar />
      <div className="container">
        <h1>Panel de Administracion</h1>
        <p className="subtitle">Gestiona las solicitudes de prestamos</p>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="filters">
          <label>Filtrar por estado:</label>
          <select value={filter} onChange={(e) => setFilter(e.target.value)}>
            <option value="">Todos</option>
            <option value="PENDING">Pendientes</option>
            <option value="APPROVED">Aprobados</option>
            <option value="REJECTED">Rechazados</option>
          </select>
        </div>

        <div className="card">
          <h2>Solicitudes de Prestamos ({loans.length})</h2>

          {loading ? (
            <p className="loading-text">Cargando prestamos...</p>
          ) : loans.length === 0 ? (
            <p className="empty-text">No hay prestamos registrados</p>
          ) : (
            <div className="table-responsive">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Monto</th>
                    <th>Plazo</th>
                    <th>Estado</th>
                    <th>Fecha Solicitud</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {loans.map((loan) => (
                    <tr key={loan.id}>
                      <td>{loan.id}</td>
                      <td>{loan.userEmail}</td>
                      <td>${parseFloat(loan.amount).toFixed(2)}</td>
                      <td>{loan.termMonths} meses</td>
                      <td>{getStatusBadge(loan.status)}</td>
                      <td>{new Date(loan.createdAt).toLocaleDateString('es-ES')}</td>
                      <td>
                        {loan.status === 'PENDING' ? (
                          <div className="action-buttons">
                            <button
                              className="btn btn-success btn-sm"
                              onClick={() => handleApprove(loan.id)}
                              disabled={actionLoading === loan.id}
                            >
                              {actionLoading === loan.id ? '...' : 'Aprobar'}
                            </button>
                            <button
                              className="btn btn-danger btn-sm"
                              onClick={() => handleReject(loan.id)}
                              disabled={actionLoading === loan.id}
                            >
                              {actionLoading === loan.id ? '...' : 'Rechazar'}
                            </button>
                          </div>
                        ) : (
                          <span className="text-muted">
                            {loan.approvedAt
                              ? new Date(loan.approvedAt).toLocaleDateString('es-ES')
                              : '-'}
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
