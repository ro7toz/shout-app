import React from 'react';
import '../styles/PlansPricingModal.css';

interface Props { onClose: () => void; }

const PlansPricingModal: React.FC<Props> = ({ onClose }) => {
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>

        <div className="modal-header">
          <h2>Upgrade to Pro</h2>
        </div>

        <div className="plans-comparison">
          <div className="plan-card basic">
            <h3>Basic</h3>
            <p className="price">FREE</p>
            <ul className="features">
              <li>10 daily requests</li>
              <li>Stories only</li>
            </ul>
          </div>

          <div className="plan-card pro featured">
            <h3>Pro</h3>
            <p className="price">₹99 <span>/month</span></p>
            <ul className="features">
              <li>50 daily requests</li>
              <li>All media types</li>
              <li>Advanced analytics</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PlansPricingModal;