# 🚀 Shout App - Quick Start (5 Minutes)

## ⏱ Ultra-Fast Setup

### 1️⃣ Prerequisites Check (1 min)

```bash
# Verify all required tools
java -version          # Need Java 17+
mvn -version          # Need Maven 3.8+
docker -v             # Need Docker
Git --version         # Need Git
```

### 2️⃣ Get Instagram Credentials (2 min)

1. Go to https://developers.facebook.com
2. Create App → Instagram Basic Display
3. Get **App ID** and **App Secret**
4. Add Redirect URI: `http://localhost:8080/login/oauth2/code/instagram`

### 3️⃣ Clone & Configure (1 min)

```bash
# Clone
git clone https://github.com/ro7toz/shout-app.git
cd shout-app

# Set environment variables (Linux/Mac)
export INSTAGRAM_CLIENT_ID="your_app_id"
export INSTAGRAM_CLIENT_SECRET="your_app_secret"

# OR Windows PowerShell
$env:INSTAGRAM_CLIENT_ID="your_app_id"
$env:INSTAGRAM_CLIENT_SECRET="your_app_secret"
```

### 4️⃣ Start Infrastructure (1 min)

```bash
# Start Docker services
docker-compose up -d

# Verify (should see 2 containers)
docker ps
```

### 5️⃣ Run Application (bonus, automatic)

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/shout-app-1.0.0.jar

# Open browser: http://localhost:8080 🎉
```

---

## 🔍 What You Get

| Feature | URL |
|---------|-----|
| 🎯 Homepage | http://localhost:8080 |
| 📄 Dashboard (login first) | http://localhost:8080/dashboard |
| 📊 Health | http://localhost:8080/actuator/health |
| 📈 Metrics | http://localhost:8080/actuator/prometheus |

---

## 🌟 Test the App

### Scenario: Exchange a Shoutout

1. **Login User A** (Browser 1)
   - Click "Login with Instagram"
   - Authorize the app
   - See "Explore Creators" page

2. **Login User B** (Private/Incognito Browser 2)
   - Repeat login with different account

3. **User A sends request to User B**
   - Find User B's card
   - Click "Request"
   - Paste Instagram post URL
   - Click "Send"

4. **User B accepts**
   - See notification
   - Go to Dashboard → Received
   - Click "Accept"
   - 24-hour timer starts ⏱

5. **Both users post**
   - Click "I posted it" when ready
   - Both get added to each other's Circle 🄟

6. **Rate each other**
   - If someone doesn't post, rate 1-5 stars 🌟

---

## 📂 Common Commands

### Development
```bash
# Run in development mode
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Run tests
mvn test

# View logs
tail -f logs/shout.log
```

### Docker
```bash
# View running services
docker ps

# View logs
docker logs -f shout-postgres
docker logs -f shout-redis

# Stop all services
docker-compose down

# Restart services
docker-compose restart
```

### Database
```bash
# Connect to PostgreSQL
docker exec -it shout-postgres psql -U shoutuser -d shoutdb

# View tables
\dt

# Exit
\q

# Connect to Redis
docker exec -it shout-redis redis-cli
```

---

## ⚠️ Troubleshooting

| Problem | Solution |
|---------|----------|
| Port 8080 in use | Change: `export SERVER_PORT=8081` |
| DB connection error | Run: `docker-compose restart postgres` |
| OAuth error | Verify redirect URI in Instagram app |
| "BUILD FAILURE" | Run: `mvn clean install` |
| OutOfMemory | Set: `export JAVA_OPTS="-Xmx2g"` |

---

## 📋 Full Documentation

- **Setup Guide** - [SETUP_GUIDE.md](../SETUP_GUIDE.md)
- **Deployment** - [.github/DEPLOYMENT.md](.github/DEPLOYMENT.md)
- **Improvements** - [.github/IMPROVEMENTS.md](.github/IMPROVEMENTS.md)
- **README** - [README.md](../README.md)

---

## 📞 Need Help?

```bash
# Check health
curl http://localhost:8080/actuator/health

# Check metrics
curl http://localhost:8080/actuator/metrics

# View Docker logs
docker-compose logs -f

# View application logs
tail -f logs/shout.log
```

---

**Ready to shout? 📣**

Happy coding! 🎆
