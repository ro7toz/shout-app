import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';
import '../styles/ExchangeModal.css';

interface ExchangeModalProps {
  recipient: { id: number; username: string; profilePicture: string };
  onClose: () => void;
}

const ExchangeModal: React.FC<ExchangeModalProps> = ({ recipient, onClose }) => {
  const { user, token } = useAuth();
  const [mediaType, setMediaType] = useState('STORY');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  const handleSendRequest = async () => {
    setLoading(true);
    setError('');
    try {
      await axios.post(
        `${API_BASE_URL}/shoutouts/send`,
        {
          recipientId: recipient.id,
          mediaType,
          mediaUrl: 'https://via.placeholder.com/500',
          message,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert('Request sent successfully!');
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.error || 'Error sending request');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>✕</button>

        <div className="modal-header">
          <h2>Send ShoutOut Request</h2>
          <p>To: {recipient.username}</p>
        </div>

        <form onSubmit={(e) => { e.preventDefault(); handleSendRequest(); }} className="form">
          <div className="form-group">
            <label>Media Type</label>
            <select value={mediaType} onChange={(e) => setMediaType(e.target.value)}>
              <option value="STORY">Story</option>
              <option value="POST">Post</option>
              <option value="REEL">Reel</option>
            </select>
            {user?.planType === 'BASIC' && mediaType !== 'STORY' && (
              <p className="warning">⚠️ Upgrade to Pro to use {mediaType}</p>
            )}
          </div>

          <div className="form-group">
            <label>Message (Optional)</label>
            <textarea
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Add a message to your request..."
              rows={4}
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Sending...' : 'Send Request'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ExchangeModal;