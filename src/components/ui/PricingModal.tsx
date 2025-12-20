import React from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircle, XCircle } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface PricingModalProps {
  onClose: () => void;
}

const PricingModal = ({ onClose }: PricingModalProps) => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const handleSelectPro = () => {
    if (isAuthenticated) {
      navigate('/payments');
    }
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg p-6 max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <h2 className="text-3xl font-bold mb-6 text-center">Choose Your Plan</h2>
        <div className="grid md:grid-cols-2 gap-6">
          <div className="border rounded-lg p-6">
            <h3 className="text-xl font-bold mb-2">Basic</h3>
            <p className="text-3xl font-bold mb-4">FREE</p>
            <ul className="space-y-2 mb-6">
              <li className="flex items-center gap-2">
                <CheckCircle className="w-5 h-5 text-green-500" />
                <span>10 send/accept per day</span>
              </li>
              <li className="flex items-center gap-2">
                <CheckCircle className="w-5 h-5 text-green-500" />
                <span>Story reposts only</span>
              </li>
              <li className="flex items-center gap-2">
                <XCircle className="w-5 h-5 text-gray-400" />
                <span>No analytics</span>
              </li>
            </ul>
            <button className="w-full py-2 bg-gray-200 text-gray-700 rounded-lg" disabled>
              Current Plan
            </button>
          </div>
          <div className="border-2 border-purple-600 rounded-lg p-6 relative">
            <div className="absolute -top-3 left-1/2 -translate-x-1/2 px-3 py-1 bg-gradient-to-r from-purple-600 to-blue-500 text-white text-sm rounded-full">
              Most Popular
            </div>
            <h3 className="text-xl font-bold mb-2">Pro</h3>
            <p className="text-3xl font-bold mb-4">₹999<span className="text-sm font-normal">/month</span></p>
            <p className="text-lg mb-4">or ₹9999<span className="text-sm">/year (Save 17%)</span></p>
            <ul className="space-y-2 mb-6">
              <li className="flex items-center gap-2">
                <CheckCircle className="w-5 h-5 text-green-500" />
                <span>50 send/accept per day</span>
              </li>
              <li className="flex items-center gap-2">
                <CheckCircle className="w-5 h-5 text-green-500" />
                <span>All media types (Story, Post, Reel)</span>
              </li>
              <li className="flex items-center gap-2">
                <CheckCircle className="w-5 h-5 text-green-500" />
                <span>Full analytics dashboard</span>
              </li>
            </ul>
            <button 
              onClick={handleSelectPro}
              className="w-full py-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg hover:shadow-lg transition"
            >
              Select Pro
            </button>
          </div>
        </div>
        <button onClick={onClose} className="w-full mt-6 text-gray-600 hover:text-gray-900">
          Close
        </button>
      </div>
    </div>
  );
};

export default PricingModal;
