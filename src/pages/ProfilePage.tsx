import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useData } from '../contexts/DataContext';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { AlertCircle, Upload, Trash2 } from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const { user, upgradeToPro } = useAuth();
  const { requests } = useData();
  const [isOwnProfile] = useState(true);

  if (!user) {
    return <div>Loading...</div>;
  }

  const mediaToRepost = requests.filter(
    req => req.status === 'pending' && req.receiverId === user?.id
  );

  return (
    <>
      <div className="flex flex-col items-center justify-center min-h-screen bg-background p-4">
        <Card className="w-full max-w-2xl">
          <CardHeader className="text-center">
            <div className="flex justify-center mb-4">
              <img
                src={user.profilePicture}
                alt={user.name}
                className="w-24 h-24 rounded-full"
              />
            </div>
            <CardTitle className="text-3xl">{user.name}</CardTitle>
            <p className="text-muted-foreground">{user.username}</p>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div className="text-center">
                <p className="text-2xl font-bold">{user.followers.toLocaleString()}</p>
                <p className="text-sm text-muted-foreground">Followers</p>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold">
                  {user.planType === 'pro' ? 'Pro' : 'Basic'}
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
                <Button variant="outline" className="w-full">
                  <Upload className="w-4 h-4 mr-2" />
                  Upload Media
                </Button>
              </div>
              <div className="space-y-2">
                {user.mediaItems.map((item) => (
                  <div key={item.id} className="flex items-center gap-2 p-2 bg-muted rounded">
                    <img
                      src={item.url}
                      alt="Media"
                      className="w-10 h-10 rounded"
                    />
                    <span className="flex-1 text-sm">{item.type}</span>
                    <Button variant="ghost" size="sm">
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  </div>
                ))}
              </div>
            </div>

            {user.planType === 'basic' && (
              <Button onClick={upgradeToPro} className="w-full" variant="default">
                Upgrade to Pro
              </Button>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  );
};