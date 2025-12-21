spring:
  application:
    name: shoutx-app
  
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/shoutdb}
    username: ${DB_USER:shoutuser}
    password: ${DB_PASSWORD:password123}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate  # Changed from update to validate for production safety
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
  
  flyway:
    enabled: true
    baseline-on-migrate: true
    validate-on-migrate: true

server:
  port: 8080

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

instagram:
  client-id: ${INSTAGRAM_CLIENT_ID}
  client-secret: ${INSTAGRAM_CLIENT_SECRET}
  redirect-uri: ${INSTAGRAM_REDIRECT_URI:http://localhost:3000/auth/callback/instagram}
  graph-api-url: https://graph.instagram.com
  api-version: v21.0

app:
  cors:
    allowed-origins: http://localhost:3000,http://localhost:5173
