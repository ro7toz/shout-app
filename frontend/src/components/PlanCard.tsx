import React from 'react';
import '../styles/PlanCard.css';

interface Plan {
  id: string;
  name: string;
  price?: number;
  priceINR?: number;
  dailyRequests: number;
  allowedMediaTypes: string[];
  features: string[];
}

interface PlanCardProps {
  plan: Plan;
  isSelected: boolean;
  isCurrentPlan: boolean;
  onSelect: () => void;
}

const PlanCard: React.FC<PlanCardProps> = ({ plan, isSelected, isCurrentPlan, onSelect }) => (
  <div className={`plan-card ${isSelected ? 'selected' : ''} ${isCurrentPlan ? 'current' : ''}`}>
    <h3>{plan.name}</h3>
    <p className="price">
      {plan.priceINR || plan.price || 0}
      {plan.name === 'Pro' && <span className="period">/month</span>}
    </p>
    <ul className="features">
      {plan.features.map((feature, idx) => (
        <li key={idx}>{feature}</li>
      ))}
    </ul>
    <button
      className={`btn ${isSelected ? 'btn-primary' : 'btn-outline'}`}
      onClick={onSelect}
      disabled={isCurrentPlan}
    >
      {isCurrentPlan ? 'Current Plan' : isSelected ? 'Selected' : 'Select'}
    </button>
  </div>
);

export default PlanCard;