import React from 'react';
import '../styles/MediaGrid.css';

interface MediaItem { id: number; url: string; type: string; }
interface Props { mediaItems: MediaItem[]; }

const MediaGrid: React.FC<Props> = ({ mediaItems }) => (
  <div className="media-grid">
    {mediaItems && mediaItems.length > 0 ? (
      mediaItems.map((item) => (
        <div key={item.id} className="media-item">
          <img src={item.url} alt={item.type} />
          <div className="media-badge">{item.type}</div>
        </div>
      ))
    ) : (
      <p>No media</p>
    )}
  </div>
);

export default MediaGrid;