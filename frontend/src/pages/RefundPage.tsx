import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import '../styles/StaticPages.css';

const RefundPage: React.FC = () => (
  <>
    <Header />
    <div className="static-page">
      <h1>Refund Policy</h1>
      <div className="content">
        <h2>1. Refund Eligibility</h2>
        <p>ShoutX offers refunds for subscription purchases within 7 days of the initial purchase.</p>

        <h2>2. How to Request a Refund</h2>
        <p>To request a refund, log in to your ShoutX account and navigate to Payment Settings.</p>

        <h2>3. Refund Processing</h2>
        <p>Once approved, the refund will be processed within 5-10 business days.</p>

        <h2>4. Exclusions</h2>
        <p>Purchases made more than 7 days ago are not eligible for refunds.</p>

        <h2>5. Cancellation of Subscription</h2>
        <p>You can cancel your subscription at any time. Your access remains active until the end of your billing cycle.</p>

        <h2>6. Contact Support</h2>
        <p>For refund requests, contact refunds@shoutx.com with your order ID.</p>
      </div>
    </div>
    <Footer />
  </>
);

export default RefundPage;