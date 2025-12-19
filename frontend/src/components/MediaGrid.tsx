import React from 'react';
import '../styles/MediaGrid.css';

interface MediaItem {
  id: number;
  url: string;
  type: string;
}

interface MediaGridProps {
  mediaItems: MediaItem[];
}

const MediaGrid: React.FC<MediaGridProps> = ({ mediaItems }) => (
  <div className="media-grid">
    {mediaItems && mediaItems.length > 0 ? (
      mediaItems.map((item) => (
        <div key={item.id} className="media-item">
          <img src={item.url} alt={item.type} />
          <div className="media-badge">{item.type}</div>
        </div>
      ))
    ) : (
      <p className="empty-state">No media items yet</p>
    )}
  </div>
);

export default MediaGrid;