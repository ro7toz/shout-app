# 🚀 PHASE 6 DEPLOYMENT GUIDE

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Database Setup](#database-setup)
4. [Local Development](#local-development)
5. [Production Deployment](#production-deployment)
6. [Health Checks](#health-checks)
7. [Testing Endpoints](#testing-endpoints)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

**Required Software:**
- Java 17+ (JDK)
- Maven 3.8+
- PostgreSQL 13+
- Redis 6+
- Git

**Optional:**
- Docker & Docker Compose (for easy setup)
- Postman (for API testing)
- pgAdmin (for database management)

**External Accounts Needed:**
- Gmail account (with 2FA enabled for app passwords)
- Razorpay account (sandbox for testing)
- PayPal account (sandbox for testing)
- Instagram OAuth app credentials
- Facebook OAuth app credentials

---

## Environment Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/ro7toz/shout-app.git
cd shout-app
git pull origin main  # Ensure latest Phase 6 code
```

### Step 2: Create .env File

```bash
cp .env.example .env
```

### Step 3: Configure Environment Variables

Edit `.env` and fill in all values:

```bash
# ===== DATABASE =====
DB_URL=jdbc:postgresql://localhost:5432/shoutxdb
DB_USER=shoutxuser
DB_PASSWORD=your_secure_password_here

# ===== REDIS =====
REDIS_HOST=localhost
REDIS_PORT=6379

# ===== EMAIL (GMAIL) =====
# Steps to get app password:
# 1. Go to myaccount.google.com/apppasswords
# 2. Select "Mail" and "Windows Computer"
# 3. Generate and copy the 16-character password
MAIL_USERNAME=tushkinit@gmail.com
MAIL_PASSWORD=your_gmail_app_password
MAIL_FROM=noreply@shoutx.co.in

# ===== INSTAGRAM OAUTH =====
# Get from: https://developers.facebook.com/apps/
INSTAGRAM_CLIENT_ID=your_instagram_client_id
INSTAGRAM_CLIENT_SECRET=your_instagram_client_secret
INSTAGRAM_GRAPH_API_ACCESS_TOKEN=your_long_lived_access_token

# ===== FACEBOOK OAUTH =====
# Get from: https://developers.facebook.com/apps/
FACEBOOK_APP_ID=your_facebook_app_id
FACEBOOK_APP_SECRET=your_facebook_app_secret

# ===== RAZORPAY (TEST KEYS) =====
# Get from: https://dashboard.razorpay.com/signin
RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxx
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# ===== PAYPAL (SANDBOX) =====
# Get from: https://www.sandbox.paypal.com/
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret
PAYPAL_MODE=sandbox  # Change to 'live' for production

# ===== APPLICATION =====
APP_URL=http://localhost:8080
SERVER_PORT=8080
LOG_LEVEL=DEBUG
```

### Step 4: Load Environment Variables

**On Linux/Mac:**
```bash
export $(cat .env | xargs)
```

**On Windows (PowerShell):**
```powershell
Get-Content .env | foreach {
    if ($_ -notmatch '^#' -and $_ -ne '') {
        $name, $value = $_ -split '=', 2
        Set-Item -Path env:$name -Value $value
    }
}
```

---

## Database Setup

### Step 1: Create PostgreSQL Database and User

```bash
# Connect as postgres user
sudo -u postgres psql

# Create user
CREATE USER shoutxuser WITH PASSWORD 'your_secure_password_here';

# Create database
CREATE DATABASE shoutxdb WITH OWNER shoutxuser;

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE shoutxdb TO shoutxuser;

# Connect to database and enable extensions
\c shoutxdb
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

# Exit
\q
```

### Step 2: Verify Database Connection

```bash
psql -U shoutxuser -d shoutxdb -h localhost

# Should connect successfully, then exit
\q
```

### Step 3: Database Migrations

**Option A: Automatic (Recommended)**
- Flyway will run migrations automatically on startup
- Just build and run the application

**Option B: Manual**
```bash
# Run migration file directly
psql -U shoutxuser -d shoutxdb -f src/main/resources/db/migration/V1_6__compliance_fixes.sql

# Verify migration
psql -U shoutxuser -d shoutxdb
\d users
-- Should show new columns: strike_count, account_banned, social_login_banned, banned_at

\q
```

---

## Local Development

### Step 1: Build the Application

```bash
mvn clean package -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
[INFO] Finished at: YYYY-MM-DDTHH:MM:SS±HH:MM
```

### Step 2: Run in Development Mode

```bash
# Option 1: Using Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Option 2: Direct Java command
java -jar target/shout-app-1.0.0.jar --spring.profiles.active=dev
```

**Expected Output:**
```
🌟 Shout App Started Successfully
📍 Running on: http://localhost:8080
🗄️  Database: shoutxdb (PostgreSQL)
🔴 Redis: Connected to localhost:6379
📧 Email: Configured with Gmail SMTP
💳 Razorpay: Initialized (TEST MODE)
🔒 Security: Enabled
```

### Step 3: Access the Application

```bash
# Homepage
http://localhost:8080

# API Documentation
http://localhost:8080/swagger-ui.html

# Health Check
http://localhost:8080/actuator/health
```

---

## Production Deployment

### Step 1: Production Build

```bash
# Build with production profile
mvn clean package -Pproduction -DskipTests
```

### Step 2: Production Environment Setup

```bash
# Create production .env
cp .env.example .env.prod

# Edit with production values (NOT test credentials)
nano .env.prod

# Important: Change these for production
# PAYPAL_MODE=live (not sandbox)
# RAZORPAY_KEY_ID=rzp_live_xxxxx (not rzp_test)
# LOG_LEVEL=INFO (not DEBUG)
# APP_URL=https://shoutx.co.in (actual domain)
```

### Step 3: Deploy to Server

**Option A: Direct JAR Deployment**
```bash
# Copy JAR to server
scp target/shout-app-1.0.0.jar user@server:/app/

# SSH into server
ssh user@server

# Navigate to app directory
cd /app

# Load environment variables
export $(cat .env.prod | xargs)

# Start application (with nohup to run in background)
nohup java -jar shout-app-1.0.0.jar --spring.profiles.active=prod > app.log 2>&1 &

# Verify it's running
ps aux | grep shout-app
```

**Option B: Docker Deployment**
```bash
# Build Docker image
docker build -t shout-app:latest .

# Run with Docker
docker run -d \
  -e DB_URL=jdbc:postgresql://db:5432/shoutxdb \
  -e REDIS_HOST=redis \
  -p 8080:8080 \
  --name shout-app \
  shout-app:latest
```

**Option C: Docker Compose (Recommended)**
```bash
# Create docker-compose.yml (see project root)
docker-compose up -d

# Verify services are running
docker-compose ps
```

### Step 4: Configure Reverse Proxy (Nginx)

```nginx
server {
    listen 443 ssl http2;
    server_name shoutx.co.in www.shoutx.co.in;

    ssl_certificate /etc/ssl/certs/your_cert.crt;
    ssl_certificate_key /etc/ssl/private/your_key.key;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}

server {
    listen 80;
    server_name shoutx.co.in www.shoutx.co.in;
    return 301 https://$server_name$request_uri;
}
```

Reload Nginx:
```bash
nginx -s reload
```

---

## Health Checks

### Check Application Health

```bash
curl http://localhost:8080/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "version": "13.0"
      }
    },
    "redis": {
      "status": "UP"
    },
    "mail": {
      "status": "UP"
    }
  }
}
```

### Check Database Connection

```bash
psql -U shoutxuser -d shoutxdb -h localhost -c "SELECT COUNT(*) FROM users;"
```

### Check Redis Connection

```bash
redis-cli ping
# Response: PONG
```

### Check Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

---

## Testing Endpoints

### 1. Test PRO Check (Subscription)

```bash
# Get authentication token first (replace with real token)
TOKEN="your_jwt_token_here"

# Test if user is PRO
curl -X GET http://localhost:8080/api/subscriptions/is-pro \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# Response (if PRO):
# {"isPro": true}

# Response (if BASIC):
# {"isPro": false}
```

### 2. Test Media Type Validation

```bash
# BASIC user tries to request POST (should fail)
curl -X POST http://localhost:8080/api/shoutouts/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUsername": "influencer_handle",
    "mediaType": "POST",
    "postLink": "https://instagram.com/p/xyz"
  }'

# Response (400 Bad Request):
# {"error": "Upgrade to PRO to request posts and reels"}
```

### 3. Test Story Request (Should work for all)

```bash
# Any user can request STORY
curl -X POST http://localhost:8080/api/shoutouts/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUsername": "influencer_handle",
    "mediaType": "STORY",
    "postLink": "https://instagram.com/p/xyz"
  }'

# Response (201 Created):
# {
#   "id": 123,
#   "status": "PENDING",
#   "mediaType": "STORY"
# }
```

### 4. Test Discovery Filtering

```bash
# Filter influencers by follower range
curl -X GET "http://localhost:8080/api/users/filter?followerRange=10000-50000&category=Fashion" \
  -H "Content-Type: application/json"

# Response:
# [
#   {
#     "username": "fashion_influencer",
#     "followerCount": 25000,
#     "category": "Fashion"
#   }
# ]
```

### 5. Test Analytics (Locked for BASIC)

```bash
# BASIC user tries to access analytics (should fail)
curl -X GET http://localhost:8080/api/analytics/dashboard \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# Response (403 Forbidden):
# {"error": "Analytics locked. Upgrade to PRO to unlock."}
```

---

## Troubleshooting

### Issue: Database Connection Failed

```
ERROR: org.postgresql.util.PSQLException: Connection refused
```

**Solution:**
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Start if not running
sudo systemctl start postgresql

# Verify connection
psql -U shoutxuser -d shoutxdb -h localhost
```

### Issue: Redis Connection Failed

```
ERROR: redis.clients.jedis.exceptions.JedisConnectionException
```

**Solution:**
```bash
# Check Redis is running
sudo systemctl status redis-server

# Start if not running
sudo systemctl start redis-server

# Verify connection
redis-cli ping
```

### Issue: Email Not Sending

```
ERROR: com.sun.mail.smtp.SMTPAuthenticationException
```

**Solution:**
```bash
# Verify Gmail app password:
# 1. Check 2FA is enabled on Gmail
# 2. Generate new app password at myaccount.google.com/apppasswords
# 3. Update MAIL_PASSWORD in .env
# 4. Restart application

# Test email endpoint
curl -X POST http://localhost:8080/api/email/test \
  -H "Content-Type: application/json" \
  -d '{"to": "test@example.com", "subject": "Test"}'
```

### Issue: Payment Gateway Not Initialized

```
ERROR: Razorpay Key ID not configured
```

**Solution:**
```bash
# Verify Razorpay credentials in .env
echo $RAZORPAY_KEY_ID
echo $RAZORPAY_KEY_SECRET

# If empty, update .env and restart:
nohup java -jar target/shout-app-1.0.0.jar > app.log 2>&1 &

# Check logs
tail -f app.log
```

### Issue: High Memory Usage

**Solution:**
```bash
# Increase JVM heap size
java -Xmx2g -Xms1g -jar target/shout-app-1.0.0.jar

# Monitor memory
jps -l  # List Java processes
jstat -gc <process_id>  # Check garbage collection
```

### Issue: Migration Failed

```
ERROR: org.flywaydb.core.internal.command.DbMigrate
```

**Solution:**
```bash
# Check which migrations were applied
psql -U shoutxuser -d shoutxdb -c "SELECT * FROM flyway_schema_history;"

# If stuck, check the SQL file for syntax errors
psql -U shoutxuser -d shoutxdb -f src/main/resources/db/migration/V1_6__compliance_fixes.sql

# If migration file can't be recreated, reset and restart:
# (WARNING: This deletes all data)
psql -U shoutxuser -d shoutxdb -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Then restart application to re-run all migrations
```

---

## Performance Optimization

### Enable Query Caching

```yaml
# In application.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
          fetch_size: 50
        format_sql: true
        use_sql_comments: true
```

### Monitor Performance

```bash
# View metrics
curl http://localhost:8080/actuator/metrics/process.cpu.usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### Database Connection Pooling

```yaml
# In application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
```

---

## Security Checklist

- [ ] Change default passwords
- [ ] Enable HTTPS/SSL certificates
- [ ] Configure CORS origins properly
- [ ] Enable rate limiting
- [ ] Rotate JWT signing keys
- [ ] Use environment variables for secrets (NOT in code)
- [ ] Enable audit logging
- [ ] Configure firewall rules
- [ ] Enable database encryption
- [ ] Set up monitoring and alerting

---

**Deployment Guide Complete** ✅

For more help, check the project README or contact the development team.
