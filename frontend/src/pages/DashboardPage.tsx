import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import Header from '../components/Header';
import Footer from '../components/Footer';
import ReachChart from '../components/charts/ReachChart';
import RepostsChart from '../components/charts/RepostsChart';
import FollowersChart from '../components/charts/FollowersChart';
import ExchangeHistoryCard from '../components/ExchangeHistoryCard';
import PlansPricingModal from '../components/PlansPricingModal';
import axios from 'axios';
import '../styles/DashboardPage.css';

interface Analytics {
  totalReach: number;
  profileVisits: number;
  repostsCount: number;
  completionRate: number;
  followerGrowth: number;
}

const DashboardPage: React.FC = () => {
  const { user, token } = useAuth();
  const [analytics, setAnalytics] = useState<Analytics | null>(null);
  const [exchanges, setExchanges] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showUpgradeModal, setShowUpgradeModal] = useState(false);

  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

  useEffect(() => {
    fetchAnalytics();
  }, [token]);

  const fetchAnalytics = async () => {
    try {
      const analyticsRes = await axios.get(`${API_BASE_URL}/pages/dashboard`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setAnalytics(analyticsRes.data);

      const exchangesRes = await axios.get(`${API_BASE_URL}/shoutouts/exchanges`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setExchanges(exchangesRes.data);
    } catch (error) {
      console.error('Error fetching analytics', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">Loading dashboard...</div>;
  if (user?.planType !== 'PRO')
    return (
      <>
        <Header />
        <div className="dashboard-locked">
          <h2>📊 Advanced Analytics Available for Pro Members</h2>
          <p>Upgrade to Pro to unlock detailed insights about your reach and performance</p>
          <button className="btn btn-primary btn-lg" onClick={() => setShowUpgradeModal(true)}>
            Upgrade to Pro
          </button>
        </div>
        <Footer />
        {showUpgradeModal && <PlansPricingModal onClose={() => setShowUpgradeModal(false)} />}
      </>
    );

  return (
    <>
      <Header />
      <div className="dashboard-page">
        <h1>📊 Your Analytics Dashboard</h1>

        <div className="stats-grid">
          <div className="stat-card">
            <h3>Total Reach</h3>
            <p className="stat-value">{analytics?.totalReach.toLocaleString() || 0}</p>
          </div>
          <div className="stat-card">
            <h3>Profile Visits</h3>
            <p className="stat-value">{analytics?.profileVisits.toLocaleString() || 0}</p>
          </div>
          <div className="stat-card">
            <h3>Reposts</h3>
            <p className="stat-value">{analytics?.repostsCount || 0}</p>
          </div>
          <div className="stat-card">
            <h3>Completion Rate</h3>
            <p className="stat-value">{analytics?.completionRate || 0}%</p>
          </div>
        </div>

        <div className="charts-grid">
          <ReachChart />
          <RepostsChart />
          <FollowersChart />
        </div>

        <div className="exchange-history">
          <h2>Recent Exchanges</h2>
          <div className="exchanges-list">
            {exchanges.map((exchange) => (
              <ExchangeHistoryCard key={exchange.id} exchange={exchange} />
            ))}
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default DashboardPage;