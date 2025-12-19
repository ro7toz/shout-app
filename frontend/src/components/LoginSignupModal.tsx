import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import '../styles/LoginSignupModal.css';

interface Props { onClose: () => void; }

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
      if (isLogin) {
        await login(formData.instagramUsername, 'token');
      } else {
        await signup(formData.instagramUsername, 'token', formData.followers, 'pic', formData.accountType);
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
        <button className="modal-close" onClick={onClose}>×</button>

        <div className="modal-header">
          <h2>{isLogin ? 'Login' : 'Sign Up'}</h2>
        </div>

        <form onSubmit={handleSubmit} className="form">
          <div className="form-group">
            <label>Instagram Username</label>
            <input type="text" name="instagramUsername" value={formData.instagramUsername} onChange={handleInputChange} required />
          </div>

          {!isLogin && (
            <>
              <div className="form-group">
                <label>Followers</label>
                <input type="number" name="followers" value={formData.followers} onChange={handleInputChange} />
              </div>
              <div className="form-group">
                <label>Account Type</label>
                <select name="accountType" value={formData.accountType} onChange={handleInputChange}>
                  <option value="Creator">Creator</option>
                  <option value="Business">Business</option>
                </select>
              </div>
            </>
          )}

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Loading...' : isLogin ? 'Login' : 'Sign Up'}
          </button>
        </form>

        <div className="modal-footer">
          <p>
            {isLogin ? "Don't have account? " : 'Have account? '}
            <button type="button" className="link-btn" onClick={() => setIsLogin(!isLogin)}>
              {isLogin ? 'Sign Up' : 'Login'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginSignupModal;