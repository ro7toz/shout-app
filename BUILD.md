# ShoutX - Build & Deployment Guide

## Development Build

```bash
# Clean and build
mvn clean package

# Run tests
mvn test

# Skip tests and build
mvn clean package -DskipTests
```

## Production Build

```bash
# Build with production profile
mvn clean package -P prod -DskipTests

# JAR file location
ls -lh target/shout-app-1.0.0.jar
```

## Docker Build

```bash
# Build image
docker build -t shoutx:latest .

# Run container
docker run -p 8080:8080 --env-file .env shoutx:latest

# Or with Docker Compose
docker-compose up -d
docker-compose logs -f
```

## Deployment Steps

### AWS EC2 Deployment

```bash
# 1. SSH into EC2 instance
ssh -i your-key.pem ubuntu@your-instance-ip

# 2. Install Java
sudo apt-get update
sudo apt-get install openjdk-17-jdk -y

# 3. Install MySQL
sudo apt-get install mysql-server -y
sudo mysql_secure_installation

# 4. Install Redis
sudo apt-get install redis-server -y

# 5. Clone repository
cd /opt
sudo git clone https://github.com/ro7toz/shout-app.git
cd shout-app

# 6. Setup environment
sudo cp .env.example .env
sudo vim .env  # Configure with production values

# 7. Build application
cd /opt/shout-app
mvn clean package -DskipTests

# 8. Run as service (create shoutx.service file)
# See systemd configuration below
```

### Heroku Deployment

```bash
# 1. Install Heroku CLI
# https://devcenter.heroku.com/articles/heroku-cli

# 2. Login to Heroku
heroku login

# 3. Create app
heroku create shoutx-app

# 4. Add buildpacks
heroku buildpacks:add heroku/jvm
heroku buildpacks:add heroku/maven

# 5. Set environment variables
heroku config:set DB_PASSWORD=your_password
heroku config:set JWT_SECRET=your_secret_key
# ... set all required variables

# 6. Deploy
git push heroku main

# 7. View logs
heroku logs --tail
```

## Systemd Service (Linux)

Create `/etc/systemd/system/shoutx.service`:

```ini
[Unit]
Description=ShoutX Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/shout-app
ExecStart=/usr/bin/java -jar /opt/shout-app/target/shout-app-1.0.0.jar
Restart=on-failure
RestartSec=10
EnvironmentFile=/opt/shout-app/.env

[Install]
WantedBy=multi-user.target
```

Then:

```bash
sudo systemctl daemon-reload
sudo systemctl enable shoutx
sudo systemctl start shoutx
sudo systemctl status shoutx
```

## Database Backup

```bash
# Backup
mysqldump -u root -p shoutx > backup.sql

# Restore
mysql -u root -p shoutx < backup.sql
```

## SSL Certificate (Let's Encrypt)

```bash
sudo apt-get install certbot python3-certbot-nginx -y
sudo certbot certonly --standalone -d shoutx.app

# Configure application.yml with SSL
server:
  ssl:
    key-store: /etc/letsencrypt/live/shoutx.app/keystore.p12
    key-store-password: your-password
    key-store-type: PKCS12
```

## Monitoring & Logs

```bash
# View application logs
cat logs/shoutx.log

# Monitor in real-time
tail -f logs/shoutx.log

# Check health endpoint
curl http://localhost:8080/actuator/health
```

## Rollback Procedure

```bash
# Keep previous JAR
cp target/shout-app-1.0.0.jar ~/backups/shout-app-1.0.0-old.jar

# If rollback needed
sudo systemctl stop shoutx
sudo cp ~/backups/shout-app-1.0.0-old.jar target/shout-app-1.0.0.jar
sudo systemctl start shoutx
```

## Post-Deployment Checklist

- [ ] Database migrations ran successfully
- [ ] All environment variables set
- [ ] Health check endpoint returns 200
- [ ] API endpoints responding
- [ ] SSL certificate installed
- [ ] Backups configured
- [ ] Monitoring setup
- [ ] Team notified

