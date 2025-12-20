import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import ExchangeModal from '../components/ExchangeModal';
import axios from 'axios';
import '../styles/HomePageLoggedIn.css';

interface User {
  id: number;
  username: string;
  profilePicture: string;
  followerCount: number;
  averageRating: number;
  isVerified: boolean;
  accountType: string;
}

interface Request {
  id: number;
  senderId: number;
  senderUsername: string;
  senderProfilePic: string;
  mediaType: string;
  createdAt: string;
}

type Tab = 'send' | 'requests';

const HomePageLoggedIn: React.FC = () => {
  const { user, token } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('send');
  const [users, setUsers] = useState<User[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [requests, setRequests] = useState<Request[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [showExchangeModal, setShowExchangeModal] = useState(false);

  // Filters
  const [genreFilter, setGenreFilter] = useState('All');
  const [followerRangeFilter, setFollowerRangeFilter] = useState('All');
  const [mediaTypeFilter, setMediaTypeFilter] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');

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

  // Apply filters when users or filters change
  useEffect(() => {
    applyFilters();
  }, [users, genreFilter, followerRangeFilter, mediaTypeFilter, searchQuery]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/users/discovery`, {
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
      const response = await axios.get(`${API_BASE_URL}/exchanges/pending-requests`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRequests(response.data);
    } catch (error) {
      console.error('Error fetching requests', error);
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = users;

    // Genre filter
    if (genreFilter !== 'All') {
      filtered = filtered.filter((u) => u.accountType === genreFilter);
    }

    // Follower range filter
    if (followerRangeFilter !== 'All') {
      filtered = filtered.filter((u) => matchesFollowerRange(u.followerCount, followerRangeFilter));
    }

    // Media type filter (based on user plan - for now assume all can see all)
    if (mediaTypeFilter !== 'All') {
      // This would require checking user's media types
      // Placeholder for now
    }

    // Search filter
    if (searchQuery) {
      filtered = filtered.filter((u) => u.username.toLowerCase().includes(searchQuery.toLowerCase()));
    }

    // Sort by similar follower count (closest first)
    if (user?.followerCount) {
      filtered.sort((a, b) => {
        const diffA = Math.abs(a.followerCount - user.followerCount);
        const diffB = Math.abs(b.followerCount - user.followerCount);
        return diffA - diffB;
      });
    }

    setFilteredUsers(filtered);
  };

  const matchesFollowerRange = (followerCount: number, range: string): boolean => {
    const ranges: { [key: string]: [number, number] } = {
      '0-99': [0, 99],
      '100-499': [100, 499],
      '500-999': [500, 999],
      '1K-2.5K': [1000, 2500],
      '2.5K-5K': [2500, 5000],
      '5K-10K': [5000, 10000],
      '10K-25K': [10000, 25000],
      '25K-50K': [25000, 50000],
      '50K-100K': [50000, 100000],
      '100K-250K': [100000, 250000],
      '250K-500K': [250000, 500000],
      '500K-1M': [500000, 1000000],
      '1M+': [1000000, Infinity],
    };

    const [min, max] = ranges[range] || [0, Infinity];
    return followerCount >= min && followerCount <= max;
  };

  const handleSendShoutout = (user: User) => {
    setSelectedUser(user);
    setShowExchangeModal(true);
  };

  const handleAcceptRequest = (request: Request) => {
    navigate(`/profile/${request.senderId}?requestId=${request.id}`);
  };

  const genres = ['All', 'Fashion', 'Tech', 'Food', 'Travel', 'Lifestyle', 'Music', 'Sports', 'Beauty'];
  const followerRanges = ['All', '0-99', '100-499', '500-999', '1K-2.5K', '2.5K-5K', '5K-10K', '10K-25K', '25K-50K', '50K-100K', '100K-250K', '250K-500K', '500K-1M', '1M+'];
  const mediaTypes = ['All', 'Story', 'Post', 'Reel'];

  return (
    <div className="home-logged-in">
      <div className="home-container">
        {/* Hero Section */}
        <section className="hero-section">
          <h1>Welcome back, {user?.username}!</h1>
          <p className="subtitle">
            {user?.planType === 'PRO' ? '🌟 Pro Member' : '📱 Basic Member'}
          </p>
        </section>

        {/* Tabs */}
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
            {/* Filters Section */}
            <div className="filter-section">
              <input
                type="text"
                placeholder="Search by username..."
                className="search-input"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />

              {/* Genre Filter */}
              <select
                className="filter-dropdown"
                value={genreFilter}
                onChange={(e) => setGenreFilter(e.target.value)}
              >
                <option value="All">📂 Genre: All</option>
                {genres.slice(1).map((g) => (
                  <option key={g} value={g}>
                    {g}
                  </option>
                ))}
              </select>

              {/* Followers Filter */}
              <select
                className="filter-dropdown"
                value={followerRangeFilter}
                onChange={(e) => setFollowerRangeFilter(e.target.value)}
              >
                <option value="All">👥 Followers: All</option>
                {followerRanges.slice(1).map((r) => (
                  <option key={r} value={r}>
                    {r}
                  </option>
                ))}
              </select>

              {/* Media Type Filter */}
              <select
                className="filter-dropdown"
                value={mediaTypeFilter}
                onChange={(e) => setMediaTypeFilter(e.target.value)}
              >
                <option value="All">📹 Media Type: All</option>
                {mediaTypes.slice(1).map((m) => (
                  <option key={m} value={m}>
                    {m}
                  </option>
                ))}
              </select>
            </div>

            {/* Users Grid */}
            {loading ? (
              <div className="loading">Loading creators...</div>
            ) : filteredUsers.length > 0 ? (
              <div className="users-grid">
                {filteredUsers.map((u) => (
                  <div
                    key={u.id}
                    className="user-card"
                    onClick={() => navigate(`/profile/${u.id}`)}
                  >
                    <img src={u.profilePicture} alt={u.username} className="user-avatar" />
                    <div className="user-info">
                      <div className="user-header">
                        <h3>@{u.username}</h3>
                        {u.isVerified && <span className="verified-badge">✓</span>}
                      </div>
                      <p className="user-genre">{u.accountType}</p>
                      <div className="user-stats">
                        <span>👥 {u.followerCount.toLocaleString()}</span>
                        <span>⭐ {u.averageRating.toFixed(1)}</span>
                      </div>
                      <button
                        className="btn btn-primary btn-sm"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleSendShoutout(u);
                        }}
                      >
                        Send Request
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <p>No creators found matching your filters</p>
                <p className="empty-state-hint">Try adjusting your filters</p>
              </div>
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
                    <img src={request.senderProfilePic} alt={request.senderUsername} className="request-avatar" />
                    <div className="request-info">
                      <h3>@{request.senderUsername}</h3>
                      <p>Sent a shoutout request</p>
                      <p className="request-time">{new Date(request.createdAt).toLocaleDateString()}</p>
                    </div>
                    <button
                      className="btn btn-primary"
                      onClick={() => handleAcceptRequest(request)}
                    >
                      Accept & Repost →
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <p>📭 No Pending Requests</p>
                <p className="empty-state-hint">Start sending shoutouts to get requests back!</p>
              </div>
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
