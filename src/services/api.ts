import axios, { AxiosInstance, AxiosError } from 'axios';
import type { User, ShoutoutRequest, Exchange, Notification, AnalyticsSummary, SearchFilters } from '../types';


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
    });

    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('auth_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('auth_token');
          localStorage.removeItem('user');
          window.location.href = '/';
        }
        return Promise.reject(error);
      }
    );
  }

  async instagramCallback(code: string, state: string) {
    return this.client.get(`/auth/callback/instagram`, { params: { code, state } });
  }

  async selectMedia(userId: string, mediaIds: string[]) {
    return this.client.post('/auth/select-media', { userId, mediaIds });
  }

  async searchUsers(params: SearchFilters) {
    return this.client.get<{ users: User[]; total: number }>('/users/search', { params });
  }

  async getUserById(userId: string) {
    return this.client.get<User>(`/users/${userId}`);
  }

  async getUserMedia(userId: string) {
    return this.client.get(`/users/${userId}/media`);
  }

  async updateProfile(userId: string, data: Partial<User>) {
    return this.client.put(`/users/${userId}/profile`, data);
  }

  async uploadPhotos(userId: string, files: File[]) {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    return this.client.post(`/users/${userId}/photos`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  }

  async deletePhoto(userId: string, photoId: string) {
    return this.client.delete(`/users/${userId}/photos/${photoId}`);
  }

  async sendRequest(receiverId: string, photoId: string) {
    return this.client.post<ShoutoutRequest>('/requests', { receiverId, photoId });
  }

  async getReceivedRequests() {
    return this.client.get<{ requests: ShoutoutRequest[]; unreadCount: number }>('/requests/received');
  }

  async getSentRequests() {
    return this.client.get<{ requests: ShoutoutRequest[] }>('/requests/sent');
  }

  async getRequestById(requestId: string) {
    return this.client.get<ShoutoutRequest>(`/requests/${requestId}`);
  }

  async acceptRequest(requestId: string) {
    return this.client.put<{ request: ShoutoutRequest; deadline: string }>(`/requests/${requestId}/accept`);
  }

  async completeRequest(requestId: string) {
    return this.client.put(`/requests/${requestId}/complete`);
  }

  async rateRequest(requestId: string, rating: number, comment?: string) {
    return this.client.post(`/requests/${requestId}/rate`, { rating, comment });
  }

  async getExchange(exchangeId: string) {
    return this.client.get<Exchange>(`/exchanges/${exchangeId}`);
  }

  async confirmRepost(exchangeId: string, postUrl: string) {
    return this.client.post(`/exchanges/${exchangeId}/confirm-repost`, { postUrl });
  }

  async rateExchange(exchangeId: string, rating: number, ratedUserId: string, review?: string) {
    return this.client.post(`/exchanges/${exchangeId}/rate`, { rating, ratedUserId, review });
  }

  async getActiveExchanges() {
    return this.client.get<Exchange[]>('/exchanges/user/active');
  }

  async getDashboard() {
    return this.client.get<AnalyticsSummary>('/analytics/dashboard');
  }

  async getDashboardByMonth(month: string) {
    return this.client.get(`/analytics/dashboard/monthly/${month}`);
  }

  async getDashboardByType(type: 'story' | 'post' | 'reel') {
    return this.client.get(`/analytics/dashboard/by-type/${type}`);
  }

  async getGraphData(userId: string, metric: string, period: string) {
    return this.client.get(`/analytics/${userId}/graph`, { params: { metric, period } });
  }

  async initiatePayment(planType: string, gateway: string) {
    return this.client.post<{ paymentUrl: string; transactionId: string }>('/payments/initiate', { planType, gateway });
  }

  async verifyPayment(transactionId: string, status: string) {
    return this.client.post('/payments/verify', { transactionId, status });
  }

  async getPaymentHistory() {
    return this.client.get('/payments/history');
  }

  async getCurrentSubscription() {
    return this.client.get('/subscriptions/current');
  }

  async checkProStatus() {
    return this.client.get<{ isPro: boolean }>('/subscriptions/is-pro');
  }

  async upgradeToProMonthly() {
    return this.client.post('/subscriptions/upgrade/monthly');
  }

  async upgradeToProYearly() {
    return this.client.post('/subscriptions/upgrade/yearly');
  }

  async cancelSubscription() {
    return this.client.delete('/subscriptions/cancel');
  }

  async getNotifications() {
    return this.client.get<{ notifications: Notification[]; unreadCount: number }>('/notifications');
  }

  async markNotificationAsRead(notificationId: string) {
    return this.client.put(`/notifications/${notificationId}/read`);
  }

  async deleteNotification(notificationId: string) {
    return this.client.delete(`/notifications/${notificationId}`);
  }

  async rateUser(ratedUserId: string, rating: number, review?: string) {
    return this.client.post('/ratings/rate', { ratedUserId, rating, review });
  }

  async getUserRatings(username: string) {
    return this.client.get(`/ratings/user/${username}`);
  }

  async getCategoryRatings(username: string, category: string) {
    return this.client.get(`/ratings/user/${username}/category/${category}`);
  }

  async healthCheck() {
    return this.client.get('/health');
  }
}

export const api = new ApiService();
export default api;
