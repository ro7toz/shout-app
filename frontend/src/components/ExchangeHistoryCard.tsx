import React from 'react';
import '../styles/ExchangeHistoryCard.css';

interface Exchange {
  id: number;
  status: string;
  user1Name: string;
  user2Name: string;
  createdAt: string;
}

const ExchangeHistoryCard: React.FC<{ exchange: Exchange }> = ({ exchange }) => (
  <div className="exchange-card">
    <h4>{exchange.user1Name} ↔ {exchange.user2Name}</h4>
    <span className={`badge ${exchange.status.toLowerCase()}`}>{exchange.status}</span>
    <p>{new Date(exchange.createdAt).toLocaleDateString()}</p>
  </div>
);

export default ExchangeHistoryCard;