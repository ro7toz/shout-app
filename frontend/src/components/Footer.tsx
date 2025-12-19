import React from 'react';
import '../styles/Footer.css';

const Footer: React.FC = () => (
  <footer className="footer">
    <div className="footer-container">
      <div className="footer-section">
        <h3>ShoutX</h3>
        <p>Grow your Instagram reach through shoutout exchanges</p>
      </div>
      <div className="footer-section">
        <h4>Quick Links</h4>
        <ul>
          <li><a href="/">Home</a></li>
          <li><a href="/terms">Terms</a></li>
          <li><a href="/privacy">Privacy</a></li>
          <li><a href="/refund">Refund Policy</a></li>
        </ul>
      </div>
      <div className="footer-section">
        <h4>Support</h4>
        <ul>
          <li><a href="mailto:support@shoutx.com">support@shoutx.com</a></li>
          <li><a href="mailto:privacy@shoutx.com">privacy@shoutx.com</a></li>
          <li><a href="mailto:refunds@shoutx.com">refunds@shoutx.com</a></li>
        </ul>
      </div>
    </div>
    <div className="footer-bottom">
      <p>&copy; 2025 ShoutX. All rights reserved.</p>
    </div>
  </footer>
);

export default Footer;