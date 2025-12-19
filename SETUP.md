# ShoutX - Setup Guide

## Prerequisites

- Java 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+
- Git

## Installation Steps

### 1. Clone Repository

```bash
git clone https://github.com/ro7toz/shout-app.git
cd shout-app
```

### 2. Setup Database

```bash
# Create database
mysql -u root -p
CREATE DATABASE shoutx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3. Configure Environment

```bash
# Copy example env file
cp .env.example .env

# Edit .env with your credentials
vim .env
```

Key configurations:
- `DB_PASSWORD`: MySQL password
- `JWT_SECRET`: Min 32 characters
- `INSTAGRAM_CLIENT_ID` & `INSTAGRAM_CLIENT_SECRET`: From Instagram Developer
- `AWS_*`: AWS credentials
- `RAZORPAY_*`: Payment gateway keys
- `SENDGRID_API_KEY`: Email service

### 4. Build Project

```bash
# Download dependencies and build
mvn clean install

# Skip tests if needed
mvn clean install -DskipTests
```

### 5. Run Application

```bash
# Using Maven
mvn spring-boot:run

# Or using Java
java -jar target/shout-app-1.0.0.jar
```

Application will start at: `http://localhost:8080`

## Access Points

- **Frontend**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **API Base**: http://localhost:8080/api

## Database Setup

Flyway will automatically:
1. Create all tables
2. Create indexes
3. Setup relationships

On first run, check logs for migration status.

## Troubleshooting

### Database Connection Failed

```bash
# Check MySQL is running
mysql -u root -p -e "SELECT 1"

# Verify database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'shoutx'"
```

### Port 8080 Already in Use

```bash
# Use different port
java -jar target/shout-app-1.0.0.jar --server.port=8081
```

### Redis Connection Failed

```bash
# Ensure Redis is running
redis-cli ping
# Should return: PONG
```

## Docker Setup (Alternative)

```bash
# Build Docker image
docker build -t shoutx-app .

# Run with Docker Compose
docker-compose up
```

## Next Steps

1. Review API documentation
2. Create test users
3. Test authentication
4. Implement frontend
5. Deploy to production

## Support

Email: tushkinit@gmail.com
