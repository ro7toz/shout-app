export interface User {
  id: string;
  name: string;
  username: string;
  email: string;
  profilePicture: string;
  planType: 'BASIC' | 'PRO';
  followers: number;
  followerCount: number; // Backend uses this
  accountType: string;
  isVerified: boolean;
  rating: number;
  strikes: number;
  strikeCount: number; // Backend uses this
  dailyRequestsSent: number;
  dailyRequestsAccepted: number;
  mediaItems: MediaItem[];
  instagramId?: string;
  instagramUsername?: string;
  bio?: string;
}

export interface ShoutoutRequest {
  id: string;
  senderId: string;
  senderName: string;
  senderUsername: string;
  senderProfilePicture: string;
  receiverId: string;
  receiverName?: string;
  receiverUsername?: string;
  mediaId: string;
  mediaType: 'STORY' | 'POST' | 'REEL';
  status: 'PENDING' | 'ACCEPTED' | 'COMPLETED' | 'EXPIRED' | 'REJECTED';
  createdAt: string;
  completedAt?: string;
  expiresAt?: string;
  sentByMe: boolean;
  receivedByMe: boolean;
}

export interface Exchange {
  id: string;
  requesterId: string;
  requesterName: string;
  requesterUsername: string;
  requesterProfilePicture: string;
  acceptorId: string;
  acceptorName: string;
  acceptorUsername: string;
  acceptorProfilePicture: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'EXPIRED' | 'CANCELLED';
  timeStatus: 'LIVE' | 'EXPIRED';
  requesterPosted: boolean;
  acceptorPosted: boolean;
  requesterPostedAt?: string;
  acceptorPostedAt?: string;
  requesterPostUrl?: string;
  acceptorPostUrl?: string;
  expiresAt: string;
  hoursRemaining: number;
  minutesRemaining: number;
  rating?: number;
  isPendingFromMe: boolean;
  canRate: boolean;
  myRating?: number;
  createdAt: string;
}
