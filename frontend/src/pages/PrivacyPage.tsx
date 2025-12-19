import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import '../styles/StaticPages.css';

const PrivacyPage: React.FC = () => (
  <>
    <Header />
    <div className="static-page">
      <h1>Privacy Policy</h1>
      <div className="content">
        <h2>1. Introduction</h2>
        <p>
          ShoutX is committed to protecting your privacy. This Privacy Policy explains how we
          collect, use, disclose, and safeguard your information when you use our website and
          services.
        </p>

        <h2>2. Information We Collect</h2>
        <p>We may collect information about you in a variety of ways, including:</p>
        <ul>
          <li><strong>Information You Provide:</strong> Instagram username, profile picture, follower count,
          account type, and email address.</li>
          <li><strong>Automatically Collected Information:</strong> We may automatically collect certain
          information about your device when you use ShoutX, including IP address, browser type,
          operating system, referring URL, and pages visited.</li>
        </ul>

        <h2>3. Use of Your Information</h2>
        <p>We may use the information we collect from you to:</p>
        <ul>
          <li>Create and manage your ShoutX account</li>
          <li>Process your transactions and send related information</li>
          <li>Email regarding your account or order</li>
          <li>Fulfill and manage purchases, orders, payments, and other transactions related to our services</li>
          <li>Generate a personal profile about you</li>
          <li>Increase the efficiency and operation of our site</li>
          <li>Monitor and analyze usage and trends to improve your experience with the site</li>
        </ul>

        <h2>4. Disclosure of Your Information</h2>
        <p>
          We may share or disclose your information in the following situations:
        </p>
        <ul>
          <li><strong>By Law or to Protect Rights:</strong> If we believe the release of information is
          necessary to comply with the law.</li>
          <li><strong>Third-Party Service Providers:</strong> We may share your information with third parties
          that perform services for us or on our behalf.</li>
        </ul>

        <h2>5. Security of Your Information</h2>
        <p>
          We use administrative, technical, and physical security measures to help protect your
          personal information. However, security cannot be guaranteed, and any transmission of
          data is at your own risk.
        </p>

        <h2>6. Contact Us</h2>
        <p>
          If you have questions or concerns about this Privacy Policy, please contact us at
          privacy@shoutx.com.
        </p>
      </div>
    </div>
    <Footer />
  </>
);

export default PrivacyPage;