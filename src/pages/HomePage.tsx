import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Bell, Users, Star, CheckCircle } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/ui/Header';
import Footer from '../components/ui/Footer';
import LoginModal from '../components/ui/LoginModal';
import PricingModal from '../components/ui/PricingModal';

export const HomePageLoggedOut = () => {
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showPricingModal, setShowPricingModal] = useState(false);

  return (
    <>
      <div className="flex flex-col min-h-screen">
        <Header />
        
        {/* Hero Section */}
        <section className="bg-gradient-to-br from-purple-600 to-blue-500 text-white py-20 px-4">
          <div className="container mx-auto text-center">
            <h1 className="text-5xl md:text-6xl font-bold mb-6">
              Exchange Instagram Shoutouts.<br />Grow Together.
            </h1>
            <p className="text-xl mb-8 opacity-90">
              Connect with creators, exchange authentic shoutouts, and grow your audience organically
            </p>
            <button 
              onClick={() => setShowLoginModal(true)}
              className="px-8 py-4 bg-white text-purple-600 rounded-lg text-lg font-semibold hover:shadow-xl transition"
            >
              Get Started for Free
            </button>
          </div>
        </section>

        {/* How It Works */}
        <section className="py-20 px-4">
          <div className="container mx-auto">
            <h2 className="text-4xl font-bold text-center mb-12">How It Works</h2>
            <div className="grid md:grid-cols-3 gap-8">
              <div className="text-center">
                <div className="w-16 h-16 bg-purple-100 text-purple-600 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl font-bold">1</div>
                <h3 className="text-xl font-semibold mb-2">Sign Up</h3>
                <p className="text-gray-600">Connect your Instagram account and create your profile</p>
              </div>
              <div className="text-center">
                <div className="w-16 h-16 bg-purple-100 text-purple-600 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl font-bold">2</div>
                <h3 className="text-xl font-semibold mb-2">Exchange</h3>
                <p className="text-gray-600">Send requests and accept shoutouts from other creators</p>
              </div>
              <div className="text-center">
                <div className="w-16 h-16 bg-purple-100 text-purple-600 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl font-bold">3</div>
                <h3 className="text-xl font-semibold mb-2">Grow</h3>
                <p className="text-gray-600">Track your analytics and watch your audience expand</p>
              </div>
            </div>
          </div>
        </section>

        {/* FAQs */}
        <section className="bg-gray-50 py-20 px-4">
          <div className="container mx-auto max-w-3xl">
            <h2 className="text-4xl font-bold text-center mb-12">Frequently Asked Questions</h2>
            <div className="space-y-4">
              <details className="bg-white rounded-lg p-4">
                <summary className="font-semibold cursor-pointer">How does ShoutX work?</summary>
                <p className="mt-2 text-gray-600">ShoutX connects Instagram creators for mutual shoutout exchanges. You repost someone's content, they repost yours, and both audiences grow organically.</p>
              </details>
              <details className="bg-white rounded-lg p-4">
                <summary className="font-semibold cursor-pointer">What happens if someone doesn't repost?</summary>
                <p className="mt-2 text-gray-600">We have a 3-strike system. Users have 24 hours to complete exchanges. Failure to do so results in strikes, and 3 strikes lead to permanent account suspension.</p>
              </details>
              <details className="bg-white rounded-lg p-4">
                <summary className="font-semibold cursor-pointer">What's the difference between Basic and Pro?</summary>
                <p className="mt-2 text-gray-600">Basic is free with 10 exchanges/day and story-only reposts. Pro (₹999/month) offers 50 exchanges/day, all media types, and advanced analytics.</p>
              </details>
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

export const HomePageLoggedIn = () => {
  const [activeTab, setActiveTab] = useState('send');
  const [users, setUsers] = useState<any[]>([]);
  const [requests, setRequests] = useState<any[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch users for Send ShoutOuts tab
    fetch('/api/users/search', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
      .then(res => res.json())
      .then(data => setUsers(data))
      .catch(err => console.error(err));

    // Fetch incoming requests
    fetch('/api/shoutouts/requests', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
      .then(res => res.json())
      .then(data => setRequests(data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      
      <div className="container mx-auto px-4 py-8">
        <div className="flex gap-6 border-b mb-6">
          <button 
            onClick={() => setActiveTab('send')}
            className={`pb-4 px-4 font-semibold ${activeTab === 'send' ? 'border-b-2 border-purple-600 text-purple-600' : 'text-gray-600'}`}
          >
            Send ShoutOuts
          </button>
          <button 
            onClick={() => setActiveTab('requests')}
            className={`pb-4 px-4 font-semibold relative ${activeTab === 'requests' ? 'border-b-2 border-purple-600 text-purple-600' : 'text-gray-600'}`}
          >
            Requests
            {requests.length > 0 && (
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center animate-pulse">
                {requests.length}
              </span>
            )}
          </button>
        </div>

        {activeTab === 'send' ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {users.map(user => (
              <div 
                key={user.id}
                onClick={() => navigate(`/profile/${user.id}`)}
                className="border rounded-lg p-4 hover:shadow-lg transition cursor-pointer"
              >
                <div className="flex items-center gap-3 mb-4">
                  <img src={user.profilePicture} alt={user.name} className="w-12 h-12 rounded-full" />
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <p className="font-semibold">{user.username}</p>
                      {user.isVerified && <CheckCircle className="w-4 h-4 text-blue-500" />}
                    </div>
                    <div className="flex items-center gap-3 text-sm text-gray-600">
                      <span className="flex items-center gap-1">
                        <Users className="w-4 h-4" />
                        {user.followers?.toLocaleString()}
                      </span>
                      <span className="flex items-center gap-1">
                        <Star className="w-4 h-4 text-yellow-500" />
                        {user.rating}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="text-sm text-gray-600 mb-2">{user.accountType}</div>
              </div>
            ))}
          </div>
        ) : (
          <div className="space-y-4">
            {requests.length === 0 ? (
              <div className="text-center py-12 text-gray-500">
                <Bell className="w-12 h-12 mx-auto mb-4 opacity-50" />
                <p className="text-xl font-semibold mb-2">No Pending Requests</p>
                <p>Start sending shoutouts to get requests back!</p>
              </div>
            ) : (
              requests.map(req => (
                <div 
                  key={req.id}
                  onClick={() => navigate(`/profile/${req.senderId}`)}
                  className="flex items-center gap-4 p-4 border rounded-lg hover:shadow-lg transition cursor-pointer"
                >
                  <img src={req.senderProfilePicture} alt={req.senderName} className="w-12 h-12 rounded-full" />
                  <div className="flex-1">
                    <p className="font-semibold">{req.senderUsername} sent a request</p>
                    <p className="text-sm text-gray-600">{new Date(req.createdAt).toLocaleString()}</p>
                  </div>
                  <button className="px-4 py-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg hover:shadow-lg transition">
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
