import React from 'react';
import { Instagram } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

interface LoginModalProps {
  onClose: () => void;
}

const LoginModal = ({ onClose }: LoginModalProps) => {
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleInstagramLogin = () => {
    // Redirect to Instagram OAuth
    window.location.href = '/oauth2/authorization/instagram';
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg p-6 max-w-md w-full">
        <h2 className="text-2xl font-bold mb-4">Login to ShoutX</h2>
        <button 
          onClick={handleInstagramLogin}
          className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-purple-600 to-pink-500 text-white rounded-lg hover:shadow-lg transition"
        >
          <Instagram className="w-5 h-5" />
          Continue with Instagram
        </button>
        <button onClick={onClose} className="w-full mt-4 text-gray-600 hover:text-gray-900">
          Cancel
        </button>
      </div>
    </div>
  );
};

export default LoginModal;
