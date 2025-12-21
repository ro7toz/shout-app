import React from 'react';
import { useData } from '../contexts/DataContext';
import { Bell, CheckCircle } from 'lucide-react';
import Header from '../components/ui/Header';
import Footer from '../components/ui/Footer';

export const NotificationsPage = () => {
  const { notifications, markNotificationAsRead } = useData();

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <div className="container mx-auto px-4 py-8 flex-1">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold">Notifications</h1>
          {unreadCount > 0 && (
            <span className="px-3 py-1 bg-red-500 text-white rounded-full text-sm font-medium">
              {unreadCount} unread
            </span>
          )}
        </div>

        <div className="space-y-4">
          {notifications.length === 0 ? (
            <div className="bg-white rounded-lg p-12 text-center">
              <Bell className="w-16 h-16 mx-auto mb-4 text-gray-400" />
              <p className="text-xl font-semibold text-gray-600">No notifications yet</p>
              <p className="text-gray-500 mt-2">We'll notify you when something happens</p>
            </div>
          ) : (
            notifications.map(notification => (
              <div 
                key={notification.id}
                className={`bg-white border-2 rounded-lg p-4 cursor-pointer transition ${
                  !notification.read ? 'bg-blue-50 border-blue-200' : 'border-gray-200 hover:border-purple-600'
                }`}
                onClick={() => !notification.read && markNotificationAsRead(notification.id)}
              >
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <p className="font-semibold text-gray-800">{notification.message}</p>
                    <p className="text-sm text-gray-500 mt-1">
                      {new Date(notification.createdAt).toLocaleString()}
                    </p>
                  </div>
                  {!notification.read && (
                    <div className="w-3 h-3 bg-blue-500 rounded-full ml-4" />
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
      <Footer />
    </div>
  );
};
