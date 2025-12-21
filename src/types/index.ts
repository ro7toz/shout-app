// Core Types
export interface User {
  id: string;
  name: string;
  username: string;
  email: string;
  profilePicture: string;
  planType: 'BASIC' | 'PRO';
  followers: number;
  accountType: string;
  isVerified: boolean;
  rating: number;
  strikes: number;
  dailyRequestsSent?: number;
  dailyRequestsAccepted?: number;
  mediaItems: MediaItem[];
}

export interface MediaItem {
  id: string;
  url: string;
  type: 'image' | 'video';
  caption?: string;
  thumbnailUrl?: string;
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
  status: 'pending' | 'accepted' | 'completed' | 'expired' | 'rejected';
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
  requesterProfilePicture?: string;
  acceptorId: string;
  acceptorName: string;
  acceptorUsername: string;
  acceptorProfilePicture?: string;
  status: 'INCOMPLETE' | 'COMPLETE' | 'PENDING_VERIFICATION';
  timeStatus: 'LIVE' | 'EXPIRED';
  requesterPosted: boolean;
  acceptorPosted: boolean;
  requesterPostedAt?: string;
  acceptorPostedAt?: string;
  requesterPostUrl?: string;
  acceptorPostUrl?: string;
  expiresAt: string;
  hoursRemaining?: number;
  minutesRemaining?: number;
  rating?: number;
  isPendingFromMe?: boolean;
  canRate?: boolean;
  myRating?: number;
}

export interface Notification {
  id: string;
  type: 'request' | 'acceptance' | 'completion' | 'warning' | 'strike' | 'system';
  message: string;
  title?: string;
  createdAt: string;
  read: boolean;
  relatedId?: string;
  actionUrl?: string;
}

export interface AnalyticsSummary {
  totalExchanges: number;
  completedExchanges: number;
  totalReach: number;
  totalProfileVisits: number;
  totalFollowersGained: number;
  completionRate: number;
  averageRating: number;
  recentExchanges: Exchange[];
}

export interface PaymentDetails {
  id: string;
  transactionId: string;
  amount: number;
  currency: string;
  gateway: 'RAZORPAY' | 'PAYPAL' | 'UPI' | 'PAYTM';
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  planType: string;
  createdAt: string;
}

export interface SearchFilters {
  query?: string;
  genre?: string;
  followers?: string;
  repostType?: 'story' | 'post' | 'reel';
  accountType?: string;
  minRating?: number;
}

export interface PaginationParams {
  page: number;
  pageSize: number;
  total: number;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  error?: string;
}

export interface ApiError {
  message: string;
  code: string;
  details?: Record<string, any>;
}
