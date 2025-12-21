import React, { useEffect } from 'react';
import { Instagram } from 'lucide-react';

interface LoginModalProps {
  onClose: () => void;
}

const LoginModal = ({ onClose }: LoginModalProps) => {
  const handleInstagramLogin = () => {
    const clientId = import.meta.env.VITE_INSTAGRAM_CLIENT_ID;
    const redirectUri = import.meta.env.VITE_INSTAGRAM_REDIRECT_URI;
    const scope = 'instagram_business_basic,instagram_business_manage_messages,instagram_business_manage_comments,instagram_business_content_publish';
    
    const authUrl = `https://api.instagram.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&response_type=code`;
    
    window.location.href = authUrl;
  };

  return (
    
      <div className="bg-white rounded-xl p-8 max-w-md w-full" onClick={e => e.stopPropagation()}>
        Welcome to ShoutX
        
          Connect your Instagram Business account to start exchanging shoutouts
        
        
          
          Continue with Instagram
        
        
          Cancel
        
      
    
  );
};

export default LoginModal;
