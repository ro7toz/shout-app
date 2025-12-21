import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { CheckCircle } from 'lucide-react';
import Header from '../components/ui/Header';
import Footer from '../components/ui/Footer';
import { api } from '../services/api';

export const PaymentsPage = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState<'monthly' | 'yearly'>('monthly');

  const handleUpgrade = async (gateway: 'RAZORPAY' | 'PAYPAL') => {
    try {
      setLoading(true);
      const response = await api.initiatePayment(selectedPlan === 'monthly' ? 'PRO_MONTHLY' : 'PRO_YEARLY', gateway);
      window.location.href = response.data.paymentUrl;
    } catch (error) {
      console.error('Payment initiation failed:', error);
      alert('Failed to initiate payment. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (user?.planType === 'PRO') {
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <div className="container mx-auto px-4 py-8 flex-1">
          <div className="bg-white rounded-lg p-8 max-w-2xl mx-auto text-center">
            <CheckCircle className="w-20 h-20 mx-auto mb-4 text-green-500" />
            <h2 className="text-2xl font-bold mb-2">You're already on Pro! 🎉</h2>
            <p className="text-gray-600">Enjoy all premium features</p>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <div className="container mx-auto px-4 py-8 flex-1">
        <h1 className="text-4xl font-bold text-center mb-8">Upgrade to Pro</h1>

        <div className="flex justify-center gap-4 mb-8 flex-wrap">
          <button
            onClick={() => setSelectedPlan('monthly')}
            className={`px-6 py-3 rounded-lg font-medium transition ${
              selectedPlan === 'monthly'
                ? 'bg-gradient-to-r from-purple-600 to-blue-500 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Monthly - ₹499
          </button>
          <button
            onClick={() => setSelectedPlan('yearly')}
            className={`px-6 py-3 rounded-lg font-medium transition ${
              selectedPlan === 'yearly'
                ? 'bg-gradient-to-r from-purple-600 to-blue-500 text-white'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            Yearly - ₹4,999 <span className="ml-2 text-green-600">(Save 17%)</span>
          </button>
        </div>

        <div className="bg-white rounded-lg p-8 max-w-2xl mx-auto">
          <h3 className="text-xl font-bold mb-6">Pro Plan Features</h3>
          <div className="space-y-4 mb-8">
            <div className="flex items-start gap-3">
              <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
              <div>
                <p className="font-semibold">50 requests per day</p>
                <p className="text-sm text-gray-600">10x more than Basic</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
              <div>
                <p className="font-semibold">All media types</p>
                <p className="text-sm text-gray-600">Stories, Posts, and Reels</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
              <div>
                <p className="font-semibold">Advanced Analytics Dashboard</p>
                <p className="text-sm text-gray-600">Track impressions, clicks, and growth</p>
              </div>
            </div>
          </div>

          <div className="space-y-3">
            <button 
              onClick={() => handleUpgrade('RAZORPAY')}
              disabled={loading}
              className="w-full py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg font-medium hover:shadow-lg transition disabled:opacity-50"
            >
              {loading ? 'Processing...' : 'Pay with Razorpay'}
            </button>
            <button 
              onClick={() => handleUpgrade('PAYPAL')}
              disabled={loading}
              className="w-full py-3 border-2 border-gray-300 text-gray-700 rounded-lg font-medium hover:border-purple-600 transition disabled:opacity-50"
            >
              Pay with PayPal
            </button>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};
