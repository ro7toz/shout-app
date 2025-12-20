import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Bell, Users, Star, CheckCircle, User, Target, TrendingUp, ChevronDown } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/ui/Header';
import Footer from '../components/ui/Footer';
import LoginModal from '../components/ui/LoginModal';

export const HomePageLoggedOut = () => {
  const [showLoginModal, setShowLoginModal] = useState(false);

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
    </>
  );
};

export const HomePageLoggedIn = () => {
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
