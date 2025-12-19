import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/Header';
import Footer from '../components/Footer';
import axios from 'axios';
import '../styles/PaymentsPage.css';

interface Plan {
  id: string;
  name: string;
  price: number;
  dailyRequests: number;
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
      const response = await axios.get(`${API_BASE_URL}/pages/payments/plans`, {
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
        `${API_BASE_URL}/pages/payments/upgrade-to-pro`,
        { paymentMethod },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert('Upgrade initiated!');
    } catch (error) {
      console.error('Error', error);
    }
  };

  if (loading) return <div className="loading">Loading plans...</div>;

  return (
    <>
      <Header />
      <div className="payments-page">
        <h1>Subscription Plans</h1>
        <p>Current Plan: <strong>{user?.planType === 'PRO' ? 'Pro' : 'Basic'}</strong></p>

        <div className="plans-grid">
          {plans.map((plan) => (
            <div key={plan.id} className="plan-card">
              <h3>{plan.name}</h3>
              <p className="price">₹{plan.price}</p>
              <ul className="features">
                {plan.features.map((f, i) => <li key={i}>{f}</li>)}
              </ul>
              <button
                className="btn btn-primary"
                onClick={() => setSelectedPlan(plan.id)}
                disabled={user?.planType === 'PRO'}
              >
                {user?.planType === 'PRO' ? 'Current' : 'Select'}
              </button>
            </div>
          ))}
        </div>

        {selectedPlan && user?.planType !== 'PRO' && (
          <div className="upgrade-form">
            <h2>Payment Method</h2>
            <label>
              <input type="radio" name="payment" value="UPI" checked={paymentMethod === 'UPI'} onChange={(e) => setPaymentMethod(e.target.value)} />
              UPI
            </label>
            <label>
              <input type="radio" name="payment" value="CARD" checked={paymentMethod === 'CARD'} onChange={(e) => setPaymentMethod(e.target.value)} />
              Card
            </label>
            <button className="btn btn-primary" onClick={handleUpgrade}>
              Proceed
            </button>
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default PaymentsPage;