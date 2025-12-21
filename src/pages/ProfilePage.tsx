import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useData } from '../contexts/DataContext';
import { AlertCircle, Upload, Trash2 } from 'lucide-react';
import Header from '../components/ui/Header';
import Footer from '../components/ui/Footer';

export const ProfilePage: React.FC = () => {
  const { user, upgradeToPro } = useAuth();
  const { requests } = useData();
  const [isOwnProfile] = useState(true);

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  const mediaToRepost = requests.filter(
    req => req.status === 'pending' && req.receiverId === user?.id
  );

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <div className="flex flex-col items-center justify-center flex-1 bg-background p-4">
        <div className="w-full max-w-2xl bg-white rounded-lg shadow-lg">
          <div className="p-6 text-center">
            <div className="flex justify-center mb-4">
              <img
                src={user.profilePicture}
                alt={user.name}
                className="w-24 h-24 rounded-full border-4 border-purple-600"
              />
            </div>
            <h2 className="text-3xl font-bold">{user.name}</h2>
            <p className="text-muted-foreground">{user.username}</p>
          </div>
          <div className="border-t p-6 space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div className="text-center">
                <p className="text-2xl font-bold">{user.followers.toLocaleString()}</p>
                <p className="text-sm text-muted-foreground">Followers</p>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold">
                  {user.planType === 'PRO' ? 'Pro' : 'Basic'}
                </p>
                <p className="text-sm text-muted-foreground">Plan Type</p>
              </div>
            </div>

            {user.strikes > 0 && (
              <div className="p-3 border border-red-300 bg-red-50 rounded-md flex items-center gap-2">
                <AlertCircle className="h-4 w-4 text-red-600" />
                <p className="text-sm text-red-700">
                  ⚠️ You have {user.strikes} strike{user.strikes !== 1 ? 's' : ''}. 3 strikes will result in account deactivation.
                </p>
              </div>
            )}

            <div className="space-y-4">
              <div>
                <h3 className="font-semibold mb-2">My Media</h3>
                <p className="text-sm text-muted-foreground mb-4">
                  Upload photos or media from Instagram (max 3, min 1)
                </p>
                <button className="w-full py-2 px-4 border-2 border-gray-300 rounded-lg hover:border-purple-600 transition font-medium">
                  <Upload className="w-4 h-4 mr-2 inline" />
                  Upload Media
                </button>
              </div>
              <div className="space-y-2">
                {user.mediaItems && user.mediaItems.length > 0 ? (
                  user.mediaItems.map((item) => (
                    <div key={item.id} className="flex items-center gap-2 p-2 bg-muted rounded">
                      <img
                        src={item.url}
                        alt="Media"
                        className="w-10 h-10 rounded"
                      />
                      <span className="flex-1 text-sm">{item.type}</span>
                      <button className="p-2 hover:bg-red-100 rounded transition">
                        <Trash2 className="w-4 h-4 text-red-600" />
                      </button>
                    </div>
                  ))
                ) : (
                  <p className="text-sm text-muted-foreground text-center py-4">No media uploaded yet</p>
                )}
              </div>
            </div>

            {user.planType === 'BASIC' && (
              <button 
                onClick={upgradeToPro}
                className="w-full py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg font-medium hover:shadow-lg transition"
              >
                Upgrade to Pro
              </button>
            )}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ProfilePage;
