import React, { createContext, useContext, useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link, useNavigate, useParams } from 'react-router-dom';
import { 
  Home, Bell, LayoutDashboard, CreditCard, User, LogOut, 
  Star, Users, TrendingUp, Clock, CheckCircle, XCircle, 
  AlertTriangle, Upload, Trash2, Search, Filter, ChevronDown,
  Instagram, Mail, MapPin, Phone, Facebook, Linkedin, Eye, Target
} from 'lucide-react';

// ==================== CONTEXTS ====================

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

// ==================== COMPONENTS ====================

const Header = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const [showDropdown, setShowDropdown] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showPricingModal, setShowPricingModal] = useState(false);
  const navigate = useNavigate();

  return (
    <>
      <header className="border-b bg-white sticky top-0 z-50 shadow-sm">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <Link to={isAuthenticated ? '/home' : '/'} className="flex items-center gap-2">
            <div className="w-10 h-10 bg-gradient-to-br from-purple-600 to-blue-500 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-xl">S</span>
            </div>
            <span className="text-2xl font-bold bg-gradient-to-r from-purple-600 to-blue-500 bg-clip-text text-transparent">ShoutX</span>
          </Link>

          <nav className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <div className={`px-3 py-1 rounded-full text-sm font-medium ${
                  user?.planType === 'PRO' 
                    ? 'bg-gradient-to-r from-purple-600 to-blue-500 text-white' 
                    : 'bg-gray-100 text-gray-700'
                }`}>
                  {user?.planType}
                </div>
                {user?.planType === 'BASIC' && (
                  <Link to="/payments">
                    <button className="px-4 py-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg text-sm font-medium hover:shadow-lg transition">
                      Get Pro
                    </button>
                  </Link>
                )}
                <Link to="/dashboard" className="p-2 text-gray-700 hover:text-purple-600 transition">
                  <LayoutDashboard className="w-5 h-5" />
                </Link>
                <Link to="/notifications" className="p-2 relative text-gray-700 hover:text-purple-600 transition">
                  <Bell className="w-5 h-5" />
                  <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
                </Link>
                <div className="relative">
                  <button 
                    onClick={() => setShowDropdown(!showDropdown)}
                    className="flex items-center gap-2 p-2 hover:bg-gray-50 rounded-lg transition"
                  >
                    <img src={user?.profilePicture} alt="" className="w-8 h-8 rounded-full" />
                    <ChevronDown className="w-4 h-4" />
                  </button>
                  {showDropdown && (
                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border py-2">
                      <Link to="/profile/me" className="block px-4 py-2 hover:bg-gray-50 transition">Profile</Link>
                      <Link to="/notifications" className="block px-4 py-2 hover:bg-gray-50 transition">Notifications</Link>
                      {user?.planType === 'BASIC' && (
                        <Link to="/payments" className="block px-4 py-2 hover:bg-gray-50 transition">Upgrade</Link>
                      )}
                      <hr className="my-2" />
                      <button onClick={logout} className="w-full text-left px-4 py-2 hover:bg-gray-50 text-red-600 transition">
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
                  className="text-gray-700 hover:text-purple-600 transition"
                >
                  Plans & Pricing
                </button>
                <button 
                  onClick={() => setShowLoginModal(true)}
                  className="px-4 py-2 text-gray-700 hover:text-purple-600 transition"
                >
                  Login
                </button>
                <button 
                  onClick={() => setShowLoginModal(true)}
                  className="px-6 py-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg hover:shadow-lg transition font-medium"
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

const Footer = () => (
  <footer className="bg-gray-50 border-t mt-auto">
    <div className="container mx-auto px-4 py-12">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h3 className="font-semibold mb-4">About</h3>
          <div className="space-y-2 text-sm text-gray-600">
            <Link to="/terms" className="block hover:text-purple-600 transition">Terms & Conditions</Link>
            <Link to="/privacy" className="block hover:text-purple-600 transition">Privacy Policy</Link>
            <Link to="/refund" className="block hover:text-purple-600 transition">Refund Policy</Link>
          </div>
        </div>
        <div>
          <h3 className="font-semibold mb-4">Contact</h3>
          <div className="space-y-2 text-sm text-gray-600">
            <div className="flex items-center gap-2"><MapPin className="w-4 h-4" /><span>Poonam Colony, Kota (Rajasthan)</span></div>
            <div className="flex items-center gap-2"><Phone className="w-4 h-4" /><span>+91 9509103148</span></div>
            <a href="mailto:tushkinit@gmail.com" className="flex items-center gap-2 hover:text-purple-600 transition">
              <Mail className="w-4 h-4" /><span>tushkinit@gmail.com</span>
            </a>
            <div className="flex gap-4 mt-4">
              <a href="https://instagram.com/shoutxapp" className="text-gray-600 hover:text-purple-600 transition">
                <Instagram className="w-5 h-5" />
              </a>
              <a href="https://linkedin.com/company/shoutxapp" className="text-gray-600 hover:text-purple-600 transition">
                <Linkedin className="w-5 h-5" />
              </a>
              <a href="https://facebook.com/shoutxapp" className="text-gray-600 hover:text-purple-600 transition">
                <Facebook className="w-5 h-5" />
              </a>
            </div>
          </div>
        </div>
      </div>
      <div className="text-center text-sm text-gray-600 mt-8 pt-8 border-t">
        © 2024 ShoutX. All rights reserved.
      </div>
    </div>
  </footer>
);

const LoginModal = ({ onClose }) => {
  const { login } = useAuth();
  
  const handleInstagramLogin = () => {
    // Mock login for demo
    login({
      id: '1',
      name: 'Demo User',
      username: '@demouser',
      profilePicture: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400',
      planType: 'BASIC',
      followers: 12500,
      accountType: 'Creator',
      isVerified: true,
      rating: 4.5,
      strikes: 0,
      mediaItems: []
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={onClose}>
      <div className="bg-white rounded-xl p-8 max-w-md w-full" onClick={e => e.stopPropagation()}>
        <h2 className="text-3xl font-bold mb-6 text-center">Welcome to ShoutX</h2>
        <button 
          onClick={handleInstagramLogin}
          className="w-full flex items-center justify-center gap-3 px-6 py-4 bg-gradient-to-r from-purple-600 via-pink-500 to-orange-500 text-white rounded-lg hover:shadow-xl transition font-medium"
        >
          <Instagram className="w-6 h-6" />
          Continue with Instagram
        </button>
        <button onClick={onClose} className="w-full mt-4 text-gray-600 hover:text-gray-900 transition">
          Cancel
        </button>
      </div>
    </div>
  );
};

const PricingModal = ({ onClose }) => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-y-auto" onClick={onClose}>
      <div className="bg-white rounded-xl p-8 max-w-4xl w-full my-8" onClick={e => e.stopPropagation()}>
        <h2 className="text-4xl font-bold mb-8 text-center">Choose Your Plan</h2>
        <div className="grid md:grid-cols-2 gap-8">
          <div className="border-2 rounded-xl p-8 hover:shadow-lg transition">
            <h3 className="text-2xl font-bold mb-4">Basic</h3>
            <p className="text-4xl font-bold mb-6">FREE</p>
            <ul className="space-y-3 mb-8">
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span>10 send/accept per day</span>
              </li>
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span>Story reposts only</span>
              </li>
              <li className="flex items-start gap-3">
                <XCircle className="w-5 h-5 text-gray-400 mt-0.5" />
                <span className="text-gray-500">No analytics</span>
              </li>
            </ul>
            <button className="w-full py-3 bg-gray-100 text-gray-500 rounded-lg font-medium" disabled>
              Current Plan
            </button>
          </div>
          <div className="border-2 border-purple-600 rounded-xl p-8 relative hover:shadow-xl transition bg-gradient-to-b from-purple-50 to-white">
            <div className="absolute -top-4 left-1/2 -translate-x-1/2 px-4 py-1 bg-gradient-to-r from-purple-600 to-blue-500 text-white text-sm rounded-full font-medium">
              Most Popular
            </div>
            <h3 className="text-2xl font-bold mb-4">Pro ⭐</h3>
            <p className="text-4xl font-bold mb-2">₹999<span className="text-lg font-normal text-gray-600">/month</span></p>
            <p className="text-lg text-gray-600 mb-6">or ₹9,999/year <span className="text-green-600 font-semibold">(Save 17%)</span></p>
            <ul className="space-y-3 mb-8">
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span className="font-medium">50 send/accept per day</span>
              </li>
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span className="font-medium">All media types (Story, Post, Reel)</span>
              </li>
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span className="font-medium">Full analytics dashboard</span>
              </li>
            </ul>
            <button 
              onClick={() => {
                if (isAuthenticated) navigate('/payments');
                onClose();
              }}
              className="w-full py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg hover:shadow-lg transition font-medium"
            >
              Select Pro →
            </button>
          </div>
        </div>
        <button onClick={onClose} className="w-full mt-6 text-gray-600 hover:text-gray-900 transition">
          Close
        </button>
      </div>
    </div>
  );
};

// ==================== PAGES ====================

const HomePageLoggedOut = () => {
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showPricingModal, setShowPricingModal] = useState(false);

  return (
    <>
      <div className="flex flex-col min-h-screen">
        <Header />
        
        <section className="bg-gradient-to-br from-purple-600 via-purple-700 to-blue-600 text-white py-24 px-4">
          <div className="container mx-auto text-center">
            <h1 className="text-5xl md:text-7xl font-extrabold mb-6 leading-tight">
              Exchange Instagram Shoutouts.<br />
              <span className="bg-gradient-to-r from-yellow-300 to-pink-300 bg-clip-text text-transparent">
                Grow Together.
              </span>
            </h1>
            <p className="text-xl md:text-2xl mb-10 opacity-90 max-w-3xl mx-auto">
              Connect with creators, exchange authentic shoutouts, and grow your audience organically
            </p>
            <button 
              onClick={() => setShowLoginModal(true)}
              className="px-10 py-5 bg-white text-purple-600 rounded-xl text-lg font-bold hover:scale-105 transition shadow-2xl"
            >
              Get Started for Free →
            </button>
          </div>
        </section>

        <section className="py-24 px-4 bg-white">
          <div className="container mx-auto">
            <h2 className="text-5xl font-bold text-center mb-16">How It Works</h2>
            <div className="grid md:grid-cols-3 gap-12">
              {[
                { num: 1, title: 'Sign Up', desc: 'Connect your Instagram account and create your profile in seconds', icon: <User className="w-8 h-8" /> },
                { num: 2, title: 'Exchange', desc: 'Send requests and accept shoutouts from verified creators', icon: <Target className="w-8 h-8" /> },
                { num: 3, title: 'Grow', desc: 'Track your analytics and watch your audience expand exponentially', icon: <TrendingUp className="w-8 h-8" /> }
              ].map(step => (
                <div key={step.num} className="text-center group hover:scale-105 transition">
                  <div className="w-20 h-20 bg-gradient-to-br from-purple-600 to-blue-500 text-white rounded-2xl flex items-center justify-center mx-auto mb-6 text-3xl font-bold shadow-xl group-hover:shadow-2xl transition">
                    {step.num}
                  </div>
                  <div className="mb-4 text-purple-600">{step.icon}</div>
                  <h3 className="text-2xl font-bold mb-3">{step.title}</h3>
                  <p className="text-gray-600 text-lg">{step.desc}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="bg-gray-50 py-24 px-4">
          <div className="container mx-auto max-w-4xl">
            <h2 className="text-5xl font-bold text-center mb-16">Frequently Asked Questions</h2>
            <div className="space-y-4">
              {[
                { q: 'How does ShoutX work?', a: 'ShoutX connects Instagram creators for mutual shoutout exchanges. You repost someone\'s content, they repost yours, and both audiences grow organically.' },
                { q: 'What happens if someone doesn\'t repost?', a: 'We have a 3-strike system. Users have 24 hours to complete exchanges. Failure to do so results in strikes, and 3 strikes lead to permanent account suspension.' },
                { q: 'What\'s the difference between Basic and Pro?', a: 'Basic is free with 10 exchanges/day and story-only reposts. Pro (₹999/month) offers 50 exchanges/day, all media types, and advanced analytics dashboard.' }
              ].map((faq, i) => (
                <details key={i} className="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition">
                  <summary className="font-semibold text-lg cursor-pointer flex items-center justify-between">
                    {faq.q}
                    <ChevronDown className="w-5 h-5" />
                  </summary>
                  <p className="mt-4 text-gray-600 leading-relaxed">{faq.a}</p>
                </details>
              ))}
            </div>
          </div>
        </section>

        <Footer />
      </div>

      {showLoginModal && <LoginModal onClose={() => setShowLoginModal(false)} />}
      {showPricingModal && <PricingModal onClose={() => setShowPricingModal(false)} />}
    </>
  );
};

const HomePageLoggedIn = () => {
  const [activeTab, setActiveTab] = useState('send');
  const mockUsers = [
    { id: '2', name: 'Sarah Miller', username: '@sarahmiller', profilePicture: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400', followers: 15000, accountType: 'Influencer', isVerified: true, rating: 4.8 },
    { id: '3', name: 'Mike Johnson', username: '@mikej', profilePicture: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400', followers: 22000, accountType: 'Creator', isVerified: false, rating: 4.2 }
  ];
  const mockRequests = [
    { id: '1', senderId: '2', senderName: 'Sarah Miller', senderUsername: '@sarahmiller', senderProfilePicture: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400', createdAt: new Date().toISOString() }
  ];

  const navigate = useNavigate();

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      
      <div className="container mx-auto px-4 py-8">
        <div className="flex gap-8 border-b mb-8">
          <button 
            onClick={() => setActiveTab('send')}
            className={`pb-4 px-6 font-semibold text-lg transition ${activeTab === 'send' ? 'border-b-4 border-purple-600 text-purple-600' : 'text-gray-600 hover:text-purple-600'}`}
          >
            Send ShoutOuts
          </button>
          <button 
            onClick={() => setActiveTab('requests')}
            className={`pb-4 px-6 font-semibold text-lg relative transition ${activeTab === 'requests' ? 'border-b-4 border-purple-600 text-purple-600' : 'text-gray-600 hover:text-purple-600'}`}
          >
            Requests
            {mockRequests.length > 0 && (
              <span className="absolute -top-1 -right-2 w-6 h-6 bg-red-500 text-white text-xs rounded-full flex items-center justify-center animate-pulse font-bold">
                {mockRequests.length}
              </span>
            )}
          </button>
        </div>

        {activeTab === 'send' ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {mockUsers.map(user => (
              <div 
                key={user.id}
                onClick={() => navigate(`/profile/${user.id}`)}
                className="bg-white border-2 rounded-xl p-6 hover:shadow-xl hover:border-purple-600 transition cursor-pointer group"
              >
                <div className="flex items-center gap-4 mb-4">
                  <img src={user.profilePicture} alt={user.name} className="w-16 h-16 rounded-full border-4 border-purple-100 group-hover:border-purple-600 transition" />
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <p className="font-bold text-lg">{user.username}</p>
                      {user.isVerified && <CheckCircle className="w-5 h-5 text-blue-500" />}
                    </div>
                    <div className="flex items-center gap-4 text-sm text-gray-600">
                      <span className="flex items-center gap-1">
                        <Users className="w-4 h-4" />
                        {(user.followers / 1000).toFixed(1)}K
                      </span>
                      <span className="flex items-center gap-1">
                        <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                        {user.rating}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="text-sm font-medium text-purple-600 bg-purple-50 px-3 py-1 rounded-full inline-block">
                  {user.accountType}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="space-y-4">
            {mockRequests.length === 0 ? (
              <div className="text-center py-20 bg-white rounded-xl">
                <Bell className="w-16 h-16 mx-auto mb-6 opacity-20" />
                <p className="text-2xl font-bold mb-3 text-gray-800">No Pending Requests</p>
                <p className="text-gray-600">Start sending shoutouts to get requests back!</p>
              </div>
            ) : (
              mockRequests.map(req => (
                <div 
                  key={req.id}
                  className="flex items-center gap-6 p-6 bg-white border-2 rounded-xl hover:shadow-xl hover:border-purple-600 transition cursor-pointer"
                  onClick={() => navigate(`/profile/${req.senderId}`)}
                >
                  <img src={req.senderProfilePicture} alt={req.senderName} className="w-16 h-16 rounded-full border-4 border-purple-100" />
                  <div className="flex-1">
                    <p className="font-bold text-lg mb-1">{req.senderUsername} sent a request</p>
                    <p className="text-sm text-gray-600">2 hours ago</p>
                  </div>
                  <button 
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate(`/profile/${req.senderId}`);
                    }}
                    className="px-6 py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-xl hover:shadow-lg transition font-medium"
                  >
                    Accept & Repost →
                  </button>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      <Footer />
    </div>
  );
};

// ==================== ROUTING & PROTECTED ROUTES ====================

const ProtectedRoute = ({ children, requireAuth = false }) => {
  const { isAuthenticated } = useAuth();
  
  if (requireAuth && !isAuthenticated) {
    return <Navigate to="/" replace />;
  }
  
  if (!requireAuth && isAuthenticated) {
    return <Navigate to="/home" replace />;
  }
  
  return children;
};

const StaticPage = ({ title }) => (
  <div className="flex flex-col min-h-screen">
    <Header />
    <div className="container mx-auto px-4 py-12 flex-1">
      <h1 className="text-4xl font-bold mb-8">{title}</h1>
      <div className="prose max-w-none">
        <p className="text-gray-600">Content for {title}...</p>
      </div>
    </div>
    <Footer />
  </div>
);

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<ProtectedRoute><HomePageLoggedOut /></ProtectedRoute>} />
          <Route path="/home" element={<ProtectedRoute requireAuth><HomePageLoggedIn /></ProtectedRoute>} />
          <Route path="/terms" element={<StaticPage title="Terms & Conditions" />} />
          <Route path="/privacy" element={<StaticPage title="Privacy Policy" />} />
          <Route path="/refund" element={<StaticPage title="Refund Policy" />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
