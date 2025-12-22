import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useSearchParams } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { DataProvider } from './contexts/DataContext';
import { HomePageLoggedOut, HomePageLoggedIn } from './pages/HomePage';
import { NotificationsPage } from './pages/NotificationsPage';
import { PaymentsPage } from './pages/PaymentsPage';
import { ProfilePage } from './pages/ProfilePage';
import { DashboardPage } from './pages/DashboardPage';

// ✅ NEW: Instagram Callback Handler Component
const InstagramCallbackHandler = () => {
  const [searchParams] = useSearchParams();
  const { handleInstagramCallback } = useAuth();

  useEffect(() => {
    const code = searchParams.get('code');
    const state = searchParams.get('state');

    if (code) {
      handleInstagramCallback(code, state || '');
    }
  }, [searchParams, handleInstagramCallback]);

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 mx-auto mb-4"></div>
        <p className="text-lg font-medium">Completing Instagram authentication...</p>
      </div>
    </div>
  );
};

const ProtectedRoute = ({ children, requireAuth = false }: { children: React.ReactNode; requireAuth?: boolean }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  if (requireAuth && !isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  if (!requireAuth && isAuthenticated) {
    return <Navigate to="/home" replace />;
  }

  return <>{children}</>;
};

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<ProtectedRoute><HomePageLoggedOut /></ProtectedRoute>} />
      <Route path="/auth/callback/instagram" element={<InstagramCallbackHandler />} />
      <Route path="/home" element={<ProtectedRoute requireAuth><HomePageLoggedIn /></ProtectedRoute>} />
      <Route path="/dashboard" element={<ProtectedRoute requireAuth><DashboardPage /></ProtectedRoute>} />
      <Route path="/notifications" element={<ProtectedRoute requireAuth><NotificationsPage /></ProtectedRoute>} />
      <Route path="/payments" element={<ProtectedRoute requireAuth><PaymentsPage /></ProtectedRoute>} />
      <Route path="/profile/:userId" element={<ProtectedRoute requireAuth><ProfilePage /></ProtectedRoute>} />
      <Route path="/profile/me" element={<ProtectedRoute requireAuth><ProfilePage /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

const App = () => {
  return (
    <Router>
      <AuthProvider>
        <DataProvider>
          <Toaster position="top-right" richColors />
          <AppRoutes />
        </DataProvider>
      </AuthProvider>
    </Router>
  );
};

export default App;
