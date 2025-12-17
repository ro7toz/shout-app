# Stage 1: Build
FROM maven:3.9.0-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /app/target/shout-app-*.jar app.jar

RUN addgroup -g 1000 shoutx && adduser -D -u 1000 -G shoutx shoutx
USER shoutx

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
