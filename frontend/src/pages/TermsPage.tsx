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
        <p>
          By accessing and using ShoutX, you agree to be bound by these Terms and Conditions.
          If you do not agree to abide by the above, please do not use this service.
        </p>

        <h2>2. Use License</h2>
        <p>
          Permission is granted to temporarily download one copy of the materials (information
          or software) on ShoutX for personal, non-commercial transitory viewing only. This is
          the grant of a license, not a transfer of title, and under this license you may not:
        </p>
        <ul>
          <li>Modifying or copying the materials</li>
          <li>Using the materials for any commercial purpose or for any public display</li>
          <li>Attempting to decompile or reverse engineer any software contained on ShoutX</li>
          <li>Removing any copyright or other proprietary notations from the materials</li>
          <li>Transferring the materials to another person or "mirroring" the materials on any
          other server</li>
        </ul>

        <h2>3. Disclaimer</h2>
        <p>
          The materials on ShoutX's website are provided for informational purposes only. ShoutX
          makes no warranties, expressed or implied, and hereby disclaims and negates all other
          warranties including, without limitation, implied warranties or conditions of
          merchantability, fitness for a particular purpose, or non-infringement of intellectual
          property or other violation of rights.
        </p>

        <h2>4. Limitations</h2>
        <p>
          In no event shall ShoutX or its suppliers be liable for any damages (including, without
          limitation, damages for loss of data or profit, or due to business interruption)
          arising out of the use or inability to use the materials on ShoutX.
        </p>

        <h2>5. Accuracy of Materials</h2>
        <p>
          The materials appearing on ShoutX could include technical, typographical, or
          photographic errors. ShoutX does not warrant that any of the materials on its website
          are accurate, complete, or current.
        </p>

        <h2>6. Links</h2>
        <p>
          ShoutX has not reviewed all of the sites linked to its website and is not responsible
          for the contents of any such linked site. The inclusion of any link does not imply
          endorsement by ShoutX of the site. Use of any such linked website is at the user's own
          risk.
        </p>

        <h2>7. Modifications</h2>
        <p>
          ShoutX may revise these terms of service for its website at any time without notice.
          By using this website, you are agreeing to be bound by the then current version of
          these terms of service.
        </p>
      </div>
    </div>
    <Footer />
  </>
);

export default TermsPage;