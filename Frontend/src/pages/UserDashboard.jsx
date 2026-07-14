import { useState, useEffect } from 'react';
import { requestLoan, getMyLoans } from '../services/loanService';
import Navbar from '../components/Navbar';

const UserDashboard = () => {
  const [loans, setLoans] = useState([]);
  const [amount, setAmount] = useState('');
  const [termMonths, setTermMonths] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);

  const fetchLoans = async () => {
    try {
      const data = await getMyLoans();
      setLoans(data);
    } catch (err) {
      setError('Error al cargar los prestamos');
    } finally {
      setFetching(false);
    }
  };

  useEffect(() => {
    fetchLoans();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const amountNum = parseFloat(amount);
    if (isNaN(amountNum) || amountNum < 100) {
      setError('El monto minimo es $100.00');
      return;
    }

    const termNum = parseInt(termMonths);
    if (isNaN(termNum) || termNum < 1) {
      setError('El plazo debe ser al menos 1 mes');
      return;
    }

    setLoading(true);

    try {
      await requestLoan(amountNum, termNum);
      setSuccess('Solicitud de prestamo enviada exitosamente');
      setAmount('');
      setTermMonths('');
      fetchLoans();
    } catch (err) {
      setError(err.response?.data?.message || 'Error al solicitar prestamo');
    } finally {
      setLoading(false);
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
        <h1>Mis Prestamos</h1>

        <div className="content-grid">
          <div className="card">
            <h2>Solicitar Nuevo Prestamo</h2>

            {error && <div className="alert alert-error">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="amount">Monto ($)</label>
                <input
                  id="amount"
                  type="number"
                  step="0.01"
                  min="100"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  placeholder="Ej: 5000.00"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="term">Plazo (meses)</label>
                <input
                  id="term"
                  type="number"
                  min="1"
                  value={termMonths}
                  onChange={(e) => setTermMonths(e.target.value)}
                  placeholder="Ej: 12"
                  required
                />
              </div>

              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Enviando...' : 'Solicitar Prestamo'}
              </button>
            </form>
          </div>

          <div className="card">
            <h2>Historial de Prestamos</h2>

            {fetching ? (
              <p className="loading-text">Cargando prestamos...</p>
            ) : loans.length === 0 ? (
              <p className="empty-text">No tienes prestamos registrados</p>
            ) : (
              <div className="table-responsive">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Monto</th>
                      <th>Plazo</th>
                      <th>Estado</th>
                      <th>Fecha Solicitud</th>
                      <th>Fecha Respuesta</th>
                    </tr>
                  </thead>
                  <tbody>
                    {loans.map((loan) => (
                      <tr key={loan.id}>
                        <td>{loan.id}</td>
                        <td>${parseFloat(loan.amount).toFixed(2)}</td>
                        <td>{loan.termMonths} meses</td>
                        <td>{getStatusBadge(loan.status)}</td>
                        <td>{new Date(loan.createdAt).toLocaleDateString('es-ES')}</td>
                        <td>
                          {loan.approvedAt
                            ? new Date(loan.approvedAt).toLocaleDateString('es-ES')
                            : '-'}
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
    </div>
  );
};

export default UserDashboard;
