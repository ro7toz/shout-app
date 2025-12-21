import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { ShoutoutRequest, Exchange, Notification } from '../types';
import { api } from '../services/api';
import { useAuth } from './AuthContext';

interface DataContextType {
  requests: ShoutoutRequest[];
  exchanges: Exchange[];
  notifications: Notification[];
  loading: boolean;
  sendShoutoutRequest: (receiverId: string, mediaId: string) => Promise<void>;
  acceptRequest: (requestId: string) => Promise<void>;
  rateExchange: (exchangeId: string, rating: number) => Promise<void>;
  fetchRequests: () => Promise<void>;
  fetchExchanges: () => Promise<void>;
  fetchNotifications: () => Promise<void>;
  markNotificationAsRead: (notificationId: string) => Promise<void>;
}

const DataContext = createContext<DataContextType | undefined>(undefined);

export const useData = () => {
  const context = useContext(DataContext);
  if (!context) {
    throw new Error('useData must be used within DataProvider');
  }
  return context;
};

export const DataProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  const [requests, setRequests] = useState<ShoutoutRequest[]>([]);
  const [exchanges, setExchanges] = useState<Exchange[]>([]);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchRequests = async () => {
    if (!isAuthenticated) return;
    try {
      setLoading(true);
      const [received, sent] = await Promise.all([
        api.getReceivedRequests(),
        api.getSentRequests()
      ]);
      setRequests([...received.data.requests, ...sent.data.requests]);
    } catch (error) {
      console.error('Failed to fetch requests:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchExchanges = async () => {
    if (!isAuthenticated) return;
    try {
      const response = await api.getActiveExchanges();
      setExchanges(response.data);
    } catch (error) {
      console.error('Failed to fetch exchanges:', error);
    }
  };

  const fetchNotifications = async () => {
    if (!isAuthenticated) return;
    try {
      const response = await api.getNotifications();
      setNotifications(response.data.notifications);
    } catch (error) {
      console.error('Failed to fetch notifications:', error);
    }
  };

  const sendShoutoutRequest = async (receiverId: string, mediaId: string) => {
    try {
      await api.sendRequest(receiverId, mediaId);
      await fetchRequests();
    } catch (error) {
      console.error('Failed to send request:', error);
      throw error;
    }
  };

  const acceptRequest = async (requestId: string) => {
    try {
      await api.acceptRequest(requestId);
      await fetchRequests();
      await fetchExchanges();
    } catch (error) {
      console.error('Failed to accept request:', error);
      throw error;
    }
  };

  const rateExchange = async (exchangeId: string, rating: number) => {
    try {
      await api.rateRequest(exchangeId, rating);
      await fetchExchanges();
    } catch (error) {
      console.error('Failed to rate exchange:', error);
      throw error;
    }
  };

  const markNotificationAsRead = async (notificationId: string) => {
    try {
      await api.markNotificationAsRead(notificationId);
      setNotifications(prev =>
        prev.map(n => n.id === notificationId ? { ...n, read: true } : n)
      );
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      fetchRequests();
      fetchExchanges();
      fetchNotifications();
    }
  }, [isAuthenticated]);

  return (
    <DataContext.Provider value={{
      requests,
      exchanges,
      notifications,
      loading,
      sendShoutoutRequest,
      acceptRequest,
      rateExchange,
      fetchRequests,
      fetchExchanges,
      fetchNotifications,
      markNotificationAsRead,
    }}>
      {children}
    </DataContext.Provider>
  );
};
