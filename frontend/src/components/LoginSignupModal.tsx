import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import '../styles/LoginSignupModal.css';

interface Props {
  onClose: () => void;
}

const LoginSignupModal: React.FC<Props> = ({ onClose }) => {
  const { login, signup } = useAuth();
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    instagramUsername: '',
    followers: 0,
    accountType: 'Creator',
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'followers' ? parseInt(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      // TODO: Get actual Instagram token from OAuth
      const instagramToken = 'temp_token';
      const profilePicture = 'https://via.placeholder.com/150';

      if (isLogin) {
        await login(formData.instagramUsername, instagramToken);
      } else {
        await signup(formData.instagramUsername, instagramToken, formData.followers, profilePicture, formData.accountType);
      }
      onClose();
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>✕</button>

        <div className="modal-header">
          <h2>{isLogin ? 'Login' : 'Sign Up'}</h2>
          <p>Connect your Instagram account to get started</p>
        </div>

        <form onSubmit={handleSubmit} className="form">
          <div className="form-group">
            <label>Instagram Username</label>
            <input
              type="text"
              name="instagramUsername"
              value={formData.instagramUsername}
              onChange={handleInputChange}
              placeholder="@username"
              required
            />
          </div>

          {!isLogin && (
            <>
              <div className="form-group">
                <label>Followers</label>
                <input
                  type="number"
                  name="followers"
                  value={formData.followers}
                  onChange={handleInputChange}
                  placeholder="10000"
                />
              </div>
              <div className="form-group">
                <label>Account Type</label>
                <select name="accountType" value={formData.accountType} onChange={handleInputChange}>
                  <option value="Creator">Creator</option>
                  <option value="Business">Business</option>
                  <option value="Personal">Personal</option>
                </select>
              </div>
            </>
          )}

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Loading...' : isLogin ? 'Login with Instagram' : 'Sign Up with Instagram'}
          </button>
        </form>

        <div className="modal-footer">
          <p>
            {isLogin ? "Don't have an account? " : 'Already have an account? '}
            <button
              type="button"
              className="link-btn"
              onClick={() => setIsLogin(!isLogin)}
            >
              {isLogin ? 'Sign Up' : 'Login'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginSignupModal;