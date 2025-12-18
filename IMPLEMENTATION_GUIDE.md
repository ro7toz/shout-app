# ShoutX Phase 6: Compliance & Subscription System Implementation Guide

## 🎯 Overview

This phase implements:
1. **Subscription Management** - BASIC (free) vs PRO (₹499/month)
2. **Media Type Access Control** - STORY (all), POST (PRO only), REEL (PRO only)
3. **Compliance & Strikes** - 3-strike system leading to account ban
4. **Payment Gateway Integration** - Razorpay for INR payments
5. **Advanced Analytics** - Locked for BASIC users
6. **Homepage Filtering** - Follower count and category ranges

---

## 📋 Pre-Setup Checklist

- [ ] Java 17+ installed
- [ ] PostgreSQL 13+ running
- [ ] Redis server running
- [ ] Maven 3.8+
- [ ] Git configured
- [ ] IDE with Lombok support enabled

---

## 🚀 Step 1: Environment Setup

### 1.1 Create `.env` file

```bash
cp .env.example .env
```

### 1.2 Configure Database

```bash
# Create database
sudo -u postgres createdb shoutxdb
sudo -u postgres createuser shoutxuser
sudo -u postgres psql

# In psql console:
alter user shoutxuser with encrypted password 'password123';
grant all privileges on database shoutxdb to shoutxuser;
```

### 1.3 Update `.env` with your values

```env
DB_URL=jdbc:postgresql://localhost:5432/shoutxdb
DB_USER=shoutxuser
DB_PASSWORD=password123
REDIS_HOST=localhost
REDIS_PORT=6379
```

---

## 📧 Step 2: Gmail SMTP Configuration

### 2.1 Enable 2-Factor Authentication

1. Go to https://myaccount.google.com/security
2. Enable "2-Step Verification"
3. Go to https://myaccount.google.com/apppasswords
4. Select "Mail" and "Windows Computer"
5. Copy the 16-character password

### 2.2 Update `.env`

```env
MAIL_USERNAME=tushkinit@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx  # 16-char password from step 5
MAIL_FROM=noreply@shoutx.co.in
```

### 2.3 Test Email

```bash
# Run app and check logs
mvn spring-boot:run
# Look for "Mail configuration verified"
```

---

## 💳 Step 3: Razorpay Integration

### 3.1 Get Razorpay Credentials

1. Go to https://dashboard.razorpay.com/signin
2. Create account (free for testing)
3. Go to Settings → API Keys
4. Copy **Key ID** (rzp_test_xxxxx)
5. Copy **Key Secret**

### 3.2 Update `.env`

```env
RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxx
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

### 3.3 Add Razorpay to `pom.xml`

```xml
<dependency>
    <groupId>com.razorpay</groupId>
    <artifactId>razorpay-java</artifactId>
    <version>1.4.3</version>
</dependency>
```

### 3.4 Test Payment Flow

```bash
curl -X POST http://localhost:8080/api/payments/initiate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"amount":499, "billingCycle":"MONTHLY"}'
```

Expected response:
```json
{
  "transactionId": "order_xxxxx",
  "status": "PENDING",
  "amount": 499
}
```

---

## 🔐 Step 4: OAuth2 Configuration

### 4.1 Instagram OAuth Setup

1. Go to https://developers.facebook.com/apps/
2. Create new app → Business
3. Add "Instagram" product
4. Configure:
   - **Valid OAuth Redirect URIs**: `http://localhost:8080/login/oauth2/code/instagram`
   - **Deauthorize Callback URL**: `http://localhost:8080/oauth/deauthorize`

### 4.2 Facebook OAuth Setup

1. Go to https://developers.facebook.com/apps/
2. Add "Facebook Login" product
3. Configure:
   - **Valid OAuth Redirect URIs**: `http://localhost:8080/login/oauth2/code/facebook`
   - **Deauthorize Callback URL**: `http://localhost:8080/oauth/deauthorize`

### 4.3 Update `.env`

```env
INSTAGRAM_CLIENT_ID=your_instagram_client_id
INSTAGRAM_CLIENT_SECRET=your_instagram_client_secret
INSTAGRAM_GRAPH_API_ACCESS_TOKEN=your_long_lived_token

FACEBOOK_APP_ID=your_facebook_app_id
FACEBOOK_APP_SECRET=your_facebook_app_secret
```

---

## 🗄️ Step 5: Database Migration

### 5.1 Run Flyway Migration

```bash
# The app will auto-run migrations on startup
mvn spring-boot:run

# Check logs for:
# ✅ Migration V1.6 completed successfully
```

### 5.2 Verify Database Changes

```sql
-- Connect to database
psql -U shoutxuser -d shoutxdb

-- Check new columns
\d shoutout_requests
-- Should see: media_type | character varying

\d users
-- Should see: strike_count, account_banned, social_login_banned, banned_at

-- Check indexes
\di
-- Should see: idx_user_follower_count, idx_request_media_type, etc.
```

---

## 🎯 Step 6: Service Initialization

### 6.1 Initialize Subscription Plans

```bash
# Create an endpoint to initialize
POST /api/admin/subscriptions/initialize
```

Or add to startup:

```java
@Component
@RequiredArgsConstructor
public class AppInitializer implements ApplicationRunner {
    private final SubscriptionService subscriptionService;
    
    @Override
    public void run(ApplicationArguments args) {
        subscriptionService.initializeSubscriptionPlans();
    }
}
```

### 6.2 Verify Plans Created

```sql
SELECT * FROM subscription_plans;
-- Should show: BASIC and PRO plans
```

---

## 🧪 Step 7: Testing

### 7.1 Test BASIC User (Story Only)

```bash
# Create request as BASIC user
curl -X POST http://localhost:8080/api/shoutouts/request \
  -H "Authorization: Bearer <basic_user_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUsername": "creator1",
    "postLink": "https://instagram.com/...",
    "mediaType": "STORY"
  }'

# Expected: 200 OK ✅
```

### 7.2 Test BASIC User POST Request (Should Fail)

```bash
curl -X POST http://localhost:8080/api/shoutouts/request \
  -H "Authorization: Bearer <basic_user_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUsername": "creator1",
    "postLink": "https://instagram.com/...",
    "mediaType": "POST"
  }'

# Expected: 400 Bad Request - "Upgrade to PRO to request posts and reels" ❌
```

### 7.3 Test PRO User (All Media Types)

```bash
# First upgrade user to PRO
POST /api/subscriptions/upgrade

# Then try all media types
for MEDIA_TYPE in STORY POST REEL; do
  curl -X POST http://localhost:8080/api/shoutouts/request \
    -H "Authorization: Bearer <pro_user_token>" \
    -H "Content-Type: application/json" \
    -d "{
      \"targetUsername\": \"creator1\",
      \"postLink\": \"https://instagram.com/...\",
      \"mediaType\": \"$MEDIA_TYPE\"
    }"
done

# Expected: All return 200 OK ✅
```

### 7.4 Test Analytics Lock (BASIC User)

```bash
curl -X GET http://localhost:8080/api/analytics/dashboard \
  -H "Authorization: Bearer <basic_user_token>"

# Expected response:
{
  "locked": true,
  "message": "Upgrade to PRO to unlock advanced analytics",
  "upgradeUrl": "/api/subscriptions/upgrade"
}
```

### 7.5 Test Homepage Filtering

```bash
# Filter by follower count
curl -X GET "http://localhost:8080/api/users/filter?followerRange=10000-50000&page=0&size=9"

# Filter by category
curl -X GET "http://localhost:8080/api/users/filter?category=Fashion&page=0&size=9"

# Combined
curl -X GET "http://localhost:8080/api/users/filter?category=Beauty&followerRange=5000-20000&page=0&size=9"
```

---

## 📊 Step 8: Monitoring

### 8.1 Check Application Health

```bash
curl http://localhost:8080/actuator/health

# Expected:
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "redis": {"status": "UP"},
    "mail": {"status": "UP"}
  }
}
```

### 8.2 Monitor Logs

```bash
# Watch real-time logs
tail -f logs/shoutx.log | grep -E "(✅|❌|⚠️|💳|🔄)"
```

### 8.3 Check Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

---

## 🚢 Step 9: Deployment

### 9.1 Production Checklist

- [ ] Update `.env` with production credentials
- [ ] Enable HTTPS (SSL certificates)
- [ ] Set `ddl-auto: validate` in `application.yml`
- [ ] Configure email with production domain
- [ ] Switch Razorpay to production keys
- [ ] Set up monitoring and alerts
- [ ] Backup database before deployment

### 9.2 Build JAR

```bash
mvn clean package -DskipTests
```

### 9.3 Run on Server

```bash
java -jar target/shout-app-1.0.0.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

### 9.4 Docker Deployment (Optional)

```dockerfile
FROM eclipse-temurin:17-jdk
COPY target/shout-app-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
```

```bash
docker build -t shoutx:latest .
docker run -p 8080:8080 --env-file .env shoutx:latest
```

---

## 📚 Key Classes

| Class | Purpose |
|-------|----------|
| `SubscriptionService` | Manage subscriptions, PRO checks |
| `ShoutoutService` | Handle shoutout requests with media validation |
| `UserRepository` | Filtering and compliance queries |
| `AnalyticsController` | Lock analytics for BASIC users |
| `PaymentService` | Razorpay integration |
| `ComplianceService` | Strike system and bans (next phase) |

---

## 🐛 Troubleshooting

### Email Not Sending

```
❌ JavaMailSender connection timeout
✅ Solution: Check MAIL_PASSWORD is correct 16-char app password
```

### Payment Gateway Error

```
❌ "Invalid Razorpay credentials"
✅ Solution: Use TEST keys (rzp_test_) for development
```

### Database Migration Failed

```
❌ "Migration V1.6 failed"
✅ Solution: Run migration manually and check PostgreSQL version
```

### OAuth Token Expired

```
❌ "Invalid Instagram token"
✅ Solution: Get new long-lived token (valid 60 days)
```

---

## ✅ Completion Checklist

- [ ] All 5 commits merged to `backend/phase-6-compliance-fixes`
- [ ] Database migration successful
- [ ] Razorpay sandbox payments working
- [ ] Email notifications sending
- [ ] PRO validation on media types working
- [ ] Analytics locked for BASIC users
- [ ] Homepage filtering working
- [ ] All tests passing
- [ ] PR created for code review

---

## 📞 Support

For issues:
1. Check logs: `tail -f logs/shoutx.log`
2. Check environment variables: `env | grep RAZORPAY`
3. Test database connection: `psql -U shoutxuser -d shoutxdb -c "SELECT 1"`
4. Verify Redis: `redis-cli ping` (should return PONG)

---

**Phase 6 Implementation Complete!** 🎉

Next: Phase 7 - Compliance Monitoring & 24-Hour Timer
