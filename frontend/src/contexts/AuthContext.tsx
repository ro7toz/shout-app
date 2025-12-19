import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import axios from 'axios';

interface User {
  id: number;
  username: string;
  profilePicture: string;
  followers: number;
  planType: 'BASIC' | 'PRO';
  rating: number;
  strikes: number;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (instagramUsername: string, instagramToken: string) => Promise<void>;
  signup: (instagramUsername: string, instagramToken: string, followers: number, profilePicture: string, accountType: string) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  // Check if user is already logged in (from localStorage)
  useEffect(() => {
    const storedToken = localStorage.getItem('authToken');
    if (storedToken) {
      setToken(storedToken);
      // Verify token and fetch user
      verifyToken(storedToken);
    }
    setIsLoading(false);
  }, []);

  const verifyToken = async (token: string) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/auth/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUser(response.data);
    } catch (error) {
      console.error('Token verification failed', error);
      localStorage.removeItem('authToken');
      setToken(null);
      setUser(null);
    }
  };

  const login = useCallback(async (instagramUsername: string, instagramToken: string) => {
    setIsLoading(true);
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        instagramUsername,
        instagramToken,
      });

      const { token: newToken, user: userData } = response.data;
      setToken(newToken);
      setUser(userData);
      localStorage.setItem('authToken', newToken);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const signup = useCallback(
    async (
      instagramUsername: string,
      instagramToken: string,
      followers: number,
      profilePicture: string,
      accountType: string
    ) => {
      setIsLoading(true);
      try {
        const response = await axios.post(`${API_BASE_URL}/auth/signup`, {
          instagramUsername,
          instagramToken,
          followers,
          profilePicture,
          accountType,
        });

        const { token: newToken, user: userData } = response.data;
        setToken(newToken);
        setUser(userData);
        localStorage.setItem('authToken', newToken);
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('authToken');
  }, []);

  const refreshToken = useCallback(async () => {
    if (!token) return;

    try {
      const response = await axios.post(
        `${API_BASE_URL}/auth/refresh-token`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const { token: newToken } = response.data;
      setToken(newToken);
      localStorage.setItem('authToken', newToken);
    } catch (error) {
      console.error('Token refresh failed', error);
      logout();
    }
  }, [token]);

  const value: AuthContextType = {
    user,
    token,
    isLoading,
    isAuthenticated: !!user,
    login,
    signup,
    logout,
    refreshToken,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
