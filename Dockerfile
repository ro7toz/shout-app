# Multi-stage build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY --from=builder /build/target/shout-app-1.0.0.jar app.jar
RUN adduser -D appuser && chown appuser:appuser /app && mkdir -p /app/logs && chown appuser:appuser /app/logs
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 CMD java -cp /app/app.jar org.springframework.boot.loader.launch.JarLauncher curl -f http://localhost:8080/actuator/health || exit 1
CMD ["java", "-jar", "app.jar"]
