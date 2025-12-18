# 🚀 QUICKSTART - PHASE 6 DEPLOYMENT

## ⚡ 5-Minute Setup

### 1. Clone & Setup
```bash
git clone https://github.com/ro7toz/shout-app.git
cd shout-app
cp .env.example .env
# Edit .env with your credentials
```

### 2. Database Setup
```bash
# Create database
sudo -u postgres psql

CREATE USER shoutxuser WITH PASSWORD 'your_password';
CREATE DATABASE shoutxdb WITH OWNER shoutxuser;
GRANT ALL PRIVILEGES ON DATABASE shoutxdb TO shoutxuser;
\c shoutxdb
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
\q
```

### 3. Build & Run
```bash
# Load environment
export $(cat .env | xargs)

# Build
mvn clean package -DskipTests

# Run
java -jar target/shout-app-1.0.0.jar

# App will be available at: http://localhost:8080
```

---

## 🔍 Verify Installation

```bash
# Health check
curl http://localhost:8080/actuator/health | jq

# Check logs
tail -f app.log

# Database connection
psql -U shoutxuser -d shoutxdb -c "SELECT COUNT(*) FROM users;"

# Redis connection
redis-cli ping
```

---

## 📋 DEPLOYMENT COMMANDS

### Development
```bash
# Build
mvn clean package -DskipTests

# Run with debug logs
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Or direct Java
java -jar target/shout-app-1.0.0.jar --spring.profiles.active=dev
```

### Production
```bash
# Build for production
mvn clean package -Pproduction -DskipTests

# Run in background
nohup java -jar target/shout-app-1.0.0.jar --spring.profiles.active=prod > app.log 2>&1 &

# Check if running
ps aux | grep shout-app

# View logs
tail -f app.log

# Stop application
kill $(lsof -t -i:8080)
```

### Docker
```bash
# Build image
docker build -t shout-app:latest .

# Run container
docker run -d \
  -e DB_URL=jdbc:postgresql://db:5432/shoutxdb \
  -e REDIS_HOST=redis \
  -p 8080:8080 \
  --name shout-app \
  shout-app:latest

# Or with Docker Compose
docker-compose up -d

# Check logs
docker logs -f shout-app
```

---

## ✅ HEALTH CHECKS

```bash
# Overall health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/actuator/health/db

# Redis health
curl http://localhost:8080/actuator/health/redis

# Metrics
curl http://localhost:8080/actuator/metrics

# CPU usage
curl http://localhost:8080/actuator/metrics/process.cpu.usage

# Memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

---

## 🧪 TEST ENDPOINTS

### Setup (get a token first)
```bash
TOKEN="your_jwt_token_here"
```

### 1. Is User PRO?
```bash
curl -X GET http://localhost:8080/api/subscriptions/is-pro \
  -H "Authorization: Bearer $TOKEN"
# {"isPro": false}
```

### 2. Request STORY (Should work for all)
```bash
curl -X POST http://localhost:8080/api/shoutouts/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUsername": "influencer",
    "mediaType": "STORY",
    "postLink": "https://instagram.com/p/xyz"
  }'
# {"id": 123, "status": "PENDING"}
```

### 3. Request POST (BASIC user should fail)
```bash
curl -X POST http://localhost:8080/api/shoutouts/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUsername": "influencer",
    "mediaType": "POST",
    "postLink": "https://instagram.com/p/xyz"
  }'
# {"error": "Upgrade to PRO to request posts and reels"}
```

### 4. Discover Influencers
```bash
curl -X GET "http://localhost:8080/api/users/filter?followerRange=10000-50000&category=Fashion"
# [{"username": "fashion_guru", "followerCount": 25000}]
```

### 5. Analytics (Locked for BASIC)
```bash
curl -X GET http://localhost:8080/api/analytics/dashboard \
  -H "Authorization: Bearer $TOKEN"
# {"error": "Analytics locked. Upgrade to PRO."}
```

---

## 🔧 DATABASE MIGRATION

```bash
# Run migration manually
psql -U shoutxuser -d shoutxdb -f src/main/resources/db/migration/V1_6__compliance_fixes.sql

# Or on app startup (automatic with Flyway):
java -jar target/shout-app-1.0.0.jar

# Verify migration applied
psql -U shoutxuser -d shoutxdb -c "\d users" | grep strike
# Should show: strike_count | integer | NOT NULL DEFAULT 0
```

---

## 🐛 COMMON ISSUES

### Database Connection Error
```bash
# Check PostgreSQL running
sudo systemctl status postgresql

# Start if needed
sudo systemctl start postgresql

# Test connection
psql -U shoutxuser -d shoutxdb
```

### Redis Connection Error
```bash
# Check Redis running
sudo systemctl status redis-server

# Start if needed
sudo systemctl start redis-server

# Test connection
redis-cli ping  # Should return: PONG
```

### Port 8080 Already in Use
```bash
# Find what's using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use different port
java -Dserver.port=8081 -jar target/shout-app-1.0.0.jar
```

### Email Not Sending
```bash
# Check Gmail app password is correct
# (Not your regular Gmail password)
# Get it from: myaccount.google.com/apppasswords

# Test email connection
curl -X POST http://localhost:8080/api/email/test \
  -H "Content-Type: application/json" \
  -d '{"to": "test@example.com"}'
```

### Payment Gateway Error
```bash
# Verify Razorpay keys are set
echo $RAZORPAY_KEY_ID
echo $RAZORPAY_KEY_SECRET

# If empty, update .env and restart application
export $(cat .env | xargs)
java -jar target/shout-app-1.0.0.jar
```

---

## 📊 MONITORING

```bash
# Check application status
ps aux | grep shout-app

# View real-time logs
tail -f app.log

# Check memory usage
free -h

# Check disk space
df -h

# View database size
psql -U shoutxuser -d shoutxdb -c "SELECT pg_size_pretty(pg_database_size('shoutxdb'));"

# Check Redis memory
redis-cli INFO memory
```

---

## 🔐 SECURITY CHECKLIST

- [ ] Change all default passwords in .env
- [ ] Use HTTPS/SSL in production
- [ ] Configure CORS properly
- [ ] Enable rate limiting
- [ ] Keep dependencies updated
- [ ] Use environment variables for secrets
- [ ] Enable audit logging
- [ ] Configure firewall
- [ ] Set up monitoring
- [ ] Regular backups

---

## 📚 FULL DOCUMENTATION

For detailed setup: See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

For Phase 6 details: See [PHASE_6_DEPLOYMENT_SUMMARY.md](PHASE_6_DEPLOYMENT_SUMMARY.md)

For implementation: See [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md)

---

**Ready to deploy? Start with:** `./QUICKSTART.md` ✅
