import React, { useState } from 'react';
import '../styles/PlansPricingModal.css';

interface Props {
  onClose: () => void;
}

const PlansPricingModal: React.FC<Props> = ({ onClose }) => {
  const [selectedPlan, setSelectedPlan] = useState('pro');

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content plans-modal" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>✕</button>

        <div className="modal-header">
          <h2>Upgrade to Pro</h2>
          <p>Unlock unlimited power to grow your Instagram</p>
        </div>

        <div className="plans-comparison">
          <div className="plan-card basic">
            <h3>Basic</h3>
            <p className="price">FREE</p>
            <ul className="features">
              <li>✓ 10 daily requests</li>
              <li>✓ Stories only</li>
              <li>✓ Community support</li>
            </ul>
            <button className="btn btn-outline" disabled>
              Current Plan
            </button>
          </div>

          <div className="plan-card pro featured">
            <div className="badge">Recommended</div>
            <h3>Pro</h3>
            <p className="price">₹99 <span>/month</span></p>
            <ul className="features">
              <li>✓ 50 daily requests</li>
              <li>✓ All media types</li>
              <li>✓ Advanced analytics</li>
              <li>✓ Priority support</li>
            </ul>
            <button className="btn btn-primary">
              Upgrade Now
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PlansPricingModal;