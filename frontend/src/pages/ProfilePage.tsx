import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/Header';
import Footer from '../components/Footer';
import MediaGrid from '../components/MediaGrid';
import RatingComponent from '../components/RatingComponent';
import axios from 'axios';
import '../styles/ProfilePage.css';

interface ProfileUser {
  id: number;
  username: string;
  profilePicture: string;
  followers: number;
  rating: number;
  strikes: number;
  planType: 'BASIC' | 'PRO';
  mediaItems: Array<{ id: number; url: string; type: string }>;
}

const ProfilePage: React.FC = () => {
  const { userId } = useParams<{ userId: string }>();
  const { user: currentUser, token } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<ProfileUser | null>(null);
  const [loading, setLoading] = useState(true);
  const [isOwnProfile, setIsOwnProfile] = useState(false);

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await axios.get(`${API_BASE_URL}/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setProfile(response.data);
        setIsOwnProfile(currentUser?.id === parseInt(userId || '0'));
      } catch (error) {
        console.error('Error fetching profile', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [userId, token, currentUser?.id]);

  if (loading) return <div className="loading">Loading profile...</div>;
  if (!profile) return <div className="error">Profile not found</div>;

  return (
    <>
      <Header />
      <div className="profile-page">
        <div className="profile-header">
          <img src={profile.profilePicture} alt={profile.username} className="profile-pic" />
          <div className="profile-info">
            <h1>{profile.username}</h1>
            <p className="followers">👥 {profile.followers.toLocaleString()} followers</p>
            <p className="plan-badge">💎 {profile.planType === 'PRO' ? 'Pro' : 'Basic'} Member</p>
            <div className="rating">
              <span className="stars">{'⭐'.repeat(Math.floor(profile.rating))}</span>
              <span className="rating-value">({profile.rating.toFixed(1)})</span>
            </div>
          </div>
        </div>

        {profile.strikes > 0 && (
          <div className="alert alert-warning">
            ⚠️ {profile.strikes} strikes - {3 - profile.strikes} remaining before deactivation
          </div>
        )}

        <div className="media-section">
          <h2>Media Gallery</h2>
          <MediaGrid mediaItems={profile.mediaItems} />
        </div>

        {isOwnProfile && (
          <div className="edit-section">
            <button className="btn btn-primary">Edit Profile</button>
            <button className="btn btn-secondary">Upload Media</button>
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default ProfilePage;