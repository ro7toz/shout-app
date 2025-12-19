import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import LoginSignupModal from '../components/LoginSignupModal';
import '../styles/HomePage.css';

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [showLoginModal, setShowLoginModal] = useState(false);

  return (
    <>
      <Header />
      <div className="home-page">
        <section className="hero-section">
          <div className="hero-content">
            <h1 className="hero-title">ShoutX - Instagram Shoutout Exchange Platform</h1>
            <p className="hero-subtitle">
              Grow your Instagram reach by exchanging shoutouts with creators
            </p>
            <button
              className="btn btn-primary btn-lg"
              onClick={() => setShowLoginModal(true)}
            >
              Get Started with Instagram
            </button>
          </div>
          <div className="hero-image">
            <img src="/hero-image.png" alt="ShoutX Platform" />
          </div>
        </section>

        <section className="features-section">
          <h2>Why Choose ShoutX?</h2>
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">📱</div>
              <h3>Easy to Use</h3>
              <p>Connect your Instagram and start exchanging shoutouts instantly</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">📈</div>
              <h3>Grow Your Reach</h3>
              <p>Get in front of thousands of new potential followers</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">⭐</div>
              <h3>Safe & Secure</h3>
              <p>Community rating system ensures quality exchanges</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">💎</div>
              <h3>Premium Features</h3>
              <p>Upgrade to Pro for unlimited exchanges and all media types</p>
            </div>
          </div>
        </section>

        <section className="pricing-section">
          <h2>Simple, Transparent Pricing</h2>
          <div className="pricing-grid">
            <div className="pricing-card">
              <h3>Basic</h3>
              <p className="price">FREE</p>
              <ul className="features-list">
                <li>✓ 10 daily requests</li>
                <li>✓ Stories only</li>
                <li>✓ Community support</li>
              </ul>
              <button className="btn btn-outline" onClick={() => setShowLoginModal(true)}>
                Get Started
              </button>
            </div>
            <div className="pricing-card featured">
              <div className="badge">Popular</div>
              <h3>Pro</h3>
              <p className="price">₹99 <span>/month</span></p>
              <ul className="features-list">
                <li>✓ 50 daily requests</li>
                <li>✓ All media types</li>
                <li>✓ Advanced analytics</li>
                <li>✓ Priority support</li>
              </ul>
              <button className="btn btn-primary" onClick={() => setShowLoginModal(true)}>
                Upgrade Now
              </button>
            </div>
          </div>
        </section>

        <section className="faq-section">
          <h2>Frequently Asked Questions</h2>
          <div className="faq-list">
            <details className="faq-item">
              <summary>How does ShoutX work?</summary>
              <p>
                Connect your Instagram account, browse creators, send shoutout requests, and when
                someone accepts, you'll both repost each other's content to grow your reach.
              </p>
            </details>
            <details className="faq-item">
              <summary>Is my data safe?</summary>
              <p>
                Yes. We only access your Instagram username and public profile data. We never
                store your password or post on your behalf without explicit confirmation.
              </p>
            </details>
            <details className="faq-item">
              <summary>How long does an exchange take?</summary>
              <p>
                You have 24 hours to complete the repost after accepting a request. If not
                completed, you'll receive a strike (3 strikes = account deactivation).
              </p>
            </details>
          </div>
        </section>
      </div>
      <Footer />
      {showLoginModal && <LoginSignupModal onClose={() => setShowLoginModal(false)} />}
    </>
  );
};

export default HomePage;