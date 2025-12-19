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
        <p>
          ShoutX offers refunds for subscription purchases within 7 days of the initial purchase.
          After 7 days, no refunds will be provided, regardless of the reason for the cancellation.
        </p>

        <h2>2. How to Request a Refund</h2>
        <p>
          To request a refund, please follow these steps:
        </p>
        <ol>
          <li>Log in to your ShoutX account</li>
          <li>Navigate to Payment Settings</li>
          <li>Click on "Request Refund" for the subscription you wish to refund</li>
          <li>Provide a reason for the refund request</li>
          <li>Submit the request</li>
        </ol>

        <h2>3. Refund Processing</h2>
        <p>
          Once your refund request is approved, the refund will be processed to your original
          payment method within 5-10 business days. Please note that your bank may take
          additional time to reflect the refund in your account.
        </p>

        <h2>4. Exclusions</h2>
        <p>
          The following are not eligible for refunds:
        </p>
        <ul>
          <li>Purchases made more than 7 days ago</li>
          <li>Refunds for services already used or delivered</li>
          <li>Duplicate purchases (we will refund all but one)</li>
          <li>Purchases made in error (please contact support within 24 hours)</li>
        </ul>

        <h2>5. Cancellation of Subscription</h2>
        <p>
          You can cancel your ShoutX subscription at any time. Your access will remain active
          until the end of your current billing cycle, after which your account will revert to
          the Basic plan.
        </p>

        <h2>6. Contact Support</h2>
        <p>
          For refund requests or concerns, please contact our support team at refunds@shoutx.com
          with your order ID and reason for the refund request.
        </p>
      </div>
    </div>
    <Footer />
  </>
);

export default RefundPage;