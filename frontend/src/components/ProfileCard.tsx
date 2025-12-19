import React from 'react';
import '../styles/ProfileCard.css';

interface User {
  id: number;
  username: string;
  profilePicture: string;
  followers: number;
  rating: number;
}

interface ProfileCardProps {
  user: User;
  onSendShoutout: () => void;
  showFollowers?: boolean;
}

const ProfileCard: React.FC<ProfileCardProps> = ({
  user,
  onSendShoutout,
  showFollowers = true,
}) => {
  const renderRating = (rating: number) => {
    return '⭐'.repeat(Math.floor(rating));
  };

  return (
    <div className="profile-card">
      <div className="card-header">
        <img
          src={user.profilePicture}
          alt={user.username}
          className="profile-image"
        />
      </div>

      <div className="card-body">
        <h3 className="username">{user.username}</h3>

        {showFollowers && (
          <p className="followers">
            👥 {user.followers.toLocaleString()} followers
          </p>
        )}

        <div className="rating">
          <span className="stars">{renderRating(user.rating) || 'No rating'}</span>
          <span className="rating-value">({user.rating.toFixed(1)})</span>
        </div>
      </div>

      <div className="card-footer">
        <button className="btn btn-primary" onClick={onSendShoutout}>
          📨 Send ShoutOut
        </button>
      </div>
    </div>
  );
};

export default ProfileCard;
