# Shout Application - Production Deployment Guide

## Pre-Deployment Checklist

### Security
- [ ] All sensitive data in environment variables
- [ ] Database credentials rotated
- [ ] SSL/TLS certificates obtained
- [ ] CORS properly configured
- [ ] Security headers enabled
- [ ] Dependencies scanned for vulnerabilities
- [ ] Code reviewed for secrets
- [ ] WAF rules configured

### Performance
- [ ] Load testing completed (>100 concurrent users)
- [ ] Database indexes created
- [ ] Query optimization done
- [ ] Caching strategy in place
- [ ] CDN configured (if needed)
- [ ] Connection pool sizes tuned

### Operations
- [ ] Monitoring and alerting setup
- [ ] Log aggregation configured
- [ ] Backup strategy implemented
- [ ] Disaster recovery plan documented
- [ ] On-call rotation established
- [ ] Runbooks created
- [ ] Infrastructure as Code written

## Environment Variables (Production)

```bash
# Database
DATABASE_URL=jdbc:postgresql://prod-host:5432/shoutdb
DATABASE_USERNAME=prod_user
DATABASE_PASSWORD=$(openssl rand -base64 32)

# Redis
REDIS_HOST=prod-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=$(openssl rand -base64 32)

# Instagram OAuth
INSTAGRAM_CLIENT_ID=your_production_client_id
INSTAGRAM_CLIENT_SECRET=your_production_client_secret

# Application
SPRING_PROFILES_ACTIVE=prod
PORT=8080
BASE_URL=https://yourapp.com

# JVM
JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC
```

## Docker Deployment

### Build Production Image

```dockerfile
# Multi-stage build for optimization
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /build/target/shout-app-1.0.0.jar app.jar
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Docker Registry Setup

```bash
# Login to registry (e.g., Docker Hub, ECR, GCR)
docker login

# Tag image
docker tag shout-app:1.0.0 yourregistry/shout-app:1.0.0
docker tag shout-app:1.0.0 yourregistry/shout-app:latest

# Push image
docker push yourregistry/shout-app:1.0.0
docker push yourregistry/shout-app:latest
```

## Kubernetes Deployment

### Namespace Setup

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: shout
```

### ConfigMap

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: shout-config
  namespace: shout
data:
  SPRING_PROFILES_ACTIVE: "prod"
  BASE_URL: "https://yourapp.com"
```

### Secrets

```bash
kubectl create secret generic db-secret \
  --from-literal=username=prod_user \
  --from-literal=password=$(openssl rand -base64 32) \
  -n shout

kubectl create secret generic oauth-secret \
  --from-literal=client-id=xxx \
  --from-literal=client-secret=xxx \
  -n shout
```

### Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: shout-app
  namespace: shout
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: shout
  template:
    metadata:
      labels:
        app: shout
    spec:
      containers:
      - name: shout
        image: yourregistry/shout-app:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: INSTAGRAM_CLIENT_ID
          valueFrom:
            secretKeyRef:
              name: oauth-secret
              key: client-id
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        volumeMounts:
        - name: logs
          mountPath: /app/logs
      volumes:
      - name: logs
        emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: shout-app
  namespace: shout
spec:
  selector:
    app: shout
  type: ClusterIP
  ports:
  - port: 80
    targetPort: 8080
    protocol: TCP
```

### Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: shout-ingress
  namespace: shout
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
  - hosts:
    - yourapp.com
    secretName: shout-tls
  rules:
  - host: yourapp.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: shout-app
            port:
              number: 80
```

## Database Migration

### PostgreSQL Production Setup

```bash
# Create database
CREATE DATABASE shoutdb;

# Create user
CREATE USER shoutuser WITH ENCRYPTED PASSWORD 'secure_password';

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE shoutdb TO shoutuser;

# Connect to database and create indexes
\c shoutdb

CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_category ON users(category);
CREATE INDEX idx_user_rating ON users(average_rating DESC);
CREATE INDEX idx_request_status ON shoutout_requests(status);
CREATE INDEX idx_notification_user ON notifications(user_id, is_read);
```

### Backup Strategy

```bash
# Daily backup
*/2 * * * * pg_dump -h db-host -U shoutuser -d shoutdb | gzip > /backups/shout-$(date +\%Y\%m\%d-\%H\%M).sql.gz

# Retention: Keep last 30 days
find /backups -name "shout-*.sql.gz" -mtime +30 -delete
```

## Monitoring & Alerting

### Prometheus Scrape Configuration

```yaml
scrape_configs:
  - job_name: 'shout-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### Key Metrics to Monitor

```
http_requests_total
http_request_duration_seconds
db_connection_pool_active
db_connection_pool_max
cache_gets_total
cache_misses_total
jvm_memory_used_bytes
jvm_gc_pause_seconds
```

### Alert Rules

```yaml
groups:
- name: shout_alerts
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
    for: 5m
    annotations:
      summary: "High error rate detected"

  - alert: DatabaseConnectionPoolExhausted
    expr: db_connection_pool_active / db_connection_pool_max > 0.9
    for: 5m
    annotations:
      summary: "Database connection pool nearly full"

  - alert: HighMemoryUsage
    expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9
    for: 5m
    annotations:
      summary: "JVM memory usage exceeding 90%"
```

## Security Hardening

### SSL/TLS Configuration

```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
  port: 8443
```

### Security Headers

```java
@Configuration
public class SecurityHeadersConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
            .contentSecurityPolicy(csp -> csp.policyDirectives(
                "default-src 'self'; " +
                "script-src 'self'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:"
            ))
            .xssProtection()
            .frameOptions(frameOptions -> frameOptions.deny())
        );
        return http.build();
    }
}
```

## Performance Optimization

### Database Connection Pool

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 30
      minimum-idle: 10
      connection-timeout: 30000
      max-lifetime: 1800000
      idle-timeout: 600000
```

### Redis Optimization

```yaml
spring:
  data:
    redis:
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 30
          max-idle: 10
          min-idle: 5
          max-wait: -1ms
```

### JVM Tuning

```bash
JAVA_OPTS="-Xmx2g -Xms1g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:InitiatingHeapOccupancyPercent=35 \
  -XX:+ParallelRefProcEnabled \
  -XX:+UnlockDiagnosticVMOptions \
  -XX:G1SummarizeRSetStatsPeriod=1000"
```

## Rollback Plan

```bash
# Kubernetes rollback
kubectl rollout undo deployment/shout-app -n shout
kubectl rollout history deployment/shout-app -n shout

# Docker rollback
docker service update --image yourregistry/shout-app:previous shout-app

# Database rollback (if migrations failed)
psql -d shoutdb -f rollback.sql
```

## Incident Response

### Database Connection Pool Exhausted

```bash
# Check connection pool status
curl http://localhost:8080/actuator/metrics/db.connection.pool.active

# Increase pool size
# Update spring.datasource.hikari.maximum-pool-size
# Redeploy application
```

### High Memory Usage

```bash
# Check heap memory
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Trigger garbage collection (if safe)
# Monitor with: jcmd <pid> GC.run

# If persists, increase heap size and redeploy
JAVA_OPTS="-Xmx4g -Xms2g"
```

### Circuit Breaker Open (Instagram API)

```bash
# Check status
curl http://localhost:8080/actuator/circuitbreakers

# Circuit automatically recovers after wait duration
# Manual recovery (use with caution):
curl -X POST http://localhost:8080/actuator/circuitbreakers/instagram
```

## Success Criteria

- [ ] Application starts without errors
- [ ] Health check returns UP status
- [ ] Database connection working
- [ ] Redis cache operational
- [ ] OAuth login functional
- [ ] All endpoints responding
- [ ] Load test: 100+ concurrent users
- [ ] Uptime: >99.5% monitored
- [ ] Response time: p95 <500ms
- [ ] Error rate: <0.1%

---

**Last Updated: December 2025**
