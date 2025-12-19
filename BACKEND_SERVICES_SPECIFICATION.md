# ShoutX Backend Services - Complete Specification

## Service Layer Architecture

### 1. UserService
```java
@Service
public class UserService {
    // User registration and profile management
    public User registerUser(String email, String name);
    public User updateProfile(Long userId, UserUpdateDTO dto);
    public User getUserById(Long userId);
    public List<User> searchUsers(SearchFilterDTO filters);
    public void uploadPhotos(Long userId, List<MultipartFile> photos);
    public void deletePhoto(Long userId, Long photoId);
    public Integer getFollowerCount(Long userId); // From Instagram API
}
```

### 2. RequestService
```java
@Service
public class RequestService {
    // Shoutout request management
    public Request createRequest(Long senderId, Long receiverId, Long photoId);
    public void acceptRequest(Long requestId);
    public void completeRequest(Long requestId);
    public void expireRequest(Long requestId);
    public List<Request> getSentRequests(Long userId);
    public List<Request> getReceivedRequests(Long userId);
    public void rateExchange(Long requestId, Integer rating);
    public List<Request> getPendingExpiredRequests(); // For daily task
}
```

### 3. AnalyticsService
```java
@Service
public class AnalyticsService {
    // Analytics tracking and reporting
    public void recordRepost(Long requestId, Long photoId);
    public AnalyticsSummary getDashboardSummary(Long userId);
    public AnalyticsGraphData getGraphData(Long userId, String metric, String period);
    public Integer getReachForPhoto(Long photoId); // From Instagram API
    public Integer getProfileVisits(Long photoId); // From Instagram API
    public Integer getClicksForPhoto(Long photoId); // From Instagram API
    public Integer getFollowersGained(Long userId); // From Instagram API
}
```

### 4. NotificationService
```java
@Service
public class NotificationService {
    // Email and in-app notifications
    public void sendNewRequestNotification(User receiver, User sender);
    public void sendCompletedExchangeNotification(User user);
    public void sendProUpgradeNotification(User user);
    public void sendStrikeWarningNotification(User user, Integer strikeCount);
    public void sendBanNotification(User user);
    public List<Notification> getUserNotifications(Long userId);
    public void markAsRead(Long notificationId);
    public void deleteNotification(Long notificationId);
}
```

### 5. PaymentService
```java
@Service
public class PaymentService {
    // Payment processing and plan upgrades
    public String initiatePayment(Long userId, String planType, String gateway);
    public void verifyPayment(String transactionId, String status);
    public void upgradePlan(Long userId, String planType);
    public List<Payment> getPaymentHistory(Long userId);
    public Boolean hasActiveProPlan(Long userId);
    public Integer getDayRequestLimit(Long userId); // 10 or 50
    public Integer getDayRequestAcceptLimit(Long userId); // 10 or 50
}
```

### 6. StrikeService
```java
@Service
public class StrikeService {
    // Strike tracking and banning
    public void addStrike(Long userId, String reason);
    public Integer getStrikeCount(Long userId);
    public void checkAndBanIfNeeded(Long userId);
    public void banUser(Long userId, String reason);
    public List<User> getDailyExpiredRequests();
}
```

### 7. InstagramService
```java
@Service
public class InstagramService {
    // Instagram API integration
    public InstagramUser getInstagramUser(String accessToken);
    public List<InstagramMedia> getUserMedia(String accessToken);
    public InstagramAnalytics getMediaAnalytics(String mediaId, String accessToken);
    public Integer getFollowerCount(String userId, String accessToken);
}
```

### 8. EmailService
```java
@Service
public class EmailService {
    // Email sending
    public void sendNewRequestEmail(User receiver, User sender);
    public void sendCompletedExchangeEmail(User user);
    public void sendProUpgradeConfirmationEmail(User user, String transactionId);
    public void sendStrikeWarningEmail(User user, Integer strikeCount);
    public void sendBanNotificationEmail(User user);
}
```

---

## API Endpoint Specifications

### Authentication Endpoints

**POST /api/auth/login**
```json
Request: { "email": "user@example.com", "password": "password" }
Response: { "token": "jwt_token", "user": {...} }
```

**POST /api/auth/signup**
```json
Request: { "email": "user@example.com", "password": "password", "name": "Name" }
Response: { "user": {...}, "redirectUrl": "/photo-upload" }
```

**POST /api/auth/oauth/callback?code=...**
```json
Response: { "token": "jwt_token", "user": {...}, "isNewUser": true }
```

### User Endpoints

**GET /api/users/{id}**
```json
Response: {
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "profilePictureUrl": "https://...",
  "followerCount": 10000,
  "accountType": "CREATOR",
  "isVerified": true,
  "rating": 4.8,
  "photos": [...]
}
```

**PUT /api/users/{id}/profile**
```json
Request: { "name": "New Name", "accountType": "INFLUENCER" }
Response: { "user": {...} }
```

**POST /api/users/{id}/photos**
```
Content-Type: multipart/form-data
Form: files[] = [file1, file2, file3]
Response: { "photos": [...] }
```

**DELETE /api/users/{id}/photos/{photoId}**
```json
Response: { "success": true }
```

**GET /api/users/search?genre=LIFESTYLE&followerRange=1k-10k&repostType=STORY**
```json
Response: {
  "users": [...],
  "pagination": { "page": 1, "pageSize": 20, "total": 100 }
}
```

### Request Endpoints

**POST /api/requests**
```json
Request: { "receiverId": 2, "photoId": 5 }
Response: { "request": {...} }
```

**GET /api/requests/received**
```json
Response: {
  "requests": [...],
  "unreadCount": 3
}
```

**GET /api/requests/sent**
```json
Response: { "requests": [...] }
```

**GET /api/requests/{id}**
```json
Response: {
  "id": 1,
  "sender": {...},
  "receiver": {...},
  "photo": {...},
  "status": "PENDING",
  "repostDeadline": "2025-12-21T12:38:00Z",
  "receiverRepostDeadline": null,
  "timeRemaining": "24h 15m"
}
```

**PUT /api/requests/{id}/accept**
```json
Response: { "request": {...}, "deadline": "2025-12-21T12:38:00Z" }
```

**PUT /api/requests/{id}/complete**
```json
Response: { "request": {...}, "analytics": {...} }
```

**POST /api/requests/{id}/rate**
```json
Request: { "rating": 5, "comment": "Great collab!" }
Response: { "success": true }
```

### Analytics Endpoints

**GET /api/analytics/dashboard**
```json
Response: {
  "totalReposts": 25,
  "totalReach": 150000,
  "totalProfileVisits": 5000,
  "totalFollowersGained": 2000,
  "recentExchanges": [...]
}
```

**GET /api/analytics/{userId}/graph?metric=reach&period=month**
```json
Response: {
  "metric": "reach",
  "period": "month",
  "data": [
    { "date": "2025-12-01", "value": 5000 },
    { "date": "2025-12-02", "value": 6500 }
  ]
}
```

### Payment Endpoints

**POST /api/payments/initiate**
```json
Request: { "planType": "PRO", "gateway": "PAYPAL" }
Response: { "paymentUrl": "https://paypal.com/...", "transactionId": "txn_123" }
```

**POST /api/payments/verify**
```json
Request: { "transactionId": "txn_123", "status": "SUCCESS" }
Response: { "success": true, "user": {...} }
```

### Notification Endpoints

**GET /api/notifications**
```json
Response: {
  "notifications": [...],
  "unreadCount": 5
}
```

**PUT /api/notifications/{id}/read**
```json
Response: { "success": true }
```

**DELETE /api/notifications/{id}**
```json
Response: { "success": true }
```

---

## Database Transactions

### Request Completion Transaction
```
BEGIN TRANSACTION
  1. Update request status to COMPLETED
  2. Record analytics data
  3. Send completion notification
  4. Update user ratings
COMMIT
```

### Payment Processing Transaction
```
BEGIN TRANSACTION
  1. Create payment record (PENDING)
  2. Call payment gateway
  3. Verify transaction
  4. Update user plan to PRO
  5. Create payment record (SUCCESS)
  6. Send confirmation email
COMMIT or ROLLBACK
```

### Strike & Ban Transaction
```
BEGIN TRANSACTION
  1. Add strike record
  2. Check strike count
  3. If 3 strikes: Ban user
  4. Block social login
  5. Send notification email
COMMIT
```

---

## Security Considerations

1. **Authentication**
   - JWT tokens with 24-hour expiry
   - Refresh token for renewal
   - OAuth2 for Instagram

2. **Authorization**
   - User can only access own data
   - Admin endpoints require special role
   - Plan-based access control

3. **Data Protection**
   - Passwords hashed with bcrypt
   - SSL/TLS for all communications
   - Rate limiting on auth endpoints
   - CSRF protection on forms

4. **Input Validation**
   - Validate all file uploads
   - Sanitize user input
   - Prevent SQL injection
   - Prevent XSS attacks

---

## Error Handling

### Standard Error Response
```json
{
  "error": {
    "code": "INVALID_REQUEST",
    "message": "User-friendly error message",
    "details": "Technical details if available"
  }
}
```

### Common Error Codes
- `UNAUTHORIZED` (401) - Not authenticated
- `FORBIDDEN` (403) - Not authorized
- `NOT_FOUND` (404) - Resource not found
- `INVALID_REQUEST` (400) - Bad request
- `CONFLICT` (409) - Resource already exists
- `RATE_LIMITED` (429) - Too many requests
- `SERVER_ERROR` (500) - Internal error

---

## Testing Strategy

### Unit Tests
- Service layer methods
- Utility functions
- Validators

### Integration Tests
- API endpoints
- Database transactions
- Service interactions

### E2E Tests
- User signup flow
- Request creation & completion
- Payment processing
- Strike & ban system

### Load Tests
- 1000+ concurrent users
- Peak traffic simulation
- Database performance

---

## Monitoring & Logging

### Key Metrics
- API response times
- Error rates
- Request volumes
- Payment success rate
- Email delivery rate

### Logging
- Request/response logging
- Error stack traces
- Payment transactions
- User actions (audit trail)

---

## Deployment Checklist

- [ ] All services implemented
- [ ] All API endpoints tested
- [ ] Database schema migrated
- [ ] OAuth2 configured
- [ ] Email service configured
- [ ] Payment gateways configured
- [ ] Instagram API credentials set
- [ ] Environment variables configured
- [ ] Security audit passed
- [ ] Load testing passed
- [ ] Monitoring configured
- [ ] Backup strategy implemented
