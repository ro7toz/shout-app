import React from 'react';
import { Instagram, X } from 'lucide-react';

interface LoginModalProps {
  onClose: () => void;
}

const LoginModal = ({ onClose }: LoginModalProps) => {
  const handleInstagramLogin = () => {
    const clientId = import.meta.env.VITE_INSTAGRAM_CLIENT_ID;
    const redirectUri = import.meta.env.VITE_INSTAGRAM_REDIRECT_URI;
    
    if (!clientId) {
      alert('Instagram Client ID is not configured. Please set VITE_INSTAGRAM_CLIENT_ID in your .env file.');
      return;
    }
    
    const scope = 'instagram_business_basic,instagram_business_manage_messages,instagram_business_manage_comments,instagram_business_content_publish';
    
    // ✅ FIX: Correct Instagram OAuth URL
    const authUrl = `https://api.instagram.com/oauth/authorize?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=${scope}&response_type=code`;
    
    console.log('🔗 Redirecting to Instagram OAuth:', authUrl);
    window.location.href = authUrl;
  };

  return (
    <div 
      className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div 
        className="bg-white rounded-xl p-8 max-w-md w-full relative"
        onClick={e => e.stopPropagation()}
      >
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600"
        >
          <X className="w-5 h-5" />
        </button>

        <h2 className="text-2xl font-bold mb-4">Welcome to ShoutX</h2>
        <p className="text-gray-600 mb-6">
          Connect your Instagram Business account to start exchanging shoutouts
        </p>
        
        <button
          onClick={handleInstagramLogin}
          className="w-full flex items-center justify-center gap-3 bg-gradient-to-r from-purple-600 to-pink-600 text-white py-3 rounded-lg font-medium hover:shadow-lg transition"
        >
          <Instagram className="w-5 h-5" />
          Continue with Instagram
        </button>

        <button
          onClick={onClose}
          className="w-full mt-4 py-2 text-gray-600 hover:text-gray-900 transition"
        >
          Cancel
        </button>
      </div>
    </div>
  );
};

export default LoginModal;
