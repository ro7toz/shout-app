# 🚀 ShoutX - Complete AWS Free Tier Deployment Guide

## Prerequisites
- AWS Account (Free Tier eligible)
- Domain: `shoutx.co.in`
- Instagram/Facebook App credentials
- Git installed locally

---

## Part 1: AWS Setup

### Step 1: Create AWS Account
1. Go to https://aws.amazon.com
2. Click "Create an AWS Account"
3. Complete registration (requires credit card for verification)
4. Verify email

### Step 2: Set Up IAM User (Security Best Practice)
```bash
# Don't use root account for deployments
1. Go to IAM → Users → Add User
2. Username: shoutx-deployer
3. Enable: Programmatic access + AWS Management Console access
4. Attach policies:
   - AmazonEC2FullAccess
   - AmazonRDSFullAccess
   - ElasticLoadBalancingFullAccess
   - AmazonRoute53FullAccess
5. Save credentials (Access Key ID + Secret Access Key)
```

---

## Part 2: Database Setup (RDS PostgreSQL)

### Option A: RDS Free Tier (Recommended)
```bash
1. Go to RDS → Create database
2. Choose:
   - Engine: PostgreSQL 15
   - Template: Free tier
   - DB instance: db.t3.micro (750 hours/month free)
   - Storage: 20 GB (Free tier limit)
   
3. Settings:
   - DB instance identifier: shoutx-db
   - Master username: shoutxadmin
   - Master password: [Generate strong password]
   
4. Connectivity:
   - VPC: Default
   - Public access: Yes
   - VPC security group: Create new → shoutx-db-sg
   
5. Additional configuration:
   - Initial database name: shoutxdb
   - Automated backups: 7 days retention
   
6. Click "Create database"
7. Wait 5-10 minutes for creation
```

### Security Group Configuration:
```bash
1. Go to EC2 → Security Groups → shoutx-db-sg
2. Edit inbound rules:
   - Type: PostgreSQL
   - Port: 5432
   - Source: 0.0.0.0/0 (or restrict to your EC2 IP later)
```

### Get Connection Details:
```bash
# After creation, note down:
Endpoint: shoutx-db.xxxxxxxxx.ap-south-1.rds.amazonaws.com
Port: 5432
Database: shoutxdb
Username: shoutxadmin
Password: [Your password]
```

---

## Part 3: Cache Setup (ElastiCache Redis)

### Option A: ElastiCache Free Tier
```bash
1. Go to ElastiCache → Redis → Create
2. Choose:
   - Cluster mode: Disabled
   - Node type: cache.t3.micro (750 hours/month free)
   - Number of replicas: 0
   
3. Settings:
   - Name: shoutx-redis
   - Port: 6379
   
4. Subnet group: Create new
5. Security group: Create new → shoutx-redis-sg
6. Click "Create"
```

### Security Group Configuration:
```bash
1. Go to EC2 → Security Groups → shoutx-redis-sg
2. Edit inbound rules:
   - Type: Custom TCP
   - Port: 6379
   - Source: [Your EC2 security group ID]
```

---

## Part 4: EC2 Instance Setup

### Create EC2 Instance
```bash
1. Go to EC2 → Launch Instance
2. Name: shoutx-app
3. Choose AMI:
   - Amazon Linux 2023 (Free tier eligible)
   
4. Instance type:
   - t2.micro (750 hours/month free)
   
5. Key pair:
   - Create new key pair → shoutx-key.pem
   - Download and save securely
   
6. Network settings:
   - Create security group: shoutx-app-sg
   - Allow SSH (22) from My IP
   - Allow HTTP (80) from Anywhere
   - Allow HTTPS (443) from Anywhere
   - Allow Custom TCP (8080) from Anywhere
   
7. Storage: 30 GB gp3 (Free tier limit)
8. Click "Launch instance"
```

### Connect to EC2
```bash
# Linux/Mac
chmod 400 shoutx-key.pem
ssh -i shoutx-key.pem ec2-user@<EC2-PUBLIC-IP>

# Windows (use PuTTY or PowerShell)
ssh -i shoutx-key.pem ec2-user@<EC2-PUBLIC-IP>
```

---

## Part 5: Install Software on EC2

### Install Java 17
```bash
sudo yum update -y
sudo yum install java-17-amazon-corretto-devel -y
java -version
```

### Install Maven
```bash
cd /opt
sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
sudo tar xzf apache-maven-3.9.6-bin.tar.gz
sudo ln -s apache-maven-3.9.6 maven

# Add to PATH
echo "export M2_HOME=/opt/maven" | sudo tee -a /etc/profile.d/maven.sh
echo "export PATH=\${M2_HOME}/bin:\${PATH}" | sudo tee -a /etc/profile.d/maven.sh
source /etc/profile.d/maven.sh

mvn -version
```

### Install Git
```bash
sudo yum install git -y
git --version
```

### Install Nginx (Reverse Proxy)
```bash
sudo yum install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

---

## Part 6: Deploy Application

### Clone Repository
```bash
cd /home/ec2-user
git clone https://github.com/ro7toz/shout-app.git
cd shout-app
```

### Configure Application
```bash
# Create production config
sudo nano /home/ec2-user/shout-app/src/main/resources/application-prod.yml
```

```yaml
spring:
  application:
    name: shoutx
  datasource:
    url: jdbc:postgresql://shoutx-db.xxxxxxxxx.ap-south-1.rds.amazonaws.com:5432/shoutxdb
    username: shoutxadmin
    password: YOUR_DB_PASSWORD
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  redis:
    host: shoutx-redis.xxxxxx.cache.amazonaws.com
    port: 6379
  security:
    oauth2:
      client:
        registration:
          instagram:
            client-id: ${INSTAGRAM_CLIENT_ID}
            client-secret: ${INSTAGRAM_CLIENT_SECRET}
            redirect-uri: 'https://shoutx.co.in/login/oauth2/code/instagram'

facebook:
  app-id: ${FACEBOOK_APP_ID}
  app-secret: ${FACEBOOK_APP_SECRET}

server:
  port: 8080

logging:
  level:
    root: INFO
    com.shout: INFO
```

### Set Environment Variables
```bash
sudo nano /etc/environment
```

```bash
SPRING_PROFILES_ACTIVE=prod
INSTAGRAM_CLIENT_ID=your_instagram_client_id
INSTAGRAM_CLIENT_SECRET=your_instagram_client_secret
FACEBOOK_APP_ID=your_facebook_app_id
FACEBOOK_APP_SECRET=your_facebook_app_secret
```

```bash
source /etc/environment
```

### Build Application
```bash
cd /home/ec2-user/shout-app
mvn clean package -DskipTests
```

---

## Part 7: Create Systemd Service

```bash
sudo nano /etc/systemd/system/shoutx.service
```

```ini
[Unit]
Description=ShoutX Spring Boot Application
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/shout-app
ExecStart=/usr/bin/java -Xmx512m -Xms256m -Dspring.profiles.active=prod -jar /home/ec2-user/shout-app/target/shout-app-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=shoutx

Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="INSTAGRAM_CLIENT_ID=your_instagram_client_id"
Environment="INSTAGRAM_CLIENT_SECRET=your_instagram_client_secret"
Environment="FACEBOOK_APP_ID=your_facebook_app_id"
Environment="FACEBOOK_APP_SECRET=your_facebook_app_secret"

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable shoutx
sudo systemctl start shoutx
sudo systemctl status shoutx
```

---

## Part 8: Configure Nginx Reverse Proxy

```bash
sudo nano /etc/nginx/conf.d/shoutx.conf
```

```nginx
server {
    listen 80;
    server_name shoutx.co.in www.shoutx.co.in;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name shoutx.co.in www.shoutx.co.in;

    # SSL certificates (will be added later with Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/shoutx.co.in/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/shoutx.co.in/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Logging
    access_log /var/log/nginx/shoutx_access.log;
    error_log /var/log/nginx/shoutx_error.log;

    # Proxy settings
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
        
        # Timeouts
        proxy_connect_timeout 600s;
        proxy_send_timeout 600s;
        proxy_read_timeout 600s;
    }

    # Static files caching
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://localhost:8080;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

```bash
sudo nginx -t
sudo systemctl restart nginx
```

---

## Part 9: Domain Configuration

### Update DNS Records
Go to your domain registrar (where you bought shoutx.co.in):

| Type | Name | Value | TTL |
|------|------|-------|-----|
| A | @ | <EC2-PUBLIC-IP> | 3600 |
| A | www | <EC2-PUBLIC-IP> | 3600 |

Wait 5-60 minutes for DNS propagation.

---

## Part 10: SSL Certificate (Let's Encrypt)

### Install Certbot
```bash
sudo yum install python3 augeas-libs -y
sudo python3 -m venv /opt/certbot/
sudo /opt/certbot/bin/pip install --upgrade pip
sudo /opt/certbot/bin/pip install certbot certbot-nginx
sudo ln -s /opt/certbot/bin/certbot /usr/bin/certbot
```

### Obtain Certificate
```bash
# First, temporarily remove SSL config from nginx
sudo nano /etc/nginx/conf.d/shoutx.conf
# Comment out the HTTPS server block temporarily

sudo systemctl reload nginx

# Get certificate
sudo certbot --nginx -d shoutx.co.in -d www.shoutx.co.in
```

Follow prompts:
1. Enter email: tushkinit@gmail.com
2. Agree to terms: Yes
3. Redirect HTTP to HTTPS: Yes

### Auto-renewal
```bash
# Test renewal
sudo certbot renew --dry-run

# Set up automatic renewal
echo "0 0,12 * * * root /opt/certbot/bin/python -c 'import random; import time; time.sleep(random.random() * 3600)' && sudo certbot renew -q" | sudo tee -a /etc/crontab > /dev/null
```

---

## Part 11: Instagram OAuth Configuration

### Update Instagram App Settings
1. Go to https://developers.facebook.com
2. Select your app
3. Instagram Basic Display → Basic Display
4. Valid OAuth Redirect URIs:
   - https://shoutx.co.in/login/oauth2/code/instagram
   - https://www.shoutx.co.in/login/oauth2/code/instagram
5. Deauthorize Callback URL: https://shoutx.co.in/data-deletion
6. Data Deletion Request URL: https://shoutx.co.in/data-deletion
7. Save Changes

---

## Part 12: Monitoring & Logs

### View Application Logs
```bash
# System logs
sudo journalctl -u shoutx -f

# Nginx logs
sudo tail -f /var/log/nginx/shoutx_access.log
sudo tail -f /var/log/nginx/shoutx_error.log
```

### System Monitoring
```bash
# CPU and Memory
htop

# Disk usage
df -h

# Check Java process
ps aux | grep java
```

---

## Part 13: Backup Strategy

### Database Backups
```bash
# Create backup script
sudo nano /home/ec2-user/backup-db.sh
```

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/home/ec2-user/backups"
mkdir -p $BACKUP_DIR

PGPASSWORD="YOUR_DB_PASSWORD" pg_dump \
  -h shoutx-db.xxxxxxxxx.ap-south-1.rds.amazonaws.com \
  -U shoutxadmin \
  -d shoutxdb \
  | gzip > $BACKUP_DIR/shoutx_backup_$DATE.sql.gz

# Keep only last 7 days
find $BACKUP_DIR -name "shoutx_backup_*.sql.gz" -mtime +7 -delete
```

```bash
chmod +x /home/ec2-user/backup-db.sh

# Add to crontab (daily at 2 AM)
(crontab -l 2>/dev/null; echo "0 2 * * * /home/ec2-user/backup-db.sh") | crontab -
```

---

## Part 14: Security Hardening

### Enable Firewall
```bash
sudo yum install firewalld -y
sudo systemctl start firewalld
sudo systemctl enable firewalld

sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --reload
```

### Fail2Ban (Prevent Brute Force)
```bash
sudo yum install epel-release -y
sudo yum install fail2ban -y

sudo systemctl start fail2ban
sudo systemctl enable fail2ban
```

### Update Security Groups
- **EC2 Security Group:**
  - SSH (22): Only from your IP
  - HTTP (80): 0.0.0.0/0
  - HTTPS (443): 0.0.0.0/0
  - Remove port 8080 from public access

- **RDS Security Group:**
  - PostgreSQL (5432): Only from EC2 security group

- **Redis Security Group:**
  - Redis (6379): Only from EC2 security group

---

## Part 15: Deployment Checklist

### Pre-Deployment
- [ ] AWS account created and verified
- [ ] IAM user created with proper permissions
- [ ] RDS PostgreSQL database running
- [ ] ElastiCache Redis running
- [ ] EC2 instance running
- [ ] Domain DNS configured
- [ ] Instagram OAuth URLs updated

### During Deployment
- [ ] Application built successfully
- [ ] Systemd service running
- [ ] Nginx reverse proxy configured
- [ ] SSL certificate installed
- [ ] All environment variables set
- [ ] Database connections working

### Post-Deployment
- [ ] Test https://shoutx.co.in loads
- [ ] Test Instagram login works
- [ ] Test user discovery
- [ ] Test shoutout requests
- [ ] Test notifications
- [ ] Monitor logs for errors
- [ ] Set up backups
- [ ] Configure monitoring

---

## Part 16: Troubleshooting

### Application Won't Start
```bash
# Check service status
sudo systemctl status shoutx

# View logs
sudo journalctl -u shoutx -n 100 --no-pager

# Common issues:
# 1. Wrong environment variables
# 2. Database connection failed
# 3. Port 8080 already in use
# 4. Out of memory
```

### Database Connection Issues
```bash
# Test connection
psql -h shoutx-db.xxxxxxxxx.ap-south-1.rds.amazonaws.com \
     -U shoutxadmin \
     -d shoutxdb

# Check security group allows EC2 IP
# Verify credentials in application-prod.yml
```

### SSL Certificate Issues
```bash
# Check certificate status
sudo certbot certificates

# Renew manually
sudo certbot renew

# Check nginx config
sudo nginx -t
```

### 502 Bad Gateway
```bash
# Check if Spring Boot is running
sudo systemctl status shoutx

# Check if port 8080 is listening
sudo netstat -tuln | grep 8080

# Restart services
sudo systemctl restart shoutx
sudo systemctl restart nginx
```

---

## Part 17: Cost Optimization (Stay in Free Tier)

### Monthly Free Tier Limits
- EC2: 750 hours of t2.micro/t3.micro
- RDS: 750 hours of db.t3.micro
- ElastiCache: 750 hours of cache.t3.micro
- Data Transfer: 15 GB outbound
- Storage: 30 GB total

### Tips to Stay Free
1. Single instance only - Don't create multiple EC2 instances
2. Stop unused resources - Stop RDS/ElastiCache when testing
3. Monitor usage - Check AWS Billing Dashboard weekly
4. Set billing alerts - Create alert at $1
5. Clean up snapshots - Delete old RDS snapshots
6. Optimize images - Use WebP format to reduce bandwidth

### Set Up Billing Alert
1. Go to AWS Billing → Billing preferences
2. Enable "Receive Billing Alerts"
3. Go to CloudWatch → Alarms → Create alarm
4. Select: Billing → Total Estimated Charge
5. Threshold: $1
6. Email: tushkinit@gmail.com

---

## Part 18: Continuous Deployment

### Create Deployment Script
```bash
sudo nano /home/ec2-user/deploy.sh
```

```bash
#!/bin/bash
cd /home/ec2-user/shout-app

echo "Pulling latest code..."
git pull origin main

echo "Building application..."
mvn clean package -DskipTests

echo "Restarting service..."
sudo systemctl restart shoutx

echo "Deployment complete!"
sudo systemctl status shoutx
```

```bash
chmod +x /home/ec2-user/deploy.sh
```

### Deploy Updates
```bash
cd /home/ec2-user
./deploy.sh
```

---

## Final Verification

### Test All Endpoints
```bash
# Homepage
curl -I https://shoutx.co.in

# Health check
curl https://shoutx.co.in/actuator/health

# API test
curl https://shoutx.co.in/users/page/0
```

### Success Criteria
✅ Homepage loads with HTTPS
✅ Instagram login redirects correctly
✅ Users can discover creators
✅ Shoutout requests work
✅ Notifications appear
✅ Database persists data
✅ No errors in logs

---

## Support

If you encounter issues:
1. Check logs: `sudo journalctl -u shoutx -f`
2. Verify environment variables
3. Test database connection
4. Check security groups
5. Review nginx error logs

Contact: tushkinit@gmail.com

---

## 🎉 Congratulations!

Your ShoutX application is now live at https://shoutx.co.in!

**Total Setup Time:** 2-3 hours
**Monthly Cost:** $0 (Free Tier)

### Next Steps:
- [ ] Share with users
- [ ] Monitor performance
- [ ] Gather feedback
- [ ] Iterate and improve

Happy creating! 🚀
