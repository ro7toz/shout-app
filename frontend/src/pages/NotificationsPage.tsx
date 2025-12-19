import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/Header';
import Footer from '../components/Footer';
import axios from 'axios';
import '../styles/NotificationsPage.css';

interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

const NotificationsPage: React.FC = () => {
  const { token } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  useEffect(() => {
    fetchNotifications();
  }, [token]);

  const fetchNotifications = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/notifications`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications(response.data);
    } catch (error) {
      console.error('Error fetching notifications', error);
    } finally {
      setLoading(false);
    }
  };

  const markAsRead = async (notificationId: number) => {
    try {
      await axios.put(`${API_BASE_URL}/notifications/${notificationId}/read`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications(notifications.map(n => n.id === notificationId ? { ...n, isRead: true } : n));
    } catch (error) {
      console.error('Error marking notification as read', error);
    }
  };

  const deleteNotification = async (notificationId: number) => {
    try {
      await axios.delete(`${API_BASE_URL}/notifications/${notificationId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications(notifications.filter(n => n.id !== notificationId));
    } catch (error) {
      console.error('Error deleting notification', error);
    }
  };

  if (loading) return <div className="loading">Loading notifications...</div>;

  return (
    <>
      <Header />
      <div className="notifications-page">
        <h1>🔔 Notifications</h1>

        {notifications.length === 0 ? (
          <div className="empty-state">No notifications yet</div>
        ) : (
          <div className="notifications-list">
            {notifications.map((notif) => (
              <div key={notif.id} className={`notification-card ${!notif.isRead ? 'unread' : ''}`}>
                <div className="notification-content">
                  <h3>{notif.title}</h3>
                  <p>{notif.message}</p>
                  <small>{new Date(notif.createdAt).toLocaleDateString()}</small>
                </div>
                <div className="notification-actions">
                  {!notif.isRead && (
                    <button className="btn-small" onClick={() => markAsRead(notif.id)}>
                      Mark as read
                    </button>
                  )}
                  <button className="btn-small btn-danger" onClick={() => deleteNotification(notif.id)}>
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default NotificationsPage;