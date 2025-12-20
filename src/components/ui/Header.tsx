import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Bell, LayoutDashboard, ChevronDown } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import LoginModal from './LoginModal';
import PricingModal from './PricingModal';

const Header = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const [showDropdown, setShowDropdown] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showPricingModal, setShowPricingModal] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <>
      <header className="border-b bg-white sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <Link to={isAuthenticated ? '/home' : '/'} className="flex items-center gap-2">
            <div className="w-10 h-10 bg-gradient-to-br from-purple-600 to-blue-500 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-xl">S</span>
            </div>
            <span className="text-2xl font-bold">ShoutX</span>
          </Link>

          <nav className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <div className={`px-3 py-1 rounded-full text-sm font-medium ${
                  user?.planType === 'PRO' 
                    ? 'bg-gradient-to-r from-purple-600 to-blue-500 text-white' 
                    : 'bg-gray-100 text-gray-700'
                }`}>
                  {user?.planType || 'BASIC'}
                </div>
                {user?.planType === 'BASIC' && (
                  <Link to="/payments">
                    <button className="px-4 py-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
                      Get Pro
                    </button>
                  </Link>
                )}
                <Link to="/dashboard" className="flex items-center gap-2 text-gray-700 hover:text-purple-600">
                  <LayoutDashboard className="w-5 h-5" />
                </Link>
                <Link to="/notifications" className="relative flex items-center gap-2 text-gray-700 hover:text-purple-600">
                  <Bell className="w-5 h-5" />
                </Link>
                <div className="relative">
                  <button 
                    onClick={() => setShowDropdown(!showDropdown)}
                    className="flex items-center gap-2 text-gray-700 hover:text-purple-600"
                  >
                    <img src={user?.profilePicture || 'https://via.placeholder.com/40'} alt="" className="w-8 h-8 rounded-full" />
                    <ChevronDown className="w-4 h-4" />
                  </button>
                  {showDropdown && (
                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border py-2">
                      <Link to="/profile/me" className="block px-4 py-2 hover:bg-gray-50">Profile</Link>
                      <Link to="/notifications" className="block px-4 py-2 hover:bg-gray-50">Notifications</Link>
                      {user?.planType === 'BASIC' && (
                        <Link to="/payments" className="block px-4 py-2 hover:bg-gray-50">Upgrade</Link>
                      )}
                      <button onClick={handleLogout} className="w-full text-left px-4 py-2 hover:bg-gray-50 text-red-600">
                        Logout
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <>
                <button 
                  onClick={() => setShowPricingModal(true)}
                  className="text-gray-700 hover:text-purple-600"
                >
                  Plans & Pricing
                </button>
                <button 
                  onClick={() => setShowLoginModal(true)}
                  className="px-4 py-2 text-gray-700 hover:text-purple-600"
                >
                  Login
                </button>
                <button 
                  onClick={() => setShowLoginModal(true)}
                  className="px-4 py-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg hover:shadow-lg transition"
                >
                  Get Started
                </button>
              </>
            )}
          </nav>
        </div>
      </header>

      {showLoginModal && <LoginModal onClose={() => setShowLoginModal(false)} />}
      {showPricingModal && <PricingModal onClose={() => setShowPricingModal(false)} />}
    </>
  );
};

export default Header;
