import React from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircle, XCircle, ChevronDown } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface PricingModalProps {
  onClose: () => void;
}

const PricingModal = ({ onClose }: PricingModalProps) => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-y-auto" onClick={onClose}>
      <div className="bg-white rounded-xl p-8 max-w-4xl w-full my-8" onClick={e => e.stopPropagation()}>
        <h2 className="text-4xl font-bold mb-8 text-center">Choose Your Plan</h2>
        <div className="grid md:grid-cols-2 gap-8">
          <div className="border-2 rounded-xl p-8 hover:shadow-lg transition">
            <h3 className="text-2xl font-bold mb-4">Basic</h3>
            <p className="text-4xl font-bold mb-6">FREE</p>
            <ul className="space-y-3 mb-8">
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span>10 send/accept per day</span>
              </li>
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span>Story reposts only</span>
              </li>
              <li className="flex items-start gap-3">
                <XCircle className="w-5 h-5 text-gray-400 mt-0.5" />
                <span className="text-gray-500">No analytics</span>
              </li>
            </ul>
            <button className="w-full py-3 bg-gray-100 text-gray-500 rounded-lg font-medium" disabled>
              Current Plan
            </button>
          </div>
          <div className="border-2 border-purple-600 rounded-xl p-8 relative hover:shadow-xl transition bg-gradient-to-b from-purple-50 to-white">
            <div className="absolute -top-4 left-1/2 -translate-x-1/2 px-4 py-1 bg-gradient-to-r from-purple-600 to-blue-500 text-white text-sm rounded-full font-medium">
              Most Popular
            </div>
            <h3 className="text-2xl font-bold mb-4">Pro ⭐</h3>
            <p className="text-4xl font-bold mb-2">₹999<span className="text-lg font-normal text-gray-600">/month</span></p>
            <p className="text-lg text-gray-600 mb-6">or ₹9,999/year <span className="text-green-600 font-semibold">(Save 17%)</span></p>
            <ul className="space-y-3 mb-8">
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span className="font-medium">50 send/accept per day</span>
              </li>
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span className="font-medium">All media types (Story, Post, Reel)</span>
              </li>
              <li className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
                <span className="font-medium">Full analytics dashboard</span>
              </li>
            </ul>
            <button 
              onClick={() => {
                if (isAuthenticated) navigate('/payments');
                onClose();
              }}
              className="w-full py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg hover:shadow-lg transition font-medium"
            >
              Select Pro →
            </button>
          </div>
        </div>
        <button onClick={onClose} className="w-full mt-6 text-gray-600 hover:text-gray-900 transition">
          Close
        </button>
      </div>
    </div>
  );
};

export default PricingModal;
