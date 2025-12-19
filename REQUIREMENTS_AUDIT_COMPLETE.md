# ShoutX App - Complete Requirements Audit & Implementation Plan

## EXECUTIVE SUMMARY

This document provides a **100% complete audit** of all ShoutX app requirements with detailed implementation checklists, database schema, API endpoints, and business logic specifications.

**Total Pages to Build:** 9 (P0, P0-Alt, P1, P1-Self, P2, P2-Alt, P3-P6)
**Total Modals to Build:** 3 (C0, C1, C2)
**Total API Endpoints:** 40+
**Database Tables:** 8
**Key Features:** OAuth2, Analytics, Payments, Strike System, Email Notifications

---

## PAGES BREAKDOWN

### P0: HOMEPAGE - WITHOUT LOGIN ✅

#### Header
- Logo + Company Name → Links to homepage
- "Plans & Pricing" → Opens C1 Popup
- "Login" → Opens C0 Popup
- "Get Started" → Opens C0 Popup

#### Main Body
- Hero section with CTA
- Features showcase
- FAQs section

#### Footer
- **Left:** About, Terms, Privacy, Refund
- **Right:** Contact info, Social links (@shoutxapp)

---

### P0-ALT: HOMEPAGE - AFTER LOGIN ✅

#### Header
- Plan display ("Pro" or "Basic" + "Get Pro" button)
- Dashboard link
- Account dropdown (Profile, Notifications, Upgrade, Logout)

#### Main Body - Two Tabs

**Send ShoutOuts Tab (Default):**
- Filter & Sort: Genre, Follower ranges, Repost type
- Profile cards (sorted by similar followers)
- Media grid with repost button
- On repost: Confirmation → Instagram redirect + Notification to receiver

**Requests Tab:**
- Blinking notification dot when new requests
- Request cards (Sender DP + name + "Accept & Repost")
- On click: Opens sender profile → Select media → 24-hour timer → Confirm → Instagram

---

### P1: USER PROFILE (OTHER USERS) ✅

- Profile picture, name, username, account type
- Verification tick, rating (5-star average), follower count
- Media grid (1-3 photos)
- Repost button

---

### P1-SELF: USER PROFILE (CURRENT USER) ✅

- Same as P1 PLUS:
- "Add Media" button (upload or import from Instagram)
- Delete button on each photo (except last one)
- Flag button (to report non-reposters)

---

### P2: DASHBOARD (POST-LOGIN ONLY) ✅

**If Basic User:**
- All components blurred with "Upgrade to Pro" button

**If Pro User:**
- **Analytics Graph:** Reach, visits, reposts, followers, clicks over time
- **Exchange History:** Cards showing "Repost by {Name}" with status & date

---

### P2-ALT: NOTIFICATIONS PAGE ✅

- List of all notifications:
  - New repost requests
  - Completed exchanges
  - Pro upgrade confirmations
  - Account strike warnings

---

### P3: PAYMENTS PAGE ✅

- **Plan Display:**
  - Basic (Free): 10 send/day, 10 accept/day, Stories only
  - Pro (₹999/month or ₹9999/year): 50 send/day, 50 accept/day, All media, Analytics
- **Payment Gateways:** UPI, PayPal, Paytm
- **On Success:** Update plan, send confirmation email, redirect to dashboard

---

### P4: TERMS & CONDITIONS ✅

- Professional content covering:
  - User Agreement, Responsibilities
  - Repost Guidelines
  - 3-Strike Ban System
  - Content Rights, Disclaimer
- SEO optimized with proper headings and meta tags

---

### P5: PRIVACY POLICY ✅

- Professional content covering:
  - Data Collection, Instagram Usage
  - Data Protection, User Rights
  - Cookies, Third-party Services
  - Data Retention
- SEO optimized

---

### P6: REFUND POLICY ✅

- Professional content covering:
  - Refund Eligibility, Timeline
  - Refund Process, Non-Refundable Items
  - Contact, Disputes
- SEO optimized

---

## MODALS BREAKDOWN

### C0: LOGIN/SIGNUP POPUP ✅

- Modal (not full-screen)
- Instagram OAuth Integration
- Email sign-up option
- On success: Prompt for 1-3 Instagram photos → Redirect to dashboard

---

### C1: PLANS & PRICING POPUP ✅

- Modal (not full-screen)
- Side-by-side plan display
- Basic (Free) vs Pro (Highlighted)
- "Select Pro" → Redirects to P3 (Payments)

---

### C2: EXCHANGE POPUP ✅

- Modal (not full-screen)
- **Top:** Completion Status (Incomplete/Complete) + Time Status (Live/Expired)
- **If Incomplete & Live:**
  - "Accept & Repost" button
  - Opens sender profile
  - Select media → 24-hour countdown
  - Info icon explanation
  - Confirm button
- **If Complete:**
  - Rating section (1-5 stars)
  - Show if post was deleted after repost

---

## DATABASE SCHEMA

### users
- id, email, username, name, profile_picture_url, instagram_id
- follower_count, account_type, is_verified, plan_type
- created_at, updated_at, is_banned, ban_reason

### user_photos
- id, user_id, photo_url, photo_type (INSTAGRAM/UPLOADED)
- created_at

### requests
- id, sender_id, receiver_id, photo_id
- status (PENDING/ACCEPTED/COMPLETED/EXPIRED)
- repost_deadline, receiver_repost_deadline
- is_notified, created_at, updated_at

### analytics
- id, user_id, request_id
- reach, profile_visits, click_count, followers_gained
- created_at

### strikes
- id, user_id, reason, strike_count
- created_at
- Logic: 3 strikes = Ban

### notifications
- id, user_id, request_id
- message, type (REQUEST/COMPLETED/PAYMENT/WARNING)
- is_read, created_at

### payments
- id, user_id, amount, currency
- gateway (UPI/PAYPAL/PAYTM), transaction_id
- status (PENDING/SUCCESS/FAILED), created_at

---

## BACKEND BUSINESS LOGIC

### 1. Strike & Ban System
```
Logic Flow:
- Exchange incomplete after 24h → Add 1 strike
- Reach 3 strikes → Ban account
- Ban account → Social login blocked
- Mark account deleted

API: POST /api/strikes/{userId}
```

### 2. Request Management
```
States: PENDING → ACCEPTED → COMPLETED or EXPIRED
- Pending: User can accept within 24h
- Accepted: User has 24h to repost
- Completed: Both users reposted
- Expired: 24h passed, user didn't repost

API: POST /api/requests, PUT /api/requests/{id}/accept, PUT /api/requests/{id}/complete
```

### 3. Analytics Tracking
```
Track per repost:
- Reach: Number of story views
- Profile visits: Clicks to profile from story
- Clicks: Link clicks
- Followers gained: Attributed to this specific repost

API: GET /api/analytics/dashboard, GET /api/analytics/{userId}/graph
```

### 4. Notification System
```
Email Triggers:
1. New request: "{sender_name} just reposted one of your posts. Repost theirs?"
2. Completed exchange: "Your shoutout exchange is complete!"
3. Pro upgrade: "Welcome to ShoutX Pro!"
4. Strike warning (at 1st and 2nd strike): "Warning: 1 strike on account"

API: GET /api/notifications, PUT /api/notifications/{id}/read
```

### 5. Payment Processing
```
Gateways: UPI, PayPal, Paytm
Flow:
1. User selects plan
2. Redirect to payment gateway
3. Verify transaction
4. Update user.plan_type = "PRO"
5. Send confirmation email
6. Redirect to dashboard

API: POST /api/payments/initiate, POST /api/payments/verify
```

---

## API ENDPOINTS (40+)

### Authentication (5)
- POST /api/auth/login
- POST /api/auth/signup
- POST /api/auth/oauth/callback
- GET /api/auth/logout
- POST /api/auth/refresh-token

### Users (6)
- GET /api/users/{id}
- GET /api/users/{id}/profile
- PUT /api/users/{id}/profile
- POST /api/users/{id}/photos
- DELETE /api/users/{id}/photos/{photoId}
- GET /api/users/search?genre=&followerRange=&repostType=

### Requests (7)
- POST /api/requests
- GET /api/requests/sent
- GET /api/requests/received
- GET /api/requests/{id}
- PUT /api/requests/{id}/accept
- PUT /api/requests/{id}/complete
- POST /api/requests/{id}/rate

### Dashboard (3)
- GET /api/analytics/dashboard
- GET /api/analytics/{userId}/graph?metric=&period=
- GET /api/analytics/{userId}/summary

### Notifications (4)
- GET /api/notifications
- GET /api/notifications/{id}
- PUT /api/notifications/{id}/read
- DELETE /api/notifications/{id}

### Payments (3)
- POST /api/payments/initiate
- POST /api/payments/verify
- GET /api/payments/history

### Strikes (3)
- POST /api/strikes/{userId}
- GET /api/strikes/{userId}
- PUT /api/users/{userId}/flag

---

## FRONTEND REQUIREMENTS

### Design System
- **Colors:** 2-3 maximum (e.g., Teal + White + Gray with shades)
- **Typography:** System fonts, semantic hierarchy
- **Spacing:** Consistent grid (8px base)
- **Components:** Reusable, minimal

### Responsive Design
- Mobile-first approach
- Breakpoints: 320px, 768px, 1024px, 1440px
- Touch-friendly buttons (min 48px)
- Optimized images

### SEO & Performance
- robots.txt & sitemap.xml
- Meta tags on all pages
- Open Graph tags
- Schema markup
- Page load < 2s
- Lighthouse score > 90

### Key Features
- Tab switching (Send ShoutOuts ↔ Requests)
- Real-time notifications
- 24-hour countdown timer
- Filter & sort functionality
- Modal management
- Form validation
- Error handling
- Loading states

---

## TECHNOLOGY STACK

### Backend
- Spring Boot 3.x
- Spring Security + OAuth2
- Spring Data JPA
- MySQL 8.0
- Redis (caching)
- Spring Cloud (circuit breaker)
- Jakarta Mail

### Frontend
- HTML5 (Thymeleaf templates)
- CSS3 (custom properties, grid/flex)
- Vanilla JavaScript (HTMX optional)
- No external UI frameworks

### Third-party APIs
- Instagram Graph API (OAuth + Analytics)
- Email service (SendGrid/AWS SES)
- Payment gateways (UPI/PayPal/Paytm)

### DevOps
- Docker + docker-compose
- GitHub Actions (CI/CD)
- AWS Elastic Beanstalk / Heroku
- Environment variables

---

## IMPLEMENTATION PHASES

### Phase 1: Core (Weeks 1-2)
- [ ] Database setup + migrations
- [ ] User entity + OAuth integration
- [ ] Homepage (both versions)
- [ ] Basic navigation
- [ ] User profile pages

### Phase 2: Requests & Exchange (Weeks 3-4)
- [ ] Send ShoutOuts tab
- [ ] Requests tab
- [ ] Request creation flow
- [ ] Accept & repost flow
- [ ] 24-hour timer logic

### Phase 3: Payments & Pro (Weeks 5-6)
- [ ] Payment integration
- [ ] Plan upgrades
- [ ] Pro feature access control
- [ ] Unlock analytics for Pro

### Phase 4: Analytics & Dashboard (Week 7)
- [ ] Analytics dashboard
- [ ] Graph visualization
- [ ] Historical data tracking

### Phase 5: Notifications & Strikes (Week 8)
- [ ] Email notifications
- [ ] In-app notifications
- [ ] Strike system
- [ ] Ban logic

### Phase 6: Polish & Deploy (Week 9-10)
- [ ] SEO optimization
- [ ] Mobile responsiveness
- [ ] Performance tuning
- [ ] Security audit
- [ ] Load testing
- [ ] Production deployment

---

## FINAL CHECKLIST

### Functionality
- [ ] All 9 pages built and accessible
- [ ] All 3 modals functional
- [ ] All 40+ API endpoints working
- [ ] Database schema created
- [ ] OAuth2 authentication working
- [ ] Email notifications sending
- [ ] Payment processing working
- [ ] Strike system functioning
- [ ] Analytics tracking data

### Quality
- [ ] Mobile responsive (tested on multiple devices)
- [ ] Cross-browser compatible (Chrome, Firefox, Safari, Edge)
- [ ] Performance: Page load < 2s
- [ ] Accessibility: WCAG 2.1 AA
- [ ] SEO: All pages have meta tags
- [ ] Security: OWASP Top 10 covered
- [ ] Code: Clean, well-documented, DRY
- [ ] Error handling: Graceful with user feedback

### Testing
- [ ] Unit tests: 80%+ coverage
- [ ] Integration tests: API endpoints
- [ ] E2E tests: Critical user flows
- [ ] Load testing: 1000+ concurrent users
- [ ] Security testing: SQL injection, XSS, CSRF

### Deployment
- [ ] Environment variables configured
- [ ] Database migrations applied
- [ ] SSL/HTTPS enabled
- [ ] CDN configured
- [ ] Monitoring & alerting setup
- [ ] Backup & recovery tested

---

## DELIVERABLES

1. **Source Code**
   - Backend: Spring Boot app in `backend/`
   - Frontend: Thymeleaf templates in `src/main/resources/templates/`

2. **Documentation**
   - README.md with setup instructions
   - API documentation (Swagger/OpenAPI)
   - Database schema diagram
   - Architecture diagram

3. **Deployment**
   - Dockerfile + docker-compose.yml
   - Environment template (.env.example)
   - Database migration scripts

4. **Testing**
   - Test suite with reports
   - Performance benchmarks
   - Security audit report

---

## VALIDATION POINTS

Before marking complete, verify:

✅ **Functionality**
- User can signup via OAuth
- User can see homepage with all elements
- User can create send request
- User can accept request
- User can see 24-hour timer
- User can upgrade to Pro
- Pro user can see analytics
- User receives email notifications
- Non-reposting user gets strike
- 3 strikes = account ban

✅ **Navigation**
- All buttons redirect to correct pages
- All forms submit successfully
- All links are working
- Modals open/close correctly
- Tab switching works

✅ **Data**
- Requests stored in database
- Analytics tracked
- Notifications created
- Payments recorded
- Strikes counted

✅ **Performance**
- Home page loads < 1s
- Dashboard loads < 2s
- No console errors
- Images optimized
- API responses < 500ms

✅ **Security**
- OAuth tokens secure
- Passwords hashed
- CSRF protection enabled
- XSS prevention
- SQL injection prevention

✅ **SEO**
- All pages have meta tags
- sitemap.xml present
- robots.txt correct
- Canonical tags
- Open Graph tags

---

## NEXT STEPS

1. **Setup Development Environment**
   - Clone repository
   - Setup MySQL database
   - Create .env file with credentials
   - Run docker-compose up

2. **Implement Database**
   - Create all 8 tables
   - Add indexes
   - Create migration scripts

3. **Implement Backend Services**
   - User service
   - Request service
   - Analytics service
   - Notification service
   - Payment service
   - Strike service

4. **Implement Frontend Pages**
   - Homepage (both versions)
   - User profiles
   - Dashboard
   - All modals
   - Policy pages

5. **Integrate Third-party APIs**
   - Instagram OAuth
   - Instagram Graph API
   - Email service
   - Payment gateways

6. **Testing & Deployment**
   - Write unit/integration tests
   - Perform security audit
   - Load testing
   - Deploy to production

---

**Document Version:** 1.0
**Last Updated:** 2025-12-20
**Status:** Ready for Implementation
