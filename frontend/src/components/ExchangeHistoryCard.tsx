import React from 'react';
import '../styles/ExchangeHistoryCard.css';

interface Exchange {
  id: number;
  status: string;
  createdAt: string;
  deadline: string;
  user1Name: string;
  user2Name: string;
}

interface Props {
  exchange: Exchange;
}

const ExchangeHistoryCard: React.FC<Props> = ({ exchange }) => (
  <div className="exchange-card">
    <div className="exchange-header">
      <h4>{exchange.user1Name} ↔️ {exchange.user2Name}</h4>
      <span className={`badge ${exchange.status.toLowerCase()}`}>{exchange.status}</span>
    </div>
    <p className="exchange-date">
      {new Date(exchange.createdAt).toLocaleDateString()}
    </p>
    <p className="exchange-deadline">
      Deadline: {new Date(exchange.deadline).toLocaleDateString()}
    </p>
  </div>
);

export default ExchangeHistoryCard;