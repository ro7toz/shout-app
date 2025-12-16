# Shout - Modern Influencer Shoutout Exchange Platform

## 🎯 Overview

**Shout** is a production-ready Spring Boot 3 MVC application that enables influencers to discover, connect, and exchange shoutouts with each other. The platform features:

- 🔐 OAuth2 Instagram Authentication
- 📱 Responsive Design (Desktop & Mobile)
- ⚡ Redis Caching
- 🔄 Resilience4j Circuit Breaker Pattern
- 📬 Real-time Notifications
- ⭐ Rating System
- 👥 Circle Feature (Trusted Network)
- 📜 Lazy Loading with HTMX
- ⏱️ 24-Hour Deadline Tracking
- 🎨 Modern Thymeleaf + HTMX Frontend

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 17, Spring Boot 3.2.1, Spring Security 6 |
| **Database** | PostgreSQL 15 |
| **Cache** | Redis 7 |
| **Resilience** | Resilience4j (Circuit Breaker, Retry) |
| **Frontend** | Thymeleaf, HTMX, Tailwind CSS |
| **DevOps** | Docker, Docker Compose |
| **Build** | Maven 3.8+ |

---

## 📁 Project Structure

```
shout-app/
├── src/main/java/com/shout/
│   ├── ShoutApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── CacheConfig.java
│   ├── model/
│   │   ├── User.java
│   │   ├── ShoutoutRequest.java
│   │   ├── Rating.java
│   │   └── Notification.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ShoutoutRequestRepository.java
│   │   ├── RatingRepository.java
│   │   └── NotificationRepository.java
│   ├── service/
│   │   ├── ShoutoutService.java
│   │   ├── InstagramIntegrationService.java
│   │   └── UserSyncService.java
│   └── controller/
│       ├── HomeController.java
│       ├── ShoutoutController.java
│       ├── RatingController.java
│       └── DashboardController.java
├── src/main/resources/
│   ├── templates/
│   │   ├── home.html
│   │   ├── dashboard.html
│   │   ├── profile.html
│   │   ├── rating-form.html
│   │   └── fragments/cards.html
│   ├── application.yml
│   └── static/
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites

- **JDK 17+** - [Download](https://www.oracle.com/java/)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Instagram Developer Account** - [Meta for Developers](https://developers.facebook.com)

### 1️⃣ Clone Repository

```bash
git clone https://github.com/ro7toz/shout-app.git
cd shout-app
```

### 2️⃣ Setup Instagram OAuth2 Credentials

#### Get Your Credentials:
1. Go to [Meta for Developers](https://developers.facebook.com)
2. Create a new application
3. Add "Instagram Basic Display" product
4. Copy **App ID** and **App Secret** from Settings → Basic
5. Add Redirect URI: `http://localhost:8080/login/oauth2/code/instagram`

#### Set Environment Variables:

**Linux/Mac:**
```bash
export INSTAGRAM_CLIENT_ID="your_app_id"
export INSTAGRAM_CLIENT_SECRET="your_app_secret"
```

**Windows (PowerShell):**
```powershell
$env:INSTAGRAM_CLIENT_ID="your_app_id"
$env:INSTAGRAM_CLIENT_SECRET="your_app_secret"
```

### 3️⃣ Start Docker Services

```bash
docker-compose up -d
```

Verify services are running:
```bash
docker ps
```

### 4️⃣ Build Application

```bash
mvn clean package
```

### 5️⃣ Run Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/shout-app-1.0.0.jar
```

### 6️⃣ Access Application

- **Homepage**: [http://localhost:8080](http://localhost:8080)
- **Dashboard**: [http://localhost:8080/dashboard](http://localhost:8080/dashboard)
- **Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 📊 Application Workflow

### Authentication Flow
```
User → Login with Instagram → OAuth2 Redirect → User Sync → Dashboard Access
```

### Shoutout Exchange Flow
```
1. User A discovers User B
   ↓
2. User A sends shoutout request with post link
   ↓
3. User B receives notification (24h countdown starts)
   ↓
   ├─ Accept → Both have 24 hours to post
   └─ Decline → Request expires
   ↓
4a. BOTH post → Added to Circle ✅
4b. ONE doesn't post → Can be rated (1-5⭐) ❌
```

### Expiration Check (Every Hour)
```
Scheduler → Find 24h+ old ACCEPTED requests
    ↓
    ├─ Both posted? → Mark COMPLETED
    └─ Missing post? → Mark FAILED → Allow bad rating
```

---

## 🔑 Key Features

### 1. **User Authentication**
- OAuth2 integration with Instagram
- Automatic user profile sync
- Secure token management

### 2. **Discovery & Search**
- Paginated user feed (9 items/page)
- HTMX lazy loading (infinite scroll)
- Filter by category
- Full-text search

### 3. **Shoutout Requests**
- Send/receive requests with post links
- 24-hour deadline automatic tracking
- Real-time notifications
- Request status tracking (PENDING → ACCEPTED → COMPLETED)

### 4. **Rating System**
- 1-5 star ratings
- Reason tracking (why not posted, etc.)
- Automatic average calculation
- Public rating display on profiles

### 5. **Circle Feature**
- Build trusted network automatically
- View circle members
- Direct connection after successful exchange

### 6. **Resilience**
- **Circuit Breaker**: Instagram API failures
- **Retry Logic**: Automatic retry with backoff
- **Fallback**: Cached data or placeholder
- **Caching**: User profiles cached for 1 hour

---

## 📡 API Endpoints

### Public
| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Homepage |
| GET | `/users/page/{page}` | Lazy load users |
| GET | `/category/{category}` | Filter by category |
| GET | `/search?query=...` | Search users |

### Protected
| Method | Path | Description |
|--------|------|-------------|
| GET | `/dashboard` | User dashboard |
| GET | `/dashboard/profile/{username}` | View profile |
| POST | `/shoutout/request` | Send request |
| POST | `/shoutout/accept/{id}` | Accept request |
| POST | `/shoutout/posted/{id}` | Mark as posted |
| POST | `/shoutout/cancel/{id}` | Cancel request |
| POST | `/rating/submit` | Submit rating |
| GET | `/rating/form/{username}` | Rating form |

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    full_name VARCHAR(255),
    profile_pic_url TEXT,
    category VARCHAR(100),
    follower_count INTEGER,
    average_rating DECIMAL(3,2) DEFAULT 5.0,
    total_ratings INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Shoutout Requests Table
```sql
CREATE TABLE shoutout_requests (
    id SERIAL PRIMARY KEY,
    requester_id VARCHAR(255) REFERENCES users(username),
    target_id VARCHAR(255) REFERENCES users(username),
    post_link TEXT,
    status VARCHAR(50),
    accepted_at TIMESTAMP,
    requester_posted BOOLEAN DEFAULT false,
    target_posted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Ratings Table
```sql
CREATE TABLE ratings (
    id SERIAL PRIMARY KEY,
    rater_id VARCHAR(255) REFERENCES users(username),
    rated_user_id VARCHAR(255) REFERENCES users(username),
    score INTEGER CHECK (score BETWEEN 1 AND 5),
    reason VARCHAR(100),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## ⚙️ Configuration

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shoutdb
    username: shoutuser
    password: password123
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1 hour

resilience4j:
  circuitbreaker:
    instances:
      instagram:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 5s
```

---

## 🐛 Troubleshooting

### PostgreSQL Connection Error
```bash
# Check if container is running
docker ps | grep postgres

# Restart database
docker restart shout-db

# View logs
docker logs shout-db
```

### Redis Connection Error
```bash
# Restart Redis
docker restart shout-redis

# Check connection
docker exec shout-redis redis-cli ping
```

### OAuth2 Redirect Error
- Verify Meta app has correct redirect URI: `http://localhost:8080/login/oauth2/code/instagram`
- Check environment variables are set correctly
- Clear browser cookies and try again

### Port Already in Use
```bash
# Change port in application.yml
server:
  port: 8081  # Use different port
```

---

## 🚢 Production Deployment

### Build Docker Image
```dockerfile
FROM openjdk:17-slim
COPY target/shout-app-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Environment Variables (Production)
```bash
DATABASE_URL=jdbc:postgresql://prod-db:5432/shoutdb
REDIS_URL=redis://prod-redis:6379
INSTAGRAM_CLIENT_ID=xxx
INSTAGRAM_CLIENT_SECRET=xxx
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

### Security Checklist
- [ ] HTTPS enabled (SSL/TLS)
- [ ] Database password rotated
- [ ] Environment variables in secret manager
- [ ] Rate limiting configured
- [ ] CORS properly restricted
- [ ] Logging and monitoring enabled
- [ ] Backups configured

---

## 📈 Performance Optimization

### Database Indexes
```sql
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_category ON users(category);
CREATE INDEX idx_rating ON users(average_rating DESC);
```

### Redis Caching
- User profiles: 1 hour TTL
- Category list: 24 hours TTL
- Search results: 30 minutes TTL

### Query Optimization
- Pagination: 9 items per page
- Lazy loading: HTMX infinite scroll
- Index on frequently queried columns

---

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Manual Testing Checklist
- [ ] Login with Instagram
- [ ] View user feed
- [ ] Send shoutout request
- [ ] Accept/decline request
- [ ] Mark post as done
- [ ] Submit rating
- [ ] View dashboard
- [ ] Check notifications
- [ ] View circle members
- [ ] Search users

---

## 📚 Documentation

- [Spring Boot 3 Guide](https://spring.io/projects/spring-boot)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Redis Docs](https://redis.io/docs/)
- [Resilience4j Docs](https://resilience4j.readme.io/)
- [Thymeleaf Guide](https://www.thymeleaf.org/)
- [HTMX Docs](https://htmx.org/)

---

## 🤝 Contributing

Contributions welcome! Please:

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

---

## 📝 License

MIT License - see LICENSE file for details.

---

## 🎉 Roadmap

- [ ] Payment integration (Stripe)
- [ ] Direct messaging
- [ ] Analytics dashboard
- [ ] Mobile app (Flutter)
- [ ] Multi-language support
- [ ] Advanced recommendations
- [ ] Video shoutouts
- [ ] Subscription tiers
- [ ] AI-powered matching
- [ ] Marketplace for services

---

## 📞 Support

For issues or questions:
- Open a GitHub Issue
- Email: support@shoutapp.com
- Discord: [Join Server](https://discord.gg/shoutapp)

---

**Happy Shouting! 📣**