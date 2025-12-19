import React, { useState } from 'react';
import axios from 'axios';
import '../styles/ExchangeModal.css';

interface Props {
  recipient: { id: number; username: string };
  onClose: () => void;
}

const ExchangeModal: React.FC<Props> = ({ recipient, onClose }) => {
  const [mediaType, setMediaType] = useState('STORY');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSendRequest = async () => {
    setLoading(true);
    try {
      await axios.post('/api/shoutouts/send', {
        recipientId: recipient.id,
        mediaType,
        message,
      });
      alert('Request sent!');
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
        <h2>Send ShoutOut Request to {recipient.username}</h2>

        <div className="form-group">
          <label>Media Type</label>
          <select value={mediaType} onChange={(e) => setMediaType(e.target.value)}>
            <option value="STORY">Story</option>
            <option value="POST">Post</option>
            <option value="REEL">Reel</option>
          </select>
        </div>

        <div className="form-group">
          <label>Message</label>
          <textarea value={message} onChange={(e) => setMessage(e.target.value)} rows={3} />
        </div>

        <button className="btn btn-primary" onClick={handleSendRequest} disabled={loading}>
          {loading ? 'Sending...' : 'Send'}
        </button>
      </div>
    </div>
  );
};

export default ExchangeModal;