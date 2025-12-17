# ShoutX Backend Implementation Guide

## 🎯 Project Overview

ShoutX is a modern influencer shoutout exchange platform where users can request and exchange Instagram stories, posts, and reels with other creators. The platform supports two subscription tiers:

- **BASIC** (Free): Stories only, no analytics
- **PRO** (₹499/month or ₹4999/year): Posts, Reels, Advanced Analytics

---

## ✅ Backend Implementation Status

### Phase 1: Data Models ✅ COMPLETE
- [PR #11](https://github.com/ro7toz/shout-app/pull/11) - 8 entities, 7 repositories

### Phase 2: Services ✅ COMPLETE
- [PR #12](https://github.com/ro7toz/shout-app/pull/12) - 5 services, 4 DTOs

### Phase 3: REST Controllers ✅ COMPLETE
- [PR #13](https://github.com/ro7toz/shout-app/pull/13) - 5 controllers, 15 API endpoints

---

## 🏗️ Architecture Overview

### Database Schema

```
┌─────────────────────────────────────┐
│           Users                     │
│  (with subscription_status, strikes)│
└────────────┬────────────────────────┘
             │
      ┌──────┴──────┐
      │             │
      ▼             ▼
  ┌────────┐   ┌──────────┐
  │Subscr  │   │Payments  │
  │-ption  │   └──────────┘
  └────────┘
      │
      ├─→ ┌──────────────────┐
      │   │SubscriptionPlan  │
      │   │ (BASIC/PRO)      │
      │   └──────────────────┘
      │
      ├─→ ┌─────────────────────┐
      │   │ ShoutoutExchange    │
      │   │ (24-hr timer)       │
      │   └──────┬──────────────┘
      │          │
      │          ├─→ PostAnalytics
      │          │
      │          └─→ UserRating
      │
      └─→ ComplianceRecord
          (3-strike system)
```

### Service Layer Architecture

```
┌──────────────────────────────────────────┐
│         REST Controllers (15)             │
├──────────────────────────────────────────┤
│  Services (5)                            │
│  ├─ SubscriptionService                  │
│  ├─ PaymentService                       │
│  ├─ ShoutoutExchangeService              │
│  ├─ ComplianceService                    │
│  └─ RatingService                        │
├──────────────────────────────────────────┤
│  Repositories (7) ← JPA                  │
│  └─ Database (PostgreSQL)                │
└──────────────────────────────────────────┘
```

---

## 📊 Data Models

### 1. User (Enhanced)
```java
- username (PK)
- instagramId
- email
- subscriptionStatus (BASIC/PRO)
- strikeCount (0-3)
- accountBanned
- socialLoginBanned
- averageRating
- totalRatings
```

### 2. SubscriptionPlan
```java
- id (PK)
- planType (BASIC/PRO)
- monthlyPrice
- yearlyPrice
- storiesSupported, postsSupported, reelsSupported
- analyticsSupported, advancedAnalyticsSupported
```

### 3. Subscription
```java
- id (PK)
- user (FK)
- plan (FK)
- billingCycle (MONTHLY/YEARLY)
- startDate, renewalDate
- status (ACTIVE/CANCELLED/EXPIRED)
- autoRenew
```

### 4. Payment
```java
- id (PK)
- user (FK)
- subscription (FK)
- transactionId (unique)
- gateway (RAZORPAY/PAYPAL/UPI/PAYTM)
- amount, currency
- status (PENDING/COMPLETED/FAILED/REFUNDED)
- gatewayResponse (JSON)
```

### 5. ShoutoutExchange
```java
- id (PK)
- requester (FK), acceptor (FK)
- shoutoutRequest (FK)
- mediaType (STORY/POST/REEL)
- expiresAt (NOW + 24 hours)
- status (PENDING/COMPLETED/FAILED/EXPIRED)
- requesterPosted, acceptorPosted
- requesterPostUrl, acceptorPostUrl
```

### 6. PostAnalytics
```java
- id (PK)
- user (FK)
- shoutoutExchange (FK)
- mediaType
- impressions, clicks, profileVisits
- shares, saves, comments, likes
- engagementRate
- analyticsVerified (from Instagram API)
```

### 7. UserRating
```java
- id (PK)
- rater (FK), ratee (FK)
- exchange (FK)
- rating (1-5)
- review
- category (RELIABILITY/CONTENT_QUALITY/PROFESSIONALISM)
```

### 8. ComplianceRecord
```java
- id (PK)
- user (FK)
- exchange (FK)
- violationType (FAILED_TO_POST/REMOVED_POST)
- strikeNumber (1/2/3)
- accountBanned
- socialLoginBanned
```

---

## 🔌 REST API Endpoints

### Subscription Management
```
GET    /api/subscriptions/current        - Get user's subscription
GET    /api/subscriptions/is-pro         - Check PRO status
POST   /api/subscriptions/upgrade/monthly  - Upgrade to PRO (monthly)
POST   /api/subscriptions/upgrade/yearly   - Upgrade to PRO (yearly)
DELETE /api/subscriptions/cancel         - Cancel subscription
```

### Payment Processing
```
POST   /api/payments/initiate            - Start payment
POST   /api/payments/webhook/confirm     - Gateway webhook (for Razorpay, PayPal, UPI, Paytm)
GET    /api/payments/{transactionId}     - Check payment status
```

### Analytics (PRO Only)
```
GET    /api/analytics/dashboard          - Full dashboard
GET    /api/analytics/dashboard/monthly/{month} - Monthly filter
GET    /api/analytics/dashboard/by-type/{type}  - By media type
```

### Shoutout Exchanges
```
GET    /api/shoutouts/pending            - Get pending exchanges
POST   /api/shoutouts/{id}/mark-posted   - Mark as posted
GET    /api/shoutouts/{id}               - Get exchange details
```

### User Ratings
```
POST   /api/ratings/rate                 - Rate user
GET    /api/ratings/user/{username}      - Get user ratings
GET    /api/ratings/user/{username}/category/{cat} - Category ratings
```

---

## 🔄 Business Logic Flows

### Exchange Flow (24-Hour Timer)

```
1. User A requests shoutout from User B
   → ShoutoutRequest created (status: PENDING)

2. User B accepts request
   → ShoutoutRequest status: ACCEPTED
   → ShoutoutExchange created (expiresAt = NOW + 24hrs)
   → Status: PENDING

3. User A posts on their Instagram
   → POST /api/shoutouts/{id}/mark-posted
   → requesterPosted = true
   → requesterPostedAt = NOW

4. User B posts on their Instagram
   → POST /api/shoutouts/{id}/mark-posted
   → acceptorPosted = true
   → acceptorPostedAt = NOW
   → Status: COMPLETED ✅

5. Both can now rate each other
   → POST /api/ratings/rate
   → UserRating created

IF timer expires before both post:
   → Scheduled job marks EXPIRED
   → ComplianceRecord created (strikeNumber++)
   → User.strikeCount incremented
   → If strikeCount >= 3: accountBanned = true
```

### Compliance & Strike System

```
Violation Detected
  ↓
1st Strike: User warned (strikeCount = 1)
  ↓
2nd Strike: Restrictions applied (strikeCount = 2)
  ↓
3rd Strike: Account BANNED (strikeCount = 3)
  ├─ User.accountBanned = true
  ├─ User.socialLoginBanned = true
  ├─ Social login blocked from new signups
  └─ Account deleted (optional)
```

### Subscription Upgrade Flow

```
1. User on BASIC plan
2. User clicks "Upgrade to PRO"
3. Choose billing: Monthly (₹499) or Yearly (₹4999)
4. POST /api/payments/initiate
   → Payment created (status: PENDING)
   → Return payment URL (Razorpay/PayPal form)
5. User pays via gateway
6. Gateway sends webhook to /api/payments/webhook/confirm
   → Payment status: COMPLETED
   → Subscription status: ACTIVE
   → User.subscriptionStatus = PRO
7. Access to analytics unlocked ✅
```

---

## 🔐 Authorization & Security

### User Authentication
- OAuth2 via Instagram
- Interceptor adds @RequestAttribute("user") to all requests
- User object available in all controllers

### Authorization Levels
- **Public**: Signup, login
- **Authenticated**: View profiles, request shoutouts
- **PRO Only**: Analytics dashboard
- **Admin**: Compliance review (future)

### Data Access Rules
- Users can only access their own subscriptions
- Users can only rate exchanges they participated in
- Analytics only accessible to PRO users
- Social login ban prevents new account creation

---

## 📈 Subscription Plans

### BASIC (Free)
- ✅ Request shoutouts
- ✅ Accept shoutouts
- ✅ Stories only (24-hour content)
- ❌ No Posts/Reels
- ❌ No analytics
- ❌ No advanced features

### PRO (₹499/month or ₹4999/year)
- ✅ Everything in BASIC
- ✅ Posts support (permanent)
- ✅ Reels support
- ✅ **Advanced Analytics**
  - Total impressions
  - Clicks on shoutout
  - Profile visits attributed
  - Engagement rate
  - Monthly filtering
  - Media type filtering
- ✅ Post removal detection
- ✅ Premium support

---

## 🎯 Key Features Implemented

### ✅ Subscription Management
- Plan definitions (BASIC/PRO)
- Upgrade flow
- Renewal handling
- Cancellation
- Auto-renew option

### ✅ Payment Integration
- Multi-gateway ready (Razorpay, PayPal, UPI, Paytm)
- Webhook handling
- Payment status tracking
- Refund support

### ✅ Exchange Lifecycle
- 24-hour timer (automated)
- Expiration detection
- Status tracking
- Completion detection

### ✅ Compliance System
- Violation tracking
- 3-strike system
- Automatic bans
- Social login ban
- Post removal detection

### ✅ Rating System
- Post-exchange ratings
- Category-based ratings
- Average rating calculation
- Reputation tracking

### ✅ Analytics (PRO Only)
- Instagram metrics storage
- Monthly filtering
- Media type filtering
- Aggregate calculations

---

## 📋 Configuration

### Contact Information
- **Email**: tushkinit@gmail.com
- **Phone**: +91 9509103148
- **Address**: Poonam Colony, Kota (Rajasthan)
- **Domain**: shoutx.co.in

### Social Media
- Instagram: @shoutxapp
- LinkedIn: @shoutxapp
- Facebook: @shoutxapp

---

## 🚀 Next Steps

### Phase 4: Email Notifications
- EmailService implementation
- SMTP configuration
- Email templates (invitation, acceptance, completion)
- Notification delivery

### Phase 5: Instagram Integration
- InstagramAnalyticsService
- Real-time analytics fetching
- API rate limiting
- Analytics caching with Redis

### Phase 6: Frontend
- Thymeleaf templates
- Minimalist UI (2-3 colors)
- Mobile responsive
- HTMX for interactivity

### Phase 7: DevOps & SEO
- robots.txt
- sitemap.xml
- Meta tags
- Structured data
- Domain configuration

---

## 📚 Technology Stack

**Backend**:
- Java 17
- Spring Boot 3.2.1
- Spring Security + OAuth2
- Spring Data JPA
- PostgreSQL
- Redis
- Resilience4j

**Frontend** (Planned):
- Thymeleaf
- HTMX
- Tailwind CSS

**DevOps**:
- Docker
- AWS Elastic Beanstalk

---

## 📝 Notes for Frontend Team

1. **Authentication**: User available via `@RequestAttribute("user")` in Thymeleaf
2. **PRO Gating**: Check user.subscriptionStatus before showing analytics
3. **Analytics**: Blurred for BASIC users (CSS overlay)
4. **Payment**: Integrate with gateway SDKs (Razorpay, PayPal, etc.)
5. **Real-time**: Use HTMX for 24-hour timer countdown
6. **Notifications**: Email notifications sent automatically

---

## ✨ Quality Assurance

- [x] Database models with proper relationships
- [x] Service layer with business logic
- [x] REST controllers with proper status codes
- [x] Authorization and security
- [x] Error handling
- [x] Scheduled jobs for automation
- [x] Transaction management
- [ ] Unit tests
- [ ] Integration tests
- [ ] API documentation (Swagger)

---

**Backend Implementation**: ✅ Ready for Frontend Integration

For questions or clarifications, contact: tushkinit@gmail.com
