import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { User } from '../types';
import { api } from '../services/api';
import { useNavigate } from 'react-router-dom';

interface AuthContextType {
  user: User | null;
  login: (userData: User, token: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
  loading: boolean;
  handleInstagramCallback: (code: string, state: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('auth_token');
    const storedUser = localStorage.getItem('user');

    if (token && storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        setUser(parsedUser);
      } catch (error) {
        console.error('Failed to parse stored user:', error);
        localStorage.removeItem('auth_token');
        localStorage.removeItem('user');
      }
    }
    setLoading(false);
  }, []);

  // ✅ FIX: Add Instagram callback handler
  const handleInstagramCallback = async (code: string, state: string) => {
    try {
      setLoading(true);
      console.log('🔐 Processing Instagram callback...');
      
      const response = await api.instagramCallback(code, state);
      const authData = response.data;

      if (authData.success && authData.token && authData.user) {
        // Store auth data
        localStorage.setItem('auth_token', authData.token);
        localStorage.setItem('user', JSON.stringify(authData.user));
        
        // Update state
        setUser(authData.user);
        
        console.log('✅ Authentication successful');
        navigate('/home');
      } else {
        throw new Error(authData.message || 'Authentication failed');
      }
    } catch (error: any) {
      console.error('❌ Instagram auth error:', error);
      alert('Authentication failed: ' + (error.response?.data?.message || error.message));
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  const login = (userData: User, token: string) => {
    localStorage.setItem('auth_token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user');
    window.location.href = '/';
  };

  return (
    <AuthContext.Provider value={{
      user,
      login,
      logout,
      isAuthenticated: !!user,
      loading,
      handleInstagramCallback
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
