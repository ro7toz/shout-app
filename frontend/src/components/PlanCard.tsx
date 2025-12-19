import React from 'react';
import '../styles/PlanCard.css';

interface Plan {
  id: string;
  name: string;
  price?: number;
  dailyRequests: number;
  features: string[];
}

interface Props {
  plan: Plan;
  isSelected: boolean;
  isCurrentPlan: boolean;
  onSelect: () => void;
}

const PlanCard: React.FC<Props> = ({ plan, isSelected, isCurrentPlan, onSelect }) => (
  <div className={`plan-card ${isSelected ? 'selected' : ''}`}>
    <h3>{plan.name}</h3>
    <p className="price">₹{plan.price || 0}</p>
    <ul className="features">
      {plan.features.map((f, i) => <li key={i}>{f}</li>)}
    </ul>
    <button className="btn" onClick={onSelect} disabled={isCurrentPlan}>
      {isCurrentPlan ? 'Current' : 'Select'}
    </button>
  </div>
);

export default PlanCard;