import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);

  // Mock user for demo
  useEffect(() => {
    const mockUser = {
      id: '1',
      name: 'John Doe',
      username: '@johndoe',
      email: 'john@example.com',
      profilePicture: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400',
      planType: 'BASIC',
      followers: 12500,
      accountType: 'Creator',
      isVerified: true,
      rating: 4.5,
      strikes: 0,
      mediaItems: [
        { id: '1', url: 'https://images.unsplash.com/photo-1682687220742-aba13b6e50ba?w=400', type: 'image' },
        { id: '2', url: 'https://images.unsplash.com/photo-1682687221038-404670f1c00f?w=400', type: 'image' },
      ]
    };
    // setUser(mockUser); // Uncomment for logged-in view
  }, []);

  const login = (userData) => setUser(userData);
  const logout = () => setUser(null);

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

const useAuth = () => useContext(AuthContext);

export { AuthProvider, useAuth };
