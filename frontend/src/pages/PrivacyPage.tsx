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
        <p>ShoutX is committed to protecting your privacy.</p>

        <h2>2. Information We Collect</h2>
        <p>We collect Instagram username, profile picture, follower count, and email address.</p>

        <h2>3. Use of Your Information</h2>
        <p>We use the information to create and manage your account and process transactions.</p>

        <h2>4. Disclosure of Your Information</h2>
        <p>We may share information if required by law or with third-party service providers.</p>

        <h2>5. Security of Your Information</h2>
        <p>We use administrative, technical, and physical security measures to protect your information.</p>

        <h2>6. Contact Us</h2>
        <p>For questions, contact us at privacy@shoutx.com</p>
      </div>
    </div>
    <Footer />
  </>
);

export default PrivacyPage;