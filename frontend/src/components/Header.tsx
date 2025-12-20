import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import '../styles/Header.css';

const Header: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [showUserDropdown, setShowUserDropdown] = useState(false);

  return (
    <header className="header">
      <div className="header-container">
        <div className="logo" onClick={() => navigate('/')}>
          ShoutX
        </div>

        <nav className="nav-menu">
          <a href="/" className={location.pathname === '/' ? 'active' : ''}>
            Home
          </a>
          {isAuthenticated && (
            <>
              <a href="/dashboard" className={location.pathname === '/dashboard' ? 'active' : ''}>
                Dashboard
              </a>
              <a href="/notifications" className={location.pathname === '/notifications' ? 'active' : ''}>
                Notifications
              </a>
              <a href="/payments" className={location.pathname === '/payments' ? 'active' : ''}>
                Payments
              </a>
            </>
          )}
          <a href="/terms">Terms</a>
          <a href="/privacy">Privacy</a>
        </nav>

        <div className="header-actions">
          {isAuthenticated && user ? (
            <div className="user-menu">
              <div
                className="user-info"
                onClick={() => setShowUserDropdown(!showUserDropdown)}
              >
                <img src={user.profilePicture} alt={user.username} className="user-avatar" />
                <span>{user.username}</span>
              </div>

              {/* Dropdown Menu */}
              {showUserDropdown && (
                <div className="user-dropdown-menu">
                  <button
                    className="dropdown-item"
                    onClick={() => {
                      navigate('/profile/me');
                      setShowUserDropdown(false);
                    }}
                  >
                    👤 My Profile
                  </button>
                  <button
                    className="dropdown-item"
                    onClick={() => {
                      navigate('/dashboard');
                      setShowUserDropdown(false);
                    }}
                  >
                    📊 Dashboard
                  </button>
                  <button
                    className="dropdown-item"
                    onClick={() => {
                      navigate('/payments');
                      setShowUserDropdown(false);
                    }}
                  >
                    💳 Payments
                  </button>
                  <hr className="dropdown-divider" />
                  <button
                    className="dropdown-item logout"
                    onClick={() => {
                      logout();
                      navigate('/');
                      setShowUserDropdown(false);
                    }}
                  >
                    🚪 Logout
                  </button>
                </div>
              )}
            </div>
          ) : (
            <button className="btn btn-primary btn-small" onClick={() => navigate('/login')}>
              Login
            </button>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
