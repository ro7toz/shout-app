import React from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import '../styles/StaticPages.css';

const TermsPage: React.FC = () => (
  <>
    <Header />
    <div className="static-page">
      <h1>Terms and Conditions</h1>
      <div className="content">
        <h2>1. Acceptance of Terms</h2>
        <p>By accessing and using ShoutX, you agree to be bound by these Terms and Conditions.</p>

        <h2>2. Use License</h2>
        <p>Permission is granted to temporarily download one copy of the materials for personal, non-commercial transitory viewing only.</p>

        <h2>3. Disclaimer</h2>
        <p>The materials on ShoutX are provided for informational purposes only.</p>

        <h2>4. Limitations</h2>
        <p>ShoutX shall not be liable for any damages arising out of the use or inability to use the materials.</p>

        <h2>5. Accuracy of Materials</h2>
        <p>The materials appearing on ShoutX could include technical, typographical, or photographic errors.</p>

        <h2>6. Links</h2>
        <p>ShoutX has not reviewed all of the sites linked to its website and is not responsible for the contents.</p>

        <h2>7. Modifications</h2>
        <p>ShoutX may revise these terms of service at any time without notice.</p>
      </div>
    </div>
    <Footer />
  </>
);

export default TermsPage;