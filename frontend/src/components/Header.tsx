import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import '../styles/Header.css';

const Header: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <header className="header">
      <div className="header-container">
        <div className="logo" onClick={() => navigate('/')}>
          ShoutX
        </div>

        <nav className="nav-menu">
          <a href="/" className={location.pathname === '/' ? 'active' : ''}>Home</a>
          {isAuthenticated && (
            <>
              <a href="/dashboard" className={location.pathname === '/dashboard' ? 'active' : ''}>Dashboard</a>
              <a href="/notifications" className={location.pathname === '/notifications' ? 'active' : ''}>Notifications</a>
              <a href="/payments" className={location.pathname === '/payments' ? 'active' : ''}>Payments</a>
            </>
          )}
          <a href="/terms">Terms</a>
          <a href="/privacy">Privacy</a>
        </nav>

        <div className="header-actions">
          {isAuthenticated && user ? (
            <div className="user-menu">
              <div className="user-info">
                <img src={user.profilePicture} alt={user.username} className="user-avatar" />
                <span>{user.username}</span>
              </div>
              <button className="btn-small" onClick={() => { logout(); navigate('/'); }}>
                Logout
              </button>
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