# ShoutX API Endpoints Reference

## Base URL

```
http://localhost:8080/api
```

## Authentication Endpoints

### Signup

```
POST /auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "name": "John Doe",
  "password": "password123"
}

Response:
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "token": "jwt_token_here",
  "message": "Signup successful"
}
```

### Login

```
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "token": "jwt_token_here",
  "planType": "BASIC",
  "message": "Login successful"
}
```

### OAuth Callback

```
POST /auth/oauth/instagram/callback?code=instagram_code

Response:
{
  "id": 1,
  "email": "instagram_id@instagram.com",
  "name": "Instagram Name",
  "token": "jwt_token_here",
  "planType": "BASIC",
  "isNewUser": true
}
```

## User Endpoints

### Get Profile

```
GET /users/{userId}

Response:
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "profilePictureUrl": "https://...",
  "bio": "Creator",
  "accountType": "CREATOR",
  "isVerified": false,
  "rating": 4.8,
  "photos": ["url1", "url2", "url3"]
}
```

### Update Profile

```
PUT /users/{userId}/profile
Authorization: Bearer token
Content-Type: application/json

{
  "name": "John Doe",
  "bio": "Content Creator",
  "accountType": "INFLUENCER"
}
```

### Upload Photo

```
POST /users/{userId}/photos
Authorization: Bearer token
Content-Type: multipart/form-data

Form Data:
- file: [image_file]

Response:
{
  "id": 123,
  "url": "https://s3.../photo.jpg"
}
```

### Delete Photo

```
DELETE /users/{userId}/photos/{photoId}
Authorization: Bearer token

Response:
{
  "success": true,
  "message": "Photo deleted successfully"
}
```

### Search Users

```
GET /users/search?genre=CREATOR

Response: Array of user profiles
```

## Request Endpoints

### Create Request

```
POST /requests
Authorization: Bearer token
Content-Type: application/json

{
  "receiverId": 2,
  "photoId": 5
}

Response:
{
  "id": 1,
  "senderId": 1,
  "senderName": "John Doe",
  "receiverId": 2,
  "receiverName": "Jane Doe",
  "photoUrl": "https://...",
  "status": "PENDING",
  "deadline": "2025-12-21T12:38:00",
  "createdAt": "2025-12-20T12:38:00"
}
```

### Get Received Requests

```
GET /requests/received
Authorization: Bearer token

Response: Array of request objects
```

### Get Sent Requests

```
GET /requests/sent
Authorization: Bearer token

Response: Array of request objects
```

### Accept Request

```
PUT /requests/{requestId}/accept
Authorization: Bearer token

Response: Updated request object
```

### Complete Request

```
PUT /requests/{requestId}/complete
Authorization: Bearer token

Response: Updated request object
```

### Rate Request

```
POST /requests/{requestId}/rate
Authorization: Bearer token
Content-Type: application/json

{
  "rating": 5,
  "comment": "Great collaboration!"
}

Response:
{
  "success": true,
  "message": "Request rated successfully"
}
```

## Notification Endpoints

### Get Notifications

```
GET /notifications
Authorization: Bearer token

Response:
{
  "notifications": [...],
  "unreadCount": 3
}
```

### Mark as Read

```
PUT /notifications/{notificationId}/read
Authorization: Bearer token

Response:
{
  "success": true
}
```

## Error Responses

```
400 Bad Request:
{
  "code": "BAD_REQUEST",
  "message": "Error message"
}

401 Unauthorized:
{
  "code": "UNAUTHORIZED",
  "message": "Invalid or missing token"
}

404 Not Found:
{
  "code": "NOT_FOUND",
  "message": "Resource not found"
}

500 Internal Server Error:
{
  "code": "INTERNAL_ERROR",
  "message": "An unexpected error occurred"
}
```

## Testing with cURL

```bash
# Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","name":"Test","password":"pass123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123"}'

# Get Profile
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/api/users/1

# Create Request
curl -X POST http://localhost:8080/api/requests \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"receiverId":2,"photoId":5}'
```
