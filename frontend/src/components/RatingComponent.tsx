import React, { useState } from 'react';
import '../styles/RatingComponent.css';

interface Props { onRate: (rating: number) => void; }

const RatingComponent: React.FC<Props> = ({ onRate }) => {
  const [rating, setRating] = useState(0);

  const handleRate = (value: number) => {
    setRating(value);
    onRate(value);
  };

  return (
    <div className="rating-component">
      <p>Rate this exchange:</p>
      <div className="stars">
        {[1, 2, 3, 4, 5].map((star) => (
          <button key={star} className={`star ${star <= rating ? 'active' : ''}`} onClick={() => handleRate(star)}>
            ★
          </button>
        ))}
      </div>
    </div>
  );
};

export default RatingComponent;