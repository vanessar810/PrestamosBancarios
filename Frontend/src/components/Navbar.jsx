import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to={isAdmin() ? '/admin' : '/dashboard'}>
          Prestamos Bancarios
        </Link>
      </div>
      <div className="navbar-links">
        {isAdmin() ? (
          <>
            <Link to="/admin">Prestamos</Link>
            <span className="navbar-role">Admin</span>
          </>
        ) : (
          <>
            <Link to="/dashboard">Mis Prestamos</Link>
            <span className="navbar-role">Usuario</span>
          </>
        )}
        <span className="navbar-email">{user.email}</span>
        <button onClick={handleLogout} className="btn-logout">
          Cerrar Sesion
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
