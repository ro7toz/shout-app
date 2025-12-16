# 🚀 Shout Application - Complete Setup Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Instagram OAuth2 Setup](#instagram-oauth2-setup)
3. [Docker Setup](#docker-setup)
4. [Application Startup](#application-startup)
5. [Verification](#verification)
6. [Testing](#testing)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

- **Java Development Kit (JDK) 17+**
  ```bash
  # Check version
  java -version
  ```
  - [Download JDK 17](https://www.oracle.com/java/technologies/downloads/)
  - Set `JAVA_HOME` environment variable

- **Maven 3.8+**
  ```bash
  # Check version
  mvn -version
  ```
  - [Download Maven](https://maven.apache.org/download.cgi)
  - Add Maven to PATH

- **Docker & Docker Compose**
  ```bash
  # Check Docker
  docker --version
  # Check Compose
  docker-compose --version
  ```
  - [Download Docker Desktop](https://www.docker.com/products/docker-desktop)

- **Git**
  ```bash
  git --version
  ```
  - [Download Git](https://git-scm.com/)

### System Requirements
- Minimum 4GB RAM
- 2GB free disk space
- Windows 10+, macOS 10.14+, or Linux (Ubuntu 18.04+)

---

## Instagram OAuth2 Setup

### Step 1: Create Meta Developer Account

1. Go to [Meta for Developers](https://developers.facebook.com)
2. Click "Get Started"
3. Create an account or sign in with Facebook
4. Accept the terms and complete verification

### Step 2: Create an App

1. Click "My Apps" → "Create App"
2. Choose platform: **Consumer**
3. Fill in the form:
   - **App Name**: `Shout Dev` (or your preferred name)
   - **App Purpose**: Select "Social Media App"
4. Click "Create App ID"
5. Complete the security check

### Step 3: Add Instagram Product

1. In your app dashboard, click "+ Add Product"
2. Find **Instagram Basic Display**
3. Click "Set Up"
4. Choose **For Consumer Apps** (or appropriate option)

### Step 4: Get Credentials

1. Go to **Settings → Basic**
2. Copy your **App ID** (also called Client ID)
3. Click **Show** next to **App Secret** and copy it
4. These are your:
   - `INSTAGRAM_CLIENT_ID`
   - `INSTAGRAM_CLIENT_SECRET`

### Step 5: Configure OAuth Redirect URI

1. Go to **Settings → Basic** (still)
2. Scroll down to "App Domains"
3. Add: `localhost` for development
4. Scroll to **Instagram Basic Display**
5. In **OAuth Redirect URIs**, add:
   ```
   http://localhost:8080/login/oauth2/code/instagram
   ```
6. Click **Save Changes**

### Step 6: Add Test User (Optional but Recommended)

1. Go to **Roles → Test Users**
2. Click **Add Instagram Test User**
3. Invite yourself and accept the invitation
4. Use this test account to test OAuth login

### Step 7: Verify Instagram App Mode

⚠️ **Important**: By default, apps are in **Development Mode**. For production:
- Go to **App Roles** → **App Mode**
- Switch to **Live** (requires app review)
- For testing, Development Mode is fine

---

## Docker Setup

### Start Docker Services

```bash
# Navigate to project root
cd shout-app

# Start all services (PostgreSQL + Redis)
docker-compose up -d
```

### Verify Docker Services

```bash
# Check all containers are running
docker ps

# Expected output:
CONTAINER ID   IMAGE              STATUS         PORTS
...            postgres:15-alpine Up 2 minutes  5432->5432/tcp
...            redis:7-alpine     Up 2 minutes  6379->6379/tcp
```

### Test Database Connection

```bash
# Connect to PostgreSQL
docker exec -it shout-db psql -U shoutuser -d shoutdb

# You should see: "psql (15.x)"
# Type '\q' to exit

# Test Redis
docker exec shout-redis redis-cli ping

# Expected: "PONG"
```

### View Logs

```bash
# PostgreSQL logs
docker logs shout-db

# Redis logs
docker logs shout-redis

# Follow logs in real-time
docker logs -f shout-db
```

---

## Application Startup

### Step 1: Set Environment Variables

#### On Linux/macOS:
```bash
export INSTAGRAM_CLIENT_ID="your_app_id_here"
export INSTAGRAM_CLIENT_SECRET="your_app_secret_here"
```

#### On Windows (PowerShell):
```powershell
$env:INSTAGRAM_CLIENT_ID="your_app_id_here"
$env:INSTAGRAM_CLIENT_SECRET="your_app_secret_here"
```

#### On Windows (Command Prompt):
```cmd
set INSTAGRAM_CLIENT_ID=your_app_id_here
set INSTAGRAM_CLIENT_SECRET=your_app_secret_here
```

#### Verify Environment Variables

**Linux/macOS:**
```bash
echo $INSTAGRAM_CLIENT_ID
echo $INSTAGRAM_CLIENT_SECRET
```

**Windows (PowerShell):**
```powershell
echo $env:INSTAGRAM_CLIENT_ID
echo $env:INSTAGRAM_CLIENT_SECRET
```

### Step 2: Build Application

```bash
# Navigate to project root
cd shout-app

# Clean and build
mvn clean package

# Or skip tests for faster build
mvn clean package -DskipTests
```

**Expected output (end of build):**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2m 15s
[INFO] Finished at: 2024-12-16T10:30:45+05:30
```

### Step 3: Run Application

#### Option A: Using Maven
```bash
mvn spring-boot:run
```

#### Option B: Run JAR Directly
```bash
java -jar target/shout-app-1.0.0.jar
```

#### Option C: Run with Custom Port
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Step 4: Verify Application Startup

You should see logs like:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

2024-12-16 10:35:00.123  INFO 12345 --- [  main] com.shout.ShoutApplication : Starting ShoutApplication
2024-12-16 10:35:02.456  INFO 12345 --- [  main] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Starting.
2024-12-16 10:35:03.789  INFO 12345 --- [  main] org.hibernate.dialect.Dialect : HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect
2024-12-16 10:35:05.012  INFO 12345 --- [  main] org.springframework.boot.web.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080 (http) with context path ''
2024-12-16 10:35:05.345  INFO 12345 --- [  main] com.shout.ShoutApplication : Started ShoutApplication in 5.234 seconds (JVM running for 5.678)
```

---

## Verification

### 1. Access Homepage

Open your browser and go to:
```
http://localhost:8080
```

You should see:
- **Shout** logo in top-left
- "Explore Creators" heading
- "Login with Instagram" button (if not logged in)
- Grid of creator cards (if logged in)

### 2. Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "redis": {
      "status": "UP"
    }
  }
}
```

### 3. Test OAuth Login

1. Click "Login with Instagram" button
2. You'll be redirected to Instagram login
3. Use your Instagram account (or test account) credentials
4. Grant permissions
5. You'll be redirected back to homepage
6. Your profile picture and username should appear in top-right

### 4. Test Core Features

**Create a Test Scenario:**

1. **Login User A**:
   - Login with first Instagram account
   - Note the homepage shows creator cards
   - Go to Dashboard
   - Note it shows your profile

2. **Open Incognito/Private Window**:
   - Login with second Instagram account
   - Verify you see different profile in Dashboard
   - Find User A in the creator feed

3. **Send Shoutout Request**:
   - Click "Request" button on User A's card
   - Enter a post link (e.g., any Instagram URL)
   - Click "Send Request"
   - Should see success message

4. **Accept Request (User A)**:
   - Switch back to first browser window
   - Go to Dashboard
   - Should see pending request from User B
   - Click "Accept"
   - Should see "24 hours remaining" timer

5. **Mark as Posted**:
   - User B: Click "I posted it" button
   - User A: Click "I posted it" button
   - Both should be added to Circle

6. **Submit Rating**:
   - If exchange fails, click "Rate" button
   - Select 1-5 stars
   - Add comment
   - Submit
   - Check profile to see rating updated

---

## Testing

### Run Unit Tests

```bash
mvn test
```

### Test Specific Class

```bash
mvn test -Dtest=ShoutoutServiceTest
```

### Test with Coverage

```bash
mvn clean test jacoco:report
# View report at: target/site/jacoco/index.html
```

### Manual API Testing

Using cURL or Postman:

**Get all users:**
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/users
```

**Send shoutout request:**
```bash
curl -X POST \
  http://localhost:8080/shoutout/request \
  -d "targetUsername=user123&postLink=https://instagram.com/p/ABC123"
```

---

## Troubleshooting

### Issue: "Cannot connect to database"

**Solution:**
```bash
# Check if container is running
docker ps | grep shout-db

# If not running, start it
docker-compose up -d shout-postgres

# Check logs
docker logs shout-db
```

### Issue: "Redis connection timeout"

**Solution:**
```bash
# Restart Redis
docker restart shout-redis

# Or restart all services
docker-compose restart
```

### Issue: "Port 8080 already in use"

**Solution:**
```bash
# Option 1: Find and kill process on port 8080
Linux/Mac: lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
Windows: netstat -ano | findstr :8080

# Option 2: Use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Issue: "OAuth redirect URI mismatch"

**Solution:**
1. Go to Meta Developer app settings
2. Verify redirect URI exactly matches:
   ```
   http://localhost:8080/login/oauth2/code/instagram
   ```
3. Clear browser cookies
4. Try login again

### Issue: "BUILD FAILURE"

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# Update dependencies
mvn dependency:resolve-plugins

# Check Java version
java -version  # Should be 17+
```

### Issue: Application starts but no data in database

**Solution:**
```bash
# Check Hibernate DDL
# Logs should show:
# "create table users..."
# "create table shoutout_requests..."

# If not created, check application.yml:
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Should be 'update' not 'validate'
```

### Issue: "Circuit breaker open" for Instagram API

**This is expected behavior**
- If Instagram API fails, Circuit Breaker prevents cascading failures
- Application falls back to cached data
- Check logs to see actual error:
  ```bash
  grep -i "circuitbreaker" logs/application.log
  ```
- Circuit automatically resets after 5 seconds of wait duration

---

## Performance Tips

### 1. Database Optimization
```bash
# Connect to PostgreSQL and create indexes
docker exec -it shout-db psql -U shoutuser -d shoutdb

CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_category ON users(category);
CREATE INDEX idx_request_status ON shoutout_requests(status);
```

### 2. Redis Optimization
```bash
# Connect to Redis
docker exec -it shout-redis redis-cli

# View cache stats
INFO stats

# Clear all cache if needed
FLUSHALL
```

### 3. Application Monitoring
```bash
# Access metrics
curl http://localhost:8080/actuator/metrics

# Circuit breaker status
curl http://localhost:8080/actuator/circuitbreakers

# Cache stats
curl http://localhost:8080/actuator/metrics/cache.gets
```

---

## Next Steps

1. ✅ Start the application
2. ✅ Login with Instagram
3. ✅ Explore features
4. ✅ Read [README.md](README.md) for detailed documentation
5. ✅ Check code comments in source files
6. ✅ Deploy to production

---

## Support

If you encounter issues:

1. Check [Troubleshooting](#troubleshooting) section above
2. Review application logs: `tail -f logs/spring.log`
3. Check Docker logs: `docker-compose logs -f`
4. Open a GitHub Issue: https://github.com/ro7toz/shout-app/issues
5. Check Spring Boot documentation: https://spring.io/projects/spring-boot

---

**Ready to shout? 📣 Happy coding!**