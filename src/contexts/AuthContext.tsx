import React, { createContext, useContext, useState, ReactNode } from 'react';

export interface MediaItem {
  id: string;
  url: string;
  type: 'image' | 'video';
}

export interface User {
  id: string;
  name: string;
  username: string;
  email: string;
  profilePicture: string;
  planType: 'basic' | 'pro';
  followers: number;
  accountType: string;
  isVerified: boolean;
  rating: number;
  strikes: number;
  dailyRequestsSent: number;
  dailyRequestsAccepted: number;
  mediaItems: MediaItem[];
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (userData: Partial<User>) => Promise<void>;
  logout: () => void;
  upgradeToPro: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

const mockUser: User = {
  id: '1',
  name: 'John Doe',
  username: '@johndoe',
  email: 'john@example.com',
  profilePicture: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400',
  planType: 'basic',
  followers: 12500,
  accountType: 'Creator',
  isVerified: true,
  rating: 4.5,
  strikes: 0,
  dailyRequestsSent: 3,
  dailyRequestsAccepted: 2,
  mediaItems: [
    { id: '1', url: 'https://images.unsplash.com/photo-1682687220742-aba13b6e50ba?w=400', type: 'image' },
    { id: '2', url: 'https://images.unsplash.com/photo-1682687221038-404670f1c00f?w=400', type: 'image' },
    { id: '3', url: 'https://images.unsplash.com/photo-1682687220063-4742bd7fd538?w=400', type: 'image' },
  ],
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);

  const login = async (email: string, password: string) => {
    await new Promise(resolve => setTimeout(resolve, 500));
    setUser(mockUser);
  };

  const signup = async (userData: Partial<User>) => {
    await new Promise(resolve => setTimeout(resolve, 500));
    setUser({ ...mockUser, ...userData } as User);
  };

  const logout = () => {
    setUser(null);
  };

  const upgradeToPro = () => {
    if (user) {
      setUser({ ...user, planType: 'pro' });
    }
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, signup, logout, upgradeToPro }}>
      {children}
    </AuthContext.Provider>
  );
};