import React from 'react';
import { Instagram } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface LoginModalProps {
  onClose: () => void;
}

const LoginModal = ({ onClose }: LoginModalProps) => {
  const { login } = useAuth();
  
  const handleInstagramLogin = () => {
    // Mock login for demo
    login({
      id: '1',
      name: 'Demo User',
      username: '@demouser',
      profilePicture: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400',
      planType: 'BASIC',
      followers: 12500,
      accountType: 'Creator',
      isVerified: true,
      rating: 4.5,
      strikes: 0,
      mediaItems: []
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={onClose}>
      <div className="bg-white rounded-xl p-8 max-w-md w-full" onClick={e => e.stopPropagation()}>
        <h2 className="text-3xl font-bold mb-6 text-center">Welcome to ShoutX</h2>
        <button 
          onClick={handleInstagramLogin}
          className="w-full flex items-center justify-center gap-3 px-6 py-4 bg-gradient-to-r from-purple-600 via-pink-500 to-orange-500 text-white rounded-lg hover:shadow-xl transition font-medium"
        >
          <Instagram className="w-6 h-6" />
          Continue with Instagram
        </button>
        <button onClick={onClose} className="w-full mt-4 text-gray-600 hover:text-gray-900 transition">
          Cancel
        </button>
      </div>
    </div>
  );
};

export default LoginModal;
