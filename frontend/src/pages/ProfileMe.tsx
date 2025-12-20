import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';
import '../styles/ProfileMe.css';

interface UserProfile {
  id: number;
  username: string;
  fullName: string;
  profilePicture: string;
  followerCount: number;
  averageRating: number;
  isVerified: boolean;
  accountType: string;
  strikeCount: number;
  media: Array<{
    id: number;
    mediaType: string;
    s3Url: string;
    thumbnailUrl: string;
    uploadedAt: string;
  }>;
}

const ProfileMe: React.FC = () => {
  const { user, token } = useAuth();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [showUploadDialog, setShowUploadDialog] = useState(false);
  const [uploading, setUploading] = useState(false);

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/users/profile/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProfile(response.data);
    } catch (error) {
      console.error('Error fetching profile', error);
    } finally {
      setLoading(false);
    }
  };

  const handleUploadMedia = async (file: File, source: 'instagram' | 'upload') => {
    if (profile && profile.media.length >= 3) {
      alert('Maximum 3 media items allowed');
      return;
    }

    setUploading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('mediaType', 'STORY');

      const response = await axios.post(`${API_BASE_URL}/media/upload`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      if (profile) {
        setProfile({
          ...profile,
          media: [...profile.media, response.data],
        });
      }
      setShowUploadDialog(false);
    } catch (error) {
      console.error('Error uploading media', error);
      alert('Failed to upload media');
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteMedia = async (mediaId: number) => {
    if (!profile || profile.media.length <= 1) {
      alert('Cannot delete last media. Profile must have at least 1 item.');
      return;
    }

    if (!window.confirm('Are you sure you want to delete this media?')) {
      return;
    }

    try {
      await axios.delete(`${API_BASE_URL}/media/${mediaId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setProfile({
        ...profile,
        media: profile.media.filter((m) => m.id !== mediaId),
      });
    } catch (error) {
      console.error('Error deleting media', error);
      alert('Failed to delete media');
    }
  };

  const getStrikeColor = (strikes: number) => {
    if (strikes === 0) return 'gray';
    if (strikes === 1) return 'orange';
    if (strikes === 2) return 'red';
    return 'red';
  };

  const getStrikeMessage = (strikes: number) => {
    if (strikes === 0) return '⚠️ Strikes: 0/3';
    if (strikes === 1) return '⚠️ Strikes: 1/3';
    if (strikes === 2) return '⚠️ Strikes: 2/3 - Final Warning!';
    return '⚠️ Strikes: 3/3 - Account Banned';
  };

  if (loading) {
    return <div className="loading-container">Loading your profile...</div>;
  }

  if (!profile) {
    return <div className="error-container">Failed to load profile</div>;
  }

  return (
    <div className="profile-me-page">
      <div className="profile-container">
        {/* Profile Header */}
        <section className="profile-header">
          <img src={profile.profilePicture} alt={profile.username} className="profile-picture" />

          <div className="profile-info">
            <div className="profile-name-section">
              <h1>@{profile.username}</h1>
              {profile.isVerified && <span className="verified-badge">✓ Verified</span>}
            </div>

            <h2 className="profile-full-name">{profile.fullName}</h2>

            <div className="profile-stats">
              <div className="stat">
                <span className="stat-label">Followers</span>
                <span className="stat-value">{profile.followerCount.toLocaleString()}</span>
              </div>
              <div className="stat">
                <span className="stat-label">Rating</span>
                <span className="stat-value">⭐ {profile.averageRating.toFixed(1)}</span>
              </div>
              <div className="stat">
                <span className="stat-label">Category</span>
                <span className="stat-value">{profile.accountType}</span>
              </div>
            </div>

            {/* Strike Counter - Color Coded */}
            <div className={`strike-counter strike-${getStrikeColor(profile.strikeCount)}`}>
              {getStrikeMessage(profile.strikeCount)}
            </div>
          </div>
        </section>

        {/* Media Section */}
        <section className="media-section">
          <div className="media-header">
            <h2>Your Content</h2>
            {profile.media.length < 3 && (
              <button
                className="btn btn-primary btn-sm"
                onClick={() => setShowUploadDialog(true)}
              >
                + Add Media
              </button>
            )}
          </div>

          {profile.media.length > 0 ? (
            <div className="media-grid">
              {profile.media.map((media) => (
                <div key={media.id} className="media-item">
                  <img src={media.thumbnailUrl || media.s3Url} alt="User media" />
                  <span className="media-type-badge">{media.mediaType}</span>
                  <button
                    className="delete-btn"
                    onClick={() => handleDeleteMedia(media.id)}
                    title="Delete media"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-media-state">
              <p>No media yet</p>
              <button
                className="btn btn-primary"
                onClick={() => setShowUploadDialog(true)}
              >
                Upload Your First Media
              </button>
            </div>
          )}
        </section>
      </div>

      {/* Upload Dialog */}
      {showUploadDialog && (
        <div className="modal-overlay" onClick={() => setShowUploadDialog(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Add Media to Your Profile</h2>
            <p className="modal-subtitle">Maximum 3 media items allowed</p>

            <div className="upload-options">
              {/* Import from Instagram */}
              <div className="upload-option">
                <label>
                  📷 Import from Instagram
                  <input
                    type="file"
                    hidden
                    onChange={(e) => {
                      if (e.target.files?.[0]) {
                        handleUploadMedia(e.target.files[0], 'instagram');
                      }
                    }}
                  />
                </label>
                <p>Select from your recent Instagram posts</p>
              </div>

              {/* Upload Custom Image */}
              <div className="upload-option">
                <label>
                  ⬆️ Upload Custom Image
                  <input
                    type="file"
                    accept="image/*"
                    hidden
                    onChange={(e) => {
                      if (e.target.files?.[0]) {
                        handleUploadMedia(e.target.files[0], 'upload');
                      }
                    }}
                  />
                </label>
                <p>Upload from your device</p>
              </div>
            </div>

            <div className="modal-actions">
              <button className="btn btn-outline" onClick={() => setShowUploadDialog(false)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfileMe;
