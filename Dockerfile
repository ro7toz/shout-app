FROM maven:3.8.1-openjdk-17-slim as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:22-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/shout-app-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
