import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import ExchangeModal from '../components/ExchangeModal';
import ProfileCard from '../components/ProfileCard';
import axios from 'axios';
import '../styles/HomePageLoggedIn.css';

interface User {
  id: number;
  username: string;
  profilePicture: string;
  followers: number;
  rating: number;
}

type Tab = 'send' | 'requests';

const HomePageLoggedIn: React.FC = () => {
  const { user, token } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('send');
  const [users, setUsers] = useState<User[]>([]);
  const [requests, setRequests] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [showExchangeModal, setShowExchangeModal] = useState(false);

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  // Fetch users for "Send ShoutOuts" tab
  useEffect(() => {
    if (activeTab === 'send') {
      fetchUsers();
    }
  }, [activeTab]);

  // Fetch pending requests for "Requests" tab
  useEffect(() => {
    if (activeTab === 'requests') {
      fetchRequests();
    }
  }, [activeTab]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/users/search`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching users', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchRequests = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/shoutouts/requests`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRequests(response.data);
    } catch (error) {
      console.error('Error fetching requests', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSendShoutout = (user: User) => {
    setSelectedUser(user);
    setShowExchangeModal(true);
  };

  const handleAcceptRequest = (request: any) => {
    // Navigate to user profile to select media
    navigate(`/profile/${user?.id}?requestId=${request.id}`);
  };

  return (
    <div className="home-logged-in">
      <div className="home-container">
        <section className="hero-section">
          <h1>Welcome back, {user?.username}!</h1>
          <p className="subtitle">
            {user?.planType === 'PRO' ? '🌟 Pro Member' : '📱 Basic Member'}
          </p>
        </section>

        <div className="tabs">
          <button
            className={`tab-button ${activeTab === 'send' ? 'active' : ''}`}
            onClick={() => setActiveTab('send')}
          >
            📤 Send ShoutOuts
          </button>
          <button
            className={`tab-button ${activeTab === 'requests' ? 'active' : ''}`}
            onClick={() => setActiveTab('requests')}
          >
            📨 Requests
            {requests.length > 0 && <span className="badge">{requests.length}</span>}
          </button>
        </div>

        {/* Send ShoutOuts Tab */}
        {activeTab === 'send' && (
          <section className="tab-content">
            <div className="filter-section">
              <input
                type="text"
                placeholder="Search by username..."
                className="search-input"
                onChange={(e) => {
                  if (e.target.value) {
                    const filtered = users.filter((u) =>
                      u.username.toLowerCase().includes(e.target.value.toLowerCase())
                    );
                    setUsers(filtered);
                  } else {
                    fetchUsers();
                  }
                }}
              />
            </div>

            {loading ? (
              <div className="loading">Loading users...</div>
            ) : users.length > 0 ? (
              <div className="users-grid">
                {users.map((u) => (
                  <ProfileCard
                    key={u.id}
                    user={u}
                    onSendShoutout={() => handleSendShoutout(u)}
                  />
                ))}
              </div>
            ) : (
              <div className="empty-state">No users found</div>
            )}
          </section>
        )}

        {/* Requests Tab */}
        {activeTab === 'requests' && (
          <section className="tab-content">
            {loading ? (
              <div className="loading">Loading requests...</div>
            ) : requests.length > 0 ? (
              <div className="requests-list">
                {requests.map((request) => (
                  <div key={request.id} className="request-card">
                    <div className="request-header">
                      <img
                        src={request.senderProfilePic}
                        alt={request.senderName}
                        className="request-avatar"
                      />
                      <div className="request-info">
                        <h3>{request.senderName}</h3>
                        <p className="request-type">Wants to repost your {request.mediaType}</p>
                        <p className="request-time">
                          {new Date(request.createdAt).toLocaleDateString()}
                        </p>
                      </div>
                    </div>

                    {request.message && (
                      <p className="request-message">Message: {request.message}</p>
                    )}

                    <button
                      className="btn btn-primary"
                      onClick={() => handleAcceptRequest(request)}
                    >
                      👀 View & Accept
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">No pending requests</div>
            )}
          </section>
        )}
      </div>

      {/* Exchange Modal */}
      {showExchangeModal && selectedUser && (
        <ExchangeModal
          recipient={selectedUser}
          onClose={() => {
            setShowExchangeModal(false);
            setSelectedUser(null);
          }}
        />
      )}
    </div>
  );
};

export default HomePageLoggedIn;
