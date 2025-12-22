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
      timeout: 10000,
      withCredentials: true, // ✅ FIX: Enable CORS credentials
    });

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
          console.warn('🔒 Unauthorized - Clearing auth and redirecting');
          localStorage.removeItem('auth_token');
          localStorage.removeItem('user');
          window.location.href = '/';
        }
        return Promise.reject(error);
      }
    );
  }

  // ✅ FIX: Instagram callback handler
  async instagramCallback(code: string, state: string) {
    return this.client.get('/auth/instagram/callback', { 
      params: { code, state } 
    });
  }

  // ✅ FIX: Add remaining methods...
  async searchUsers(params: any) {
    return this.client.get('/users/search', { params });
  }

  async getUserById(userId: string) {
    return this.client.get(`/users/${userId}`);
  }

  async sendRequest(receiverId: string, mediaId: string) {
    return this.client.post('/shoutouts/send', { receiverId, mediaId });
  }

  async getReceivedRequests() {
    return this.client.get('/shoutouts/requests');
  }

  async acceptRequest(requestId: string, selectedMediaUrl: string) {
    return this.client.post(`/shoutouts/requests/${requestId}/accept`, { selectedMediaUrl });
  }

  async getActiveExchanges() {
    return this.client.get('/exchanges/user/active');
  }

  async confirmRepost(exchangeId: string, postUrl: string) {
    return this.client.post(`/exchanges/${exchangeId}/confirm-repost`, { postUrl });
  }

  async getDashboard() {
    return this.client.get('/analytics/dashboard');
  }

  async getNotifications() {
    return this.client.get('/notifications');
  }

  async markNotificationAsRead(notificationId: string) {
    return this.client.put(`/notifications/${notificationId}/read`);
  }
}

export const api = new ApiService();
export default api;
