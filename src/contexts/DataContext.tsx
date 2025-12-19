import React, { createContext, useContext, useState, ReactNode } from 'react';

export interface ShoutoutUser {
  id: string;
  name: string;
  username: string;
  profilePicture: string;
  followers: number;
  accountType: string;
  isVerified: boolean;
  rating: number;
  mediaItems: { id: string; url: string; type: 'image' | 'video' }[];
}

export interface ShoutoutRequest {
  id: string;
  senderId: string;
  senderName: string;
  senderUsername: string;
  senderProfilePicture: string;
  receiverId: string;
  mediaId: string;
  status: 'pending' | 'accepted' | 'completed' | 'expired';
  createdAt: Date;
  completedAt?: Date;
  sentByMe: boolean;
  receivedByMe: boolean;
}

export interface Exchange {
  id: string;
  user1Id: string;
  user1Name: string;
  user1Username: string;
  user2Id: string;
  user2Name: string;
  user2Username: string;
  status: 'incomplete' | 'complete';
  timeStatus: 'live' | 'expired';
  createdAt: Date;
  completedAt?: Date;
  rating?: number;
}

export interface Notification {
  id: string;
  type: 'request' | 'acceptance' | 'completion' | 'warning';
  message: string;
  createdAt: Date;
  read: boolean;
  relatedId?: string;
}

interface DataContextType {
  users: ShoutoutUser[];
  requests: ShoutoutRequest[];
  exchanges: Exchange[];
  sendShoutoutRequest: (receiverId: string, mediaId: string) => void;
  acceptRequest: (requestId: string, mediaId: string) => void;
  rateExchange: (exchangeId: string, rating: number) => void;
  filterUsers: (filter: { genre?: string; followers?: string; repostType?: string }) => ShoutoutUser[];
  notifications: Notification[];
  markNotificationAsRead: (notificationId: string) => void;
}

const DataContext = createContext<DataContextType | undefined>(undefined);

export const useData = () => {
  const context = useContext(DataContext);
  if (!context) {
    throw new Error('useData must be used within DataProvider');
  }
  return context;
};

const mockUsers: ShoutoutUser[] = [
  {
    id: '2',
    name: 'Sarah Miller',
    username: '@sarahmiller',
    profilePicture: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400',
    followers: 15000,
    accountType: 'Influencer',
    isVerified: true,
    rating: 4.8,
    mediaItems: [
      { id: '1', url: 'https://images.unsplash.com/photo-1682695796795-cc62cd78e5d2?w=400', type: 'image' },
      { id: '2', url: 'https://images.unsplash.com/photo-1682695796497-31a44224d6d6?w=400', type: 'image' },
    ],
  },
];

const mockNotifications: Notification[] = [
  {
    id: '1',
    type: 'request',
    message: 'Sarah Miller sent you a shoutout request',
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000),
    read: false,
    relatedId: '1',
  },
];

export const DataProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [users] = useState(mockUsers);
  const [requests, setRequests] = useState<ShoutoutRequest[]>([]);
  const [exchanges, setExchanges] = useState<Exchange[]>([]);
  const [notifications, setNotifications] = useState(mockNotifications);

  const sendShoutoutRequest = (receiverId: string, mediaId: string) => {
    const newRequest: ShoutoutRequest = {
      id: Date.now().toString(),
      senderId: '1',
      senderName: 'John Doe',
      senderUsername: '@johndoe',
      senderProfilePicture: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400',
      receiverId,
      mediaId,
      status: 'pending',
      createdAt: new Date(),
      sentByMe: true,
      receivedByMe: false,
    };
    setRequests([...requests, newRequest]);
  };

  const acceptRequest = (requestId: string, mediaId: string) => {
    setRequests(requests.map(req =>
      req.id === requestId ? { ...req, status: 'accepted' as const } : req
    ));
  };

  const rateExchange = (exchangeId: string, rating: number) => {
    setExchanges(exchanges.map(ex =>
      ex.id === exchangeId ? { ...ex, rating } : ex
    ));
  };

  const filterUsers = () => users;

  const markNotificationAsRead = (notificationId: string) => {
    setNotifications(notifications.map(notif =>
      notif.id === notificationId ? { ...notif, read: true } : notif
    ));
  };

  return (
    <DataContext.Provider value={{
      users,
      requests,
      exchanges,
      sendShoutoutRequest,
      acceptRequest,
      rateExchange,
      filterUsers,
      notifications,
      markNotificationAsRead,
    }}>
      {children}
    </DataContext.Provider>
  );
};