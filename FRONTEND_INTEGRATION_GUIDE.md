# ShoutX Frontend Integration Guide

## Overview
This guide explains how to integrate the React/TypeScript frontend with the Spring Boot backend for the ShoutX application.

## Project Structure

```
shout-app/
├── src/main/java/com/shout/
│   ├── api/                    # REST API endpoints
│   │   ├── PageController.java  # Page-level data endpoints
│   │   ├── AuthController.java  # Authentication endpoints
│   │   ├── UserController.java  # User profile endpoints
│   │   ├── ShoutoutController.java  # Shoutout exchange endpoints
│   │   └── PaymentController.java   # Payment endpoints
│   ├── dto/                    # Data Transfer Objects
│   ├── model/                  # JPA Entities
│   ├── service/                # Business logic
│   ├── repository/             # Database access
│   ├── controller/             # MVC Controllers
│   └── util/                   # Utility classes
│
├── src/main/resources/
│   ├── application.yml         # Spring Boot configuration
│   ├── static/
│   │   ├── sitemap.xml         # SEO sitemap
│   │   └── robots.txt          # Search engine robots
│   └── templates/              # Thymeleaf templates (if needed)
│
└── FRONTEND_INTEGRATION_GUIDE.md  # This file
```

## API Endpoints

### Authentication Endpoints

#### POST /api/auth/login
Login with Instagram OAuth
```json
{
  "instagramToken": "string",
  "instagramUsername": "string"
}

Response:
{
  "token": "jwt_token",
  "user": { UserProfileDTO },
  "expiresIn": 3600
}
```

#### POST /api/auth/signup
Signup with Instagram OAuth
```json
{
  "instagramToken": "string",
  "instagramUsername": "string",
  "accountType": "Creator|Business|Personal|Influencer"
}

Response:
{
  "token": "jwt_token",
  "user": { UserProfileDTO },
  "expiresIn": 3600
}
```

#### POST /api/auth/logout
Logout current user
```
Headers: Authorization: Bearer jwt_token
Response: { "message": "Logged out successfully" }
```

#### GET /api/auth/me
Get current user profile
```
Headers: Authorization: Bearer jwt_token
Response: { UserProfileDTO }
```

### Page Data Endpoints

#### GET /api/pages/home
Get homepage data (auto-detects if logged in)
```json
Response:
{
  "isLoggedIn": boolean,
  "page": "home-logged-out|home-logged-in",
  "user": { UserProfileDTO } // Only if logged in
}
```

#### GET /api/pages/dashboard
Get dashboard page data (Pro analytics, exchanges)
```
Headers: Authorization: Bearer jwt_token
Response:
{
  "user": { UserProfileDTO },
  "isPro": boolean,
  "exchanges": [ { ExchangeDTO } ]
}
```

#### GET /api/pages/profile/{userId}
Get user profile page data
```
Response:
{
  "id": long,
  "name": "string",
  "username": "@username",
  "profilePicture": "url",
  "followers": integer,
  "isVerified": boolean,
  "rating": float,
  "accountType": "string",
  "mediaItems": [ { UserMediaDTO } ],
  "isOwnProfile": boolean
}
```

#### GET /api/pages/notifications
Get notifications page data
```
Headers: Authorization: Bearer jwt_token
Response:
{
  "notifications": [ { NotificationDTO } ],
  "unreadCount": integer
}
```

#### GET /api/pages/payments
Get payment page data
```
Headers: Authorization: Bearer jwt_token
Response:
{
  "currentPlan": "BASIC|PRO",
  "plans": [
    {
      "name": "string",
      "price": "string",
      "currency": "INR|USD",
      "period": "month|year",
      "features": [ "string" ]
    }
  ]
}
```

#### GET /api/pages/static/{page}
Get static page content (terms, privacy, refund)
```
Path params: page = "terms|privacy|refund"
Response:
{
  "title": "string",
  "content": "markdown string"
}
```

### User Endpoints

#### GET /api/users/{userId}
Get user profile details
```
Response: { UserProfileDTO }
```

#### PUT /api/users/{userId}
Update user profile
```
Headers: Authorization: Bearer jwt_token
Request:
{
  "name": "string",
  "accountType": "string",
  "profilePicture": "url"
}
Response: { UserProfileDTO }
```

#### GET /api/users/search?query=...&genre=...&followers=...
Search users with filters
```
Query params:
  - query: "string" (username or name)
  - genre: "Creator|Business|Personal|Influencer"
  - followers: "0-99|100-499|500-999|1k-2.5k|2.5k-5k|5k-10k|10k-25k|25k-50k|50k-100k|100k-250k|250k-500k|500k-1M|1M-2.5M|2.5M-5M"

Response: [ { UserProfileDTO } ]
```

#### GET /api/users/{userId}/media
Get user's media items
```
Response: [ { UserMediaDTO } ]
```

#### POST /api/users/{userId}/media
Upload new media
```
Headers: Authorization: Bearer jwt_token
Request: FormData with file
Response: { UserMediaDTO }
```

#### DELETE /api/users/{userId}/media/{mediaId}
Delete media item
```
Headers: Authorization: Bearer jwt_token
Response: { "message": "Media deleted" }
```

### Shoutout Exchange Endpoints

#### POST /api/shoutouts/send
Send a shoutout request
```
Headers: Authorization: Bearer jwt_token
Request:
{
  "receiverId": long,
  "mediaId": long,
  "repostType": "story|post|reel"
}
Response: { ShoutoutRequestDTO }
```

#### GET /api/shoutouts/requests
Get incoming shoutout requests
```
Headers: Authorization: Bearer jwt_token
Response: [ { ShoutoutRequestDTO } ]
```

#### POST /api/shoutouts/requests/{requestId}/accept
Accept a shoutout request and select media to repost
```
Headers: Authorization: Bearer jwt_token
Request:
{
  "selectedMediaId": long
}
Response: { ExchangeDTO }
```

#### GET /api/shoutouts/exchanges
Get exchange history
```
Headers: Authorization: Bearer jwt_token
Response: [ { ExchangeDTO } ]
```

#### GET /api/shoutouts/exchanges/{exchangeId}
Get exchange details
```
Response: { ExchangeDTO }
```

#### POST /api/shoutouts/exchanges/{exchangeId}/rate
Rate a completed exchange
```
Headers: Authorization: Bearer jwt_token
Request:
{
  "rating": 1-5,
  "review": "optional string"
}
Response: { "message": "Rating recorded" }
```

### Payment Endpoints

#### POST /api/payments/create-order
Create payment order for Pro upgrade
```
Headers: Authorization: Bearer jwt_token
Request:
{
  "planType": "PRO",
  "period": "monthly|yearly",
  "currency": "INR|USD"
}
Response:
{
  "orderId": "string",
  "amount": double,
  "currency": "string",
  "gateway": "upi|paypal|paytm"
}
```

#### POST /api/payments/verify
Verify payment and upgrade plan
```
Headers: Authorization: Bearer jwt_token
Request:
{
  "orderId": "string",
  "paymentId": "string",
  "signature": "string"
}
Response:
{
  "success": boolean,
  "user": { UserProfileDTO }
}
```

## Frontend Component Structure

### Pages

1. **HomePage** (`/`)
   - For logged out users
   - Shows features, FAQs, pricing
   - Login/SignUp buttons

2. **HomePageLoggedIn** (`/home`)
   - Send ShoutOuts tab
   - Requests tab
   - User cards with filter/sort
   - Notifications dot

3. **ProfilePage** (`/profile/me` and `/profile/:userId`)
   - User info with DP, name, followers
   - Media grid (clickable for reposting)
   - Edit (if own profile) or Flag button
   - Ratings display

4. **DashboardPage** (`/dashboard`)
   - Pro analytics charts
   - Exchange history
   - Blurred for basic users

5. **NotificationsPage** (`/notifications`)
   - List of notifications
   - Mark as read
   - Filter by type

6. **PaymentsPage** (`/payments`)
   - Plan pricing display
   - Payment gateway integration
   - Upgrade button

7. **Static Pages**
   - TermsPage (`/terms`)
   - PrivacyPage (`/privacy`)
   - RefundPage (`/refund`)

### Modals/Popups

1. **LoginSignupModal**
   - Instagram OAuth button
   - Form fields
   - Toggle between login/signup

2. **PlansPricingModal**
   - Plan cards (Basic, Pro)
   - Features comparison
   - Pricing details

3. **ExchangeModal**
   - Exchange status
   - Time remaining
   - Rating component (if completed)
   - Accept/Repost button

## Configuration

### Environment Variables

```bash
# Backend
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/shoutx
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password
INSTAGRAM_APP_ID=your_instagram_app_id
INSTAGRAM_APP_SECRET=your_instagram_app_secret
JWT_SECRET=your_jwt_secret_key

# Frontend
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_INSTAGRAM_APP_ID=your_instagram_app_id
```

### CORS Configuration

The backend is configured with CORS allowed for:
- localhost:3000 (development)
- localhost:8080 (development)
- shoutx.app (production)

## Security

1. **JWT Authentication**
   - Token stored in localStorage
   - Sent in Authorization header
   - 1-hour expiration
   - Refresh token mechanism

2. **HTTPS**
   - Enforce in production
   - SSL certificate required

3. **Data Validation**
   - Input sanitization
   - Email verification
   - Rate limiting (10 requests/minute per IP)

## Error Handling

Standard API error response:
```json
{
  "error": "Error message",
  "code": "ERROR_CODE",
  "timestamp": "2025-12-19T18:00:00Z"
}
```

## Rate Limiting

- Basic users: 10 requests per day
- Pro users: 50 requests per day
- Rate limits reset at midnight UTC

## Database Models Used

### User
- id, name, username, email
- profilePicture, planType, followers
- accountType, isVerified, rating
- strikes, createdAt, updatedAt

### ShoutoutRequest
- id, senderId, receiverId, mediaId
- status, repostType, createdAt, completedAt

### ShoutoutExchange
- id, user1Id, user2Id, mediaId
- status, timeStatus, createdAt, completedAt

### Notification
- id, userId, type, message
- read, relatedId, createdAt

### Payment
- id, userId, planType, amount
- currency, status, transactionId, createdAt

## Testing

### API Testing
Use Postman or cURL to test endpoints:
```bash
curl -H "Authorization: Bearer token" http://localhost:8080/api/pages/home
```

### Frontend Testing
```bash
npm test  # Run React component tests
npm run build  # Production build
```

## Deployment

See DEPLOYMENT_GUIDE.md for production deployment instructions.

## Support

For issues or questions:
- Email: tushkinit@gmail.com
- GitHub: https://github.com/ro7toz/shout-app

---

**Last Updated**: 2025-12-19
**Version**: 1.0.0
