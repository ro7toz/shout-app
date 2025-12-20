import React, { useState } from 'react';
import LoginSignupModal from '../components/LoginSignupModal';
import PlansPricingModal from '../components/PlansPricingModal';
import '../styles/HomePage.css';

const HomePage: React.FC = () => {
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showPricingModal, setShowPricingModal] = useState(false);

  return (
    <div className="home-page-logged-out">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <h1 className="hero-title">Exchange Instagram Shoutouts. Grow Together.</h1>
          <p className="hero-subtitle">
            Connect with creators, repost each other's content, and grow your audience organically.
          </p>
          <div className="hero-cta-buttons">
            <button
              className="btn btn-primary btn-lg"
              onClick={() => setShowLoginModal(true)}
            >
              🚀 Get Started for Free
            </button>
            <button
              className="btn btn-outline btn-lg"
              onClick={() => setShowPricingModal(true)}
            >
              💰 View Plans & Pricing
            </button>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="how-it-works-section">
        <h2>How ShoutX Works</h2>
        <div className="steps-container">
          {/* Step 1 */}
          <div className="step">
            <div className="step-number">1</div>
            <h3>Sign Up</h3>
            <p>Connect your Instagram account and add your favorite content to your profile.</p>
            <div className="step-icon">📱</div>
          </div>

          {/* Step 2 */}
          <div className="step">
            <div className="step-number">2</div>
            <h3>Send Shoutouts</h3>
            <p>Browse creators, find ones you like, and request them to repost your content.</p>
            <div className="step-icon">👫</div>
          </div>

          {/* Step 3 */}
          <div className="step">
            <div className="step-number">3</div>
            <h3>Grow Together</h3>
            <p>Both repost each other's content and watch your followers grow organically.</p>
            <div className="step-icon">📈</div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features-section">
        <h2>Why Choose ShoutX?</h2>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">✨</div>
            <h3>Organic Growth</h3>
            <p>Real followers from creators who genuinely like your content.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🎯</div>
            <h3>Smart Matching</h3>
            <p>Connected with creators in your niche and similar follower count.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">⭐</div>
            <h3>Community Ratings</h3>
            <p>Rate creators and build trust in the community.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📊</div>
            <h3>Analytics</h3>
            <p>Track your growth with detailed insights (PRO only).</p>
          </div>
        </div>
      </section>

      {/* FAQs Section */}
      <section className="faqs-section">
        <h2>Frequently Asked Questions</h2>
        <div className="faqs-container">
          {/* FAQ 1 */}
          <details className="faq-item">
            <summary>
              <h4>What is a Shoutout Exchange?</h4>
              <span className="expand-icon">+</span>
            </summary>
            <p>
              A shoutout exchange is when two creators agree to repost each other's content on their Instagram
              accounts (Stories, Posts, or Reels). This helps both creators reach new audiences and grow their
              followers organically.
            </p>
          </details>

          {/* FAQ 2 */}
          <details className="faq-item">
            <summary>
              <h4>How does the 24-hour rule work?</h4>
              <span className="expand-icon">+</span>
            </summary>
            <p>
              When you accept a repost request, you have 24 hours to repost the other creator's content. If you
              don't complete it within 24 hours, you receive a strike. Three strikes result in a permanent ban.
            </p>
          </details>

          {/* FAQ 3 */}
          <details className="faq-item">
            <summary>
              <h4>What's the difference between BASIC and PRO?</h4>
              <span className="expand-icon">+</span>
            </summary>
            <p>
              BASIC (Free): 10 exchanges/day, Stories only, no analytics. PRO (₹999/month): 50 exchanges/day, all
              media types (Stories, Posts, Reels), full analytics dashboard.
            </p>
          </details>

          {/* FAQ 4 */}
          <details className="faq-item">
            <summary>
              <h4>What happens if I get 3 strikes?</h4>
              <span className="expand-icon">+</span>
            </summary>
            <p>
              Your account gets permanently banned. We also blacklist your Instagram ID, so you cannot create a
              new account with the same Instagram handle. This ensures fair play for all creators.
            </p>
          </details>

          {/* FAQ 5 */}
          <details className="faq-item">
            <summary>
              <h4>How are creators matched?</h4>
              <span className="expand-icon">+</span>
            </summary>
            <p>
              Creators are matched based on similar follower counts. This ensures fair and authentic exchanges
              between creators of similar influence levels.
            </p>
          </details>
        </div>
      </section>

      {/* CTA Footer Section */}
      <section className="cta-footer-section">
        <h2>Ready to Grow Your Audience?</h2>
        <p>Join thousands of creators already using ShoutX to grow organically.</p>
        <button
          className="btn btn-primary btn-lg"
          onClick={() => setShowLoginModal(true)}
        >
          🚀 Start for Free
        </button>
      </section>

      {/* Login Modal */}
      {showLoginModal && (
        <LoginSignupModal onClose={() => setShowLoginModal(false)} />
      )}

      {/* Pricing Modal */}
      {showPricingModal && (
        <PlansPricingModal onClose={() => setShowPricingModal(false)} />
      )}
    </div>
  );
};

export default HomePage;
