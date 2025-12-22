import axios, { AxiosInstance, AxiosError } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

class ApiService {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: 15000,
      withCredentials: false, // Set to true only if using cookies
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('auth_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        console.log('🚀 API Request:', config.method?.toUpperCase(), config.url);
        return config;
      },
      (error) => {
        console.error('❌ Request Error:', error);
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response) => {
        console.log('✅ API Response:', response.config.url, response.status);
        return response;
      },
      (error: AxiosError) => {
        console.error('❌ Response Error:', error.response?.status, error.response?.data);
        
        if (error.response?.status === 401) {
          console.warn('🔒 Unauthorized - Clearing auth');
          localStorage.removeItem('auth_token');
          localStorage.removeItem('user');
          window.location.href = '/';
        }
        
        return Promise.reject(error);
      }
    );
  }

  // Authentication
  async instagramCallback(code: string, state: string) {
    return this.client.get('/auth/instagram/callback', { 
      params: { code, state } 
    });
  }

  // User endpoints
  async searchUsers(params: any) {
    return this.client.get('/users/search', { params });
  }

  async getUserById(userId: string) {
    return this.client.get(`/users/${userId}`);
  }

  async getCurrentUser() {
    return this.client.get('/users/me');
  }

  // Shoutout endpoints
  async sendRequest(receiverId: string, mediaId: string) {
    return this.client.post('/shoutouts/send', { 
      recipientId: receiverId, 
      mediaId 
    });
  }

  async getReceivedRequests() {
    return this.client.get('/shoutouts/requests', {
      params: { page: 0, pageSize: 20 }
    });
  }

  async getSentRequests() {
    return this.client.get('/shoutouts/requests', {
      params: { page: 0, pageSize: 20, sent: true }
    });
  }

  async acceptRequest(requestId: string, selectedMediaUrl: string) {
    return this.client.post(`/shoutouts/requests/${requestId}/accept`, { 
      selectedMediaUrl 
    });
  }

  // Exchange endpoints
  async getActiveExchanges() {
    return this.client.get('/exchanges/user/active');
  }

  async getExchangeById(exchangeId: string) {
    return this.client.get(`/exchanges/${exchangeId}`);
  }

  async confirmRepost(exchangeId: string, postUrl: string) {
    return this.client.post(`/exchanges/${exchangeId}/confirm-repost`, { postUrl });
  }

  async rateExchange(exchangeId: string, rating: number, ratedUserId: string, review?: string) {
    return this.client.post(`/exchanges/${exchangeId}/rate`, {
      rating,
      ratedUserId,
      review
    });
  }

  // Analytics endpoints
  async getDashboard() {
    return this.client.get('/analytics/dashboard');
  }

  // Notification endpoints
  async getNotifications() {
    return this.client.get('/notifications');
  }

  async markNotificationAsRead(notificationId: string) {
    return this.client.put(`/notifications/${notificationId}/read`);
  }

  async markAllNotificationsAsRead() {
    return this.client.post('/notifications/mark-all-read');
  }

  // Payment endpoints
  async initiatePayment(planType: string, gateway: string) {
    return this.client.post('/payments/upgrade-to-pro', {
      billingCycle: planType,
      gateway
    });
  }

  async verifyPayment(orderId: string, paymentId: string) {
    return this.client.post('/payments/verify', {
      orderId,
      paymentId
    });
  }
}

export const api = new ApiService();
export default api;
