import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/Header';
import Footer from '../components/Footer';
import PlanCard from '../components/PlanCard';
import axios from 'axios';
import '../styles/PaymentsPage.css';

interface Plan {
  id: string;
  name: string;
  price: number;
  dailyRequests: number;
  allowedMediaTypes: string[];
  features: string[];
}

const PaymentsPage: React.FC = () => {
  const { user, token } = useAuth();
  const [plans, setPlans] = useState<Plan[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedPlan, setSelectedPlan] = useState<string | null>(null);
  const [paymentMethod, setPaymentMethod] = useState('UPI');

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  useEffect(() => {
    fetchPlans();
  }, [token]);

  const fetchPlans = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/payments/plans`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPlans(response.data);
    } catch (error) {
      console.error('Error fetching plans', error);
    } finally {
      setLoading(false);
    }
  };

  const handleUpgrade = async () => {
    if (!selectedPlan) return;
    try {
      await axios.post(
        `${API_BASE_URL}/payments/upgrade-to-pro`,
        { paymentMethod },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert('Upgrade initiated! Redirecting to payment...');
    } catch (error) {
      console.error('Error initiating upgrade', error);
    }
  };

  if (loading) return <div className="loading">Loading plans...</div>;

  return (
    <>
      <Header />
      <div className="payments-page">
        <h1>💳 Subscription Plans</h1>
        <p className="subtitle">Choose the perfect plan for your needs</p>

        <div className="current-plan">
          <p>Current Plan: <strong>{user?.planType === 'PRO' ? 'Pro' : 'Basic'}</strong></p>
        </div>

        <div className="plans-grid">
          {plans.map((plan) => (
            <PlanCard
              key={plan.id}
              plan={plan}
              isSelected={selectedPlan === plan.id}
              isCurrentPlan={plan.name === user?.planType}
              onSelect={() => setSelectedPlan(plan.id)}
            />
          ))}
        </div>

        {selectedPlan && selectedPlan !== 'basic' && user?.planType !== 'PRO' && (
          <div className="upgrade-form">
            <h2>Select Payment Method</h2>
            <div className="payment-methods">
              <label>
                <input
                  type="radio"
                  name="payment"
                  value="UPI"
                  checked={paymentMethod === 'UPI'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                📱 UPI (Google Pay, PhonePe, Paytm)
              </label>
              <label>
                <input
                  type="radio"
                  name="payment"
                  value="CARD"
                  checked={paymentMethod === 'CARD'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                💳 Credit/Debit Card
              </label>
              <label>
                <input
                  type="radio"
                  name="payment"
                  value="PAYPAL"
                  checked={paymentMethod === 'PAYPAL'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                />
                🅿️ PayPal
              </label>
            </div>
            <button className="btn btn-primary btn-lg" onClick={handleUpgrade}>
              Proceed to Payment
            </button>
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default PaymentsPage;