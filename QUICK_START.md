# 🚀 Shout App - Quick Start Guide

## Prerequisites

- Java 17+
- Docker & Docker Compose
- Git
- Instagram Developer App (for OAuth)

## Setup (5 Minutes)

### Step 1: Get Instagram Credentials

1. Go to https://developers.facebook.com
2. Create an app → Select "Consumer" type
3. Add "Instagram Basic Display"
4. Get your App ID and App Secret
5. Add Redirect URI: `http://localhost:8080/login/oauth2/code/instagram`

### Step 2: Clone & Setup

```bash
git clone https://github.com/ro7toz/shout-app.git
cd shout-app

# Set environment variables
export INSTAGRAM_CLIENT_ID="your_app_id"
export INSTAGRAM_CLIENT_SECRET="your_app_secret"
```

### Step 3: Start Database & Cache

```bash
docker-compose up -d

# Verify
docker ps
```

### Step 4: Run Application

```bash
# Option A: Maven
mvn spring-boot:run

# Option B: Build JAR
mvn clean package
java -jar target/shout-app-1.0.0.jar
```

### Step 5: Access Application

- Frontend: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics

## Features Available

✅ User Discovery - Browse creators by category
✅ OAuth Login - Instagram authentication
✅ Shoutout Exchange - Send/accept requests
✅ Notifications - Real-time notification system
✅ Rating System - 1-5 star user ratings
✅ User Circle - Network building
✅ Responsive UI - Works on all devices

## Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

## Production Deployment

```bash
# Using Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# Or Kubernetes
kubectl apply -f k8s/
```

## Troubleshooting

**Port 8080 in use?**
```bash
lsof -i :8080
kill -9 <PID>
```

**Database connection error?**
```bash
docker restart shout-db
```

**Redis connection error?**
```bash
docker restart shout-redis
```

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | No | Homepage |
| GET | `/users/page/{page}` | No | Get users paginated |
| GET | `/category/{category}` | No | Filter by category |
| GET | `/dashboard` | Yes | User dashboard |
| POST | `/api/shoutout/request` | Yes | Create request |
| POST | `/api/shoutout/{id}/accept` | Yes | Accept request |
| POST | `/api/rating/submit` | Yes | Submit rating |
| GET | `/notifications` | Yes | Get notifications |

## Support

For issues or questions, open an issue on GitHub or contact: tushkinit@gmail.com

Happy coding! 🎉
