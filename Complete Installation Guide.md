# 🔧 ShoutX - Complete Setup Guide

Comprehensive installation and configuration guide for local development and production deployment.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Instagram OAuth Setup](#instagram-oauth-setup)
3. [Database Setup](#database-setup)
4. [Application Configuration](#application-configuration)
5. [Docker Setup](#docker-setup)
6. [Running the Application](#running-the-application)
7. [Verification](#verification)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

**Java Development Kit (JDK) 17+**
```bash
# Check version
java -version

# Should output: openjdk version "17.x.x" or higher
```
Download: https://adoptium.net/

**Maven 3.8+**
```bash
# Check version
mvn -version

# Should output: Apache Maven 3.8.x or higher
```
Download: https://maven.apache.org/download.cgi

**Docker & Docker Compose**
```bash
# Check Docker
docker --version

# Check Compose
docker-compose --version
```
Download: https://docker.com/get-started

**Git**
```bash
git --version
```
Download: https://git-scm.com/

### System Requirements
- **RAM:** Minimum 4GB (8GB recommended)
- **Disk:** 2GB free space
- **OS:** Windows 10+, macOS 10.14+, Linux (Ubuntu 18.04+)

---

## Instagram OAuth Setup

### Step 1: Create Meta Developer Account

1. Go to **[Meta for Developers](https://developers.facebook.com)**
2. Click **"Get Started"**
3. Sign in with Facebook or create account
4. Accept Developer Terms
5. Complete email verification

### Step 2: Create Facebook App

1. Click **"My Apps"** → **"Create App"**
2. Select use case: **"Consumer"**
3. Fill in app details:
   - **App Name:** `Shout Dev` (or preferred name)
   - **App Contact Email:** Your email
   - **App Purpose:** Social Media/Networking
4. Click **"Create App ID"**
5. Complete security check (CAPTCHA)

### Step 3: Add Instagram Product

1. In your app dashboard, find **"Add Products to Your App"**
2. Locate **"Instagram Basic Display"**
3. Click **"Set Up"**
4. Accept Instagram terms

### Step 4: Get OAuth Credentials

1. Navigate to **Settings → Basic**
2. Copy your **App ID** (this is your Client ID)
3. Click **"Show"** next to **App Secret**
4. Copy the **App Secret**
5. Save these as:
   - `INSTAGRAM_CLIENT_ID`
   - `INSTAGRAM_CLIENT_SECRET`

### Step 5: Configure OAuth Redirect URI

1. Go to **Instagram Basic Display** settings
2. Scroll to **"Client OAuth Settings"**
3. In **"Valid OAuth Redirect URIs"**, add:

**For Local Development:**
```
http://localhost:8080/login/oauth2/code/instagram
```

**For Production:**
```
https://shoutx.co.in/login/oauth2/code/instagram
https://www.shoutx.co.in/login/oauth2/code/instagram
```

4. In **"Deauthorize Callback URL"**:
```
http://localhost:8080/auth/deauthorize
```

5. In **"Data Deletion Request URL"**:
```
http://localhost:8080/auth/data-deletion
```

6. Click **"Save Changes"**

### Step 6: Add Test Users (Recommended)

1. Go to **Roles → Test Users**
2. Click **"Add Instagram Test Users"**
3. Enter Instagram username
4. Invite the test user
5. Accept invitation on Instagram account

### Step 7: App Review (Production Only)

⚠️ **For Development:** App stays in Development Mode (limited to test users)

**For Production Launch:**
1. Go to **App Review**
2. Submit for review with:
   - App description
   - Privacy policy URL
   - Screenshots
   - Video demo
3. Wait 3-5 business days for approval

---

## Database Setup

### Option A: Docker PostgreSQL (Recommended)

```bash
# Start PostgreSQL with Docker
docker-compose up -d shout-db

# Verify running
docker ps | grep shout-db

# Should show: postgres:15-alpine
```

**Database Credentials (from docker-compose.yml):**
- Host: `localhost`
- Port: `5432`
- Database: `shoutxdb`
- Username: `shoutxuser`
- Password: `shoutpass`

### Option B: Local PostgreSQL Installation

**Install PostgreSQL 15:**

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql-15
```

**macOS (Homebrew):**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Windows:**
Download installer from https://www.postgresql.org/download/windows/

**Create Database:**
```bash
# Login as postgres user
sudo -u postgres psql

# Create user
CREATE USER shoutxuser WITH PASSWORD 'your_secure_password';

# Create database
CREATE DATABASE shoutxdb WITH OWNER shoutxuser;

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE shoutxdb TO shoutxuser;

# Enable UUID extension
\c shoutxdb
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

# Exit
\q
```

**Test Connection:**
```bash
psql -U shoutxuser -d shoutxdb -h localhost

# Should connect successfully
# Type \q to exit
```

---

## Application Configuration

### Step 1: Clone Repository

```bash
git clone https://github.com/ro7toz/shout-app.git
cd shout-app
```

### Step 2: Create Environment File

```bash
# Copy template
cp .env.example .env

# Edit with your credentials
nano .env   # or use your preferred editor
```

### Step 3: Configure Environment Variables

Edit `.env` with the following:

```bash
# ===== DATABASE =====
DB_URL=jdbc:postgresql://localhost:5432/shoutxdb
DB_USER=shoutxuser
DB_PASSWORD=your_secure_password_here

# ===== REDIS =====
REDIS_HOST=localhost
REDIS_PORT=6379

# ===== INSTAGRAM OAUTH =====
INSTAGRAM_CLIENT_ID=your_instagram_app_id
INSTAGRAM_CLIENT_SECRET=your_instagram_app_secret
INSTAGRAM_GRAPH_API_ACCESS_TOKEN=your_long_lived_token

# ===== FACEBOOK OAUTH =====
FACEBOOK_APP_ID=your_facebook_app_id
FACEBOOK_APP_SECRET=your_facebook_app_secret

# ===== JWT AUTHENTICATION =====
JWT_SECRET=your_random_32_character_secret_key_here
JWT_EXPIRATION=3600000  # 1 hour in milliseconds

# ===== EMAIL (GMAIL) =====
# Get app password from: https://myaccount.google.com/apppasswords
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password_16_chars
MAIL_FROM=noreply@shoutx.co.in

# ===== PAYMENT GATEWAYS =====
# Razorpay (Get from: https://dashboard.razorpay.com)
RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxx
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# PayPal (Get from: https://developer.paypal.com)
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret
PAYPAL_MODE=sandbox  # Change to 'live' for production

# Paytm
PAYTM_MERCHANT_ID=your_paytm_merchant_id
PAYTM_MERCHANT_KEY=your_paytm_merchant_key

# ===== AWS (Optional for production) =====
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
AWS_REGION=ap-south-1
AWS_S3_BUCKET=shoutx-media

# ===== APPLICATION =====
APP_URL=http://localhost:8080
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
```

### Step 4: Load Environment Variables

**Linux/macOS:**
```bash
export $(cat .env | xargs)
```

**Windows (PowerShell):**
```powershell
Get-Content .env | ForEach-Object {
  if ($_ -notmatch '^#' -and $_ -ne '') {
    $name, $value = $_ -split '=', 2
    Set-Item -Path env:$name -Value $value
  }
}
```

**Verify:**
```bash
echo $INSTAGRAM_CLIENT_ID  # Should show your App ID
```

---

## Docker Setup

### Start All Services

```bash
# Start PostgreSQL + Redis
docker-compose up -d

# Check status
docker-compose ps

# Expected output:
# NAME                STATUS              PORTS
# shout-db           Up 2 minutes        5432->5432/tcp
# shout-redis        Up 2 minutes        6379->6379/tcp
```

### Test Services

**PostgreSQL:**
```bash
# Connect to database
docker exec -it shout-db psql -U shoutxuser -d shoutxdb

# Run test query
SELECT version();

# Exit
\q
```

**Redis:**
```bash
# Test connection
docker exec shout-redis redis-cli ping

# Expected: PONG

# Check info
docker exec shout-redis redis-cli INFO server
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f shout-db
docker-compose logs -f shout-redis
```

### Stop Services

```bash
# Stop all
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## Running the Application

### Build Application

```bash
# Clean build
mvn clean package

# Skip tests for faster build
mvn clean package -DskipTests

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: 2.5 min
```

### Run Application

**Option 1: Using Maven**
```bash
mvn spring-boot:run
```

**Option 2: Using JAR**
```bash
java -jar target/shout-app-1.0.0.jar
```

**Option 3: With Custom Port**
```bash
java -Dserver.port=8081 -jar target/shout-app-1.0.0.jar
```

**Option 4: With Profile**
```bash
# Development
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Production
java -jar target/shout-app-1.0.0.jar --spring.profiles.active=prod
```

### Expected Startup Logs

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

:: Spring Boot ::                (v3.2.1)

2024-12-20 10:00:00 INFO  Starting ShoutApplication
2024-12-20 10:00:02 INFO  HikariPool-1 - Starting...
2024-12-20 10:00:03 INFO  HikariPool-1 - Start completed.
2024-12-20 10:00:05 INFO  Tomcat started on port(s): 8080 (http)
2024-12-20 10:00:05 INFO  Started ShoutApplication in 5.234 seconds

🌟 ShoutX Started Successfully!
📍 Running on: http://localhost:8080
🗄️  Database: shoutxdb (PostgreSQL)
🔴 Redis: Connected to localhost:6379
📧 Email: Configured with Gmail SMTP
💳 Razorpay: Initialized (TEST MODE)
🔒 Security: Enabled
```

---

## Verification

### 1. Health Check

```bash
curl http://localhost:8080/actuator/health | jq
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
        "version": "15.5"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.2.3"
      }
    },
    "mail": {
      "status": "UP"
    }
  }
}
```

### 2. Access Application

**Homepage:**
```
http://localhost:8080
```

**API Documentation:**
```
http://localhost:8080/swagger-ui.html
```

**Actuator Endpoints:**
```
http://localhost:8080/actuator
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/info
```

### 3. Test Instagram OAuth

1. Open http://localhost:8080
2. Click **"Login with Instagram"**
3. You'll be redirected to Instagram
4. Login with test account credentials
5. Grant permissions
6. Should redirect back to http://localhost:8080 with session

### 4. Test Database

```bash
# Connect to database
psql -U shoutxuser -d shoutxdb -h localhost

# Check tables created
\dt

# Should show:
# users, user_photos, shoutout_requests, etc.

# Check user count
SELECT COUNT(*) FROM users;

# Exit
\q
```

### 5. Test Redis

```bash
# Connect to Redis
docker exec -it shout-redis redis-cli

# Check keys
KEYS *

# Get session info
GET session:xxxxx

# Exit
exit
```

---

## Troubleshooting

### Issue: Port 8080 Already in Use

**Solution:**
```bash
# Find process using port
# Linux/Mac
lsof -i :8080 | grep LISTEN

# Windows
netstat -ano | findstr :8080

# Kill process
kill -9 <PID>

# Or use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Issue: Database Connection Failed

**Error:** `PSQLException: Connection refused`

**Solutions:**

1. **Check PostgreSQL is running:**
```bash
docker ps | grep shout-db

# If not running
docker-compose up -d shout-db
```

2. **Verify credentials:**
```bash
# Test connection
psql -U shoutxuser -d shoutxdb -h localhost

# If fails, check .env file credentials
```

3. **Check database exists:**
```bash
sudo -u postgres psql -c "\l" | grep shoutxdb
```

### Issue: Redis Connection Failed

**Error:** `JedisConnectionException`

**Solutions:**

1. **Restart Redis:**
```bash
docker-compose restart shout-redis
```

2. **Test connection:**
```bash
docker exec shout-redis redis-cli ping

# Should return: PONG
```

3. **Check Redis logs:**
```bash
docker logs shout-redis
```

### Issue: Instagram OAuth Error

**Error:** `redirect_uri_mismatch`

**Solutions:**

1. **Verify Redirect URI:**
   - Go to Meta Developer Console
   - Settings → Instagram Basic Display
   - Check redirect URI exactly matches:
     ```
     http://localhost:8080/login/oauth2/code/instagram
     ```

2. **Clear browser cookies:**
   - Open DevTools (F12)
   - Application → Cookies
   - Delete all cookies
   - Try again

3. **Check credentials:**
```bash
echo $INSTAGRAM_CLIENT_ID
echo $INSTAGRAM_CLIENT_SECRET

# Should not be empty
```

### Issue: Email Not Sending

**Error:** `SMTPAuthenticationException`

**Solutions:**

1. **Verify Gmail App Password:**
   - Go to https://myaccount.google.com/apppasswords
   - Generate new 16-character password
   - Update `MAIL_PASSWORD` in `.env`

2. **Check 2FA enabled:**
   - Gmail requires 2FA for app passwords
   - Enable at: https://myaccount.google.com/security

3. **Test email service:**
```bash
curl -X POST http://localhost:8080/api/email/test \
  -H "Content-Type: application/json" \
  -d '{"to":"test@example.com","subject":"Test"}'
```

### Issue: Build Failure

**Error:** `BUILD FAILURE`

**Solutions:**

1. **Clean and rebuild:**
```bash
mvn clean install

# If still fails
rm -rf ~/.m2/repository
mvn clean install
```

2. **Check Java version:**
```bash
java -version

# Should be 17 or higher
```

3. **Update dependencies:**
```bash
mvn dependency:resolve-plugins
mvn dependency:resolve
```

### Issue: Migration Failed

**Error:** `FlywayException: Unable to execute migration`

**Solutions:**

1. **Check migration history:**
```bash
psql -U shoutxuser -d shoutxdb -c "SELECT * FROM flyway_schema_history;"
```

2. **Reset migrations (⚠️ DELETES DATA):**
```bash
psql -U shoutxuser -d shoutxdb <<EOF
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO shoutxuser;
EOF

# Restart application to re-run migrations
```

3. **Fix specific migration:**
```bash
# Run migration manually
psql -U shoutxuser -d shoutxdb -f src/main/resources/db/migration/V1_6__compliance_fixes.sql
```

---

## Performance Optimization

### Database Tuning

```bash
# Connect to database
psql -U shoutxuser -d shoutxdb

# Create indexes for frequently queried columns
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_request_status ON shoutout_requests(status);
CREATE INDEX idx_request_sender ON shoutout_requests(sender_id);
CREATE INDEX idx_request_receiver ON shoutout_requests(receiver_id);

# Analyze tables
ANALYZE users;
ANALYZE shoutout_requests;
ANALYZE shoutout_exchanges;
```

### Redis Configuration

```bash
# Connect to Redis
docker exec -it shout-redis redis-cli

# Set max memory
CONFIG SET maxmemory 256mb
CONFIG SET maxmemory-policy allkeys-lru

# Save configuration
CONFIG REWRITE
```

### Application Settings

Edit `application.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 20000
  
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
          fetch_size: 50
```

---

## Next Steps

1. ✅ **Configure OAuth** - Follow Instagram OAuth setup
2. ✅ **Test locally** - Run application and verify all features
3. ✅ **Read API docs** - See [docs/backend/API_ENDPOINTS.md](docs/backend/API_ENDPOINTS.md)
4. ✅ **Explore components** - Check [docs/frontend/COMPONENTS.md](docs/frontend/COMPONENTS.md)
5. ✅ **Deploy to production** - Follow [docs/deployment/AWS_DEPLOYMENT.md](docs/deployment/AWS_DEPLOYMENT.md)

---

## Support

- **Email:** tushkinit@gmail.com
- **Issues:** https://github.com/ro7toz/shout-app/issues
- **Documentation:** See [README.md](README.md)

---

**Setup Time:** ⏱️ 30-45 minutes
**Difficulty:** 🟡 Intermediate
**Status:** ✅ Production Ready
