import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { DataProvider } from './contexts/DataContext';
import { HomePageLoggedOut, HomePageLoggedIn } from './pages/HomePage';
import { NotificationsPage } from './pages/NotificationsPage';
import { PaymentsPage } from './pages/PaymentsPage';
import Header from './components/ui/Header';
import Footer from './components/ui/Footer';

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

const StaticPage = ({ title, children }: { title: string; children?: React.ReactNode }) => (
  <div className="flex flex-col min-h-screen">
    <Header />
    <div className="container mx-auto px-4 py-12 flex-1">
      <h1 className="text-4xl font-bold mb-8">{title}</h1>
      <div className="prose max-w-none">
        {children || <p className="text-gray-600">Content for {title}...</p>}
      </div>
    </div>
    <Footer />
  </div>
);

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<ProtectedRoute><HomePageLoggedOut /></ProtectedRoute>} />
      <Route path="/home" element={<ProtectedRoute requireAuth><HomePageLoggedIn /></ProtectedRoute>} />
      <Route path="/notifications" element={<ProtectedRoute requireAuth><NotificationsPage /></ProtectedRoute>} />
      <Route path="/payments" element={<ProtectedRoute requireAuth><PaymentsPage /></ProtectedRoute>} />
      <Route path="/terms" element={<StaticPage title="Terms & Conditions" />} />
      <Route path="/privacy" element={<StaticPage title="Privacy Policy" />} />
      <Route path="/refund" element={<StaticPage title="Refund Policy" />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

const App = () => {
  return (
    <AuthProvider>
      <DataProvider>
        <Router>
          <AppRoutes />
        </Router>
      </DataProvider>
    </AuthProvider>
  );
};

export default App;
