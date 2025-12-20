# Rating Service & Exchange Controller Implementation

## Date: December 20, 2025

### Overview
Successfully added critical components for exchange lifecycle management, user rating system, and 24-hour timer verification without any errors.

---

## Files Added

### 1. **RatingService.java** ✅
**Location:** `src/main/java/com/shout/service/RatingService.java`

#### Purpose
Handles all user rating operations after exchanges with automatic average rating calculation and notification integration.

#### Key Methods

##### `rateUser(User rater, User ratee, ShoutoutExchange exchange, Integer rating, String review, UserRating.RatingCategory category)`
- **Purpose:** Rate a user after completing an exchange
- **Validation:** 
  - Rating must be between 1-5
  - Exchange must be completed
  - User cannot rate the same exchange twice
- **Process:**
  1. Validates rating and exchange status
  2. Checks for duplicate ratings
  3. Creates and saves UserRating entity
  4. Updates ratee's average rating
  5. Sends notification to ratee
- **Returns:** UserRating entity
- **Throws:** RuntimeException for validation failures

##### `getUserRatings(User user)`
- **Purpose:** Retrieve all ratings received by a user
- **Returns:** List<UserRating>

##### `getUserAverageRating(User user)`
- **Purpose:** Get user's average rating across all exchanges
- **Returns:** Double (0.0 if no ratings)

##### `getUserRatingCount(User user)`
- **Purpose:** Get total number of ratings received
- **Returns:** Integer (0 if no ratings)

##### `getCategoryAverageRating(User user, UserRating.RatingCategory category)`
- **Purpose:** Get average rating for specific category (e.g., RESPONSIVENESS, QUALITY)
- **Returns:** Double (0.0 if no category ratings)

##### `updateUserAverageRating(User user)`
- **Purpose:** Recalculate and update user's average rating
- **Transactional:** Yes
- **Process:**
  1. Fetches average rating from repository
  2. Fetches total rating count
  3. Updates user entity with calculated values
  4. Logs the update

##### `canRateExchange(User user, ShoutoutExchange exchange)`
- **Purpose:** Check if user is eligible to rate an exchange
- **Validation Checks:**
  1. User is participant (requester or acceptor)
  2. Exchange status is COMPLETED
  3. User hasn't already rated this exchange
- **Returns:** Boolean

#### Dependencies
- `UserRatingRepository` - Data access for ratings
- `ShoutoutExchangeRepository` - Exchange data access
- `NotificationService` - Send rating notifications

#### Annotations Used
- `@Service` - Spring service component
- `@RequiredArgsConstructor` - Constructor injection via Lombok
- `@Slf4j` - SLF4J logging
- `@Transactional` - Database transaction management

---

### 2. **ExchangeController.java** ✅
**Location:** `src/main/java/com/shout/controller/ExchangeController.java`

#### Base URL
`/api/exchanges`

#### Endpoints

##### 1. `GET /api/exchanges/{exchangeId}`
**Purpose:** Get complete exchange details with status and timer information

**Authentication:** Required (JWT Bearer token)

**Authorization:** Only exchange participants can access

**Response:**
```json
{
  "id": 123,
  "requesterUsername": "john_doe",
  "requesterName": "John Doe",
  "requesterProfilePic": "url",
  "acceptorUsername": "jane_smith",
  "acceptorName": "Jane Smith",
  "acceptorProfilePic": "url",
  "status": "COMPLETE|INCOMPLETE",
  "timeStatus": "LIVE|EXPIRED",
  "requesterPosted": true|false,
  "acceptorPosted": true|false,
  "requesterPostedAt": "2025-12-20T10:30:00",
  "acceptorPostedAt": "2025-12-20T10:45:00",
  "expiresAt": "2025-12-21T10:30:00",
  "hoursRemaining": 23,
  "minutesRemaining": 45,
  "postUrl": "url",
  "requesterPostUrl": "url",
  "acceptorPostUrl": "url",
  "isPendingFromMe": true|false
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing token
- `403 Forbidden` - Not an exchange participant
- `400 Bad Request` - Exchange not found

---

##### 2. `POST /api/exchanges/{exchangeId}/confirm-repost`
**Purpose:** User confirms they posted their side of the exchange

**Authentication:** Required (JWT Bearer token)

**Request Body:**
```json
{
  "postUrl": "https://instagram.com/p/abc123"
}
```

**Response:**
```json
{
  "message": "Verification scheduled",
  "status": "PENDING_VERIFICATION",
  "exchangeId": 123
}
```

**Process:**
1. Validates JWT token
2. Identifies which party (requester/acceptor) is posting
3. Marks their side as posted with URL
4. Sets posted timestamp
5. Returns verification status

**Error Responses:**
- `401 Unauthorized` - Invalid token
- `403 Forbidden` - Not an exchange participant
- `400 Bad Request` - Exchange not found

---

##### 3. `POST /api/exchanges/{exchangeId}/rate`
**Purpose:** Rate the other user after exchange completion

**Authentication:** Required (JWT Bearer token)

**Request Body:**
```json
{
  "rating": 5,
  "ratedUserId": 456,
  "review": "Great collaboration!" // Optional
}
```

**Response:**
```json
{
  "message": "Rating submitted successfully",
  "rating": 5
}
```

**Validation:**
- Rating must be between 1-5
- Exchange must be COMPLETED status
- Cannot rate non-completed exchanges

**Error Responses:**
- `401 Unauthorized` - Invalid token
- `400 Bad Request` - Invalid rating or exchange status

---

##### 4. `GET /api/exchanges/user/active`
**Purpose:** Get all active/pending exchanges for the current user

**Authentication:** Required (JWT Bearer token)

**Response:**
```json
[
  {
    "id": 123,
    "requesterUsername": "john_doe",
    "status": "INCOMPLETE",
    "timeStatus": "LIVE",
    "isPendingFromMe": true,
    // ... other exchange details
  },
  {
    "id": 124,
    "acceptorUsername": "jane_smith",
    "status": "PENDING_VERIFICATION",
    "timeStatus": "LIVE",
    "isPendingFromMe": false,
    // ... other exchange details
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Invalid token
- `400 Bad Request` - User not found

---

#### Helper Methods

##### `buildExchangeResponse(ShoutoutExchange exchange, Long currentUserId)`
- Constructs complete exchange response object
- Includes all status information and timer calculations
- Determines if action is pending from current user

##### `determineStatus(ShoutoutExchange exchange)`
- Returns: "COMPLETE" if both parties posted, "INCOMPLETE" otherwise
- Used for UI status display

##### `determineTimeStatus(ShoutoutExchange exchange)`
- Returns: "EXPIRED" if deadline passed, "LIVE" otherwise
- Critical for deadline enforcement

---

#### Security Features

✅ **Authentication**
- JWT Bearer token validation on all endpoints
- Token extraction from Authorization header

✅ **Authorization**
- Only exchange participants can view exchange details
- Only participants can confirm repost
- Only participants can rate

✅ **CORS**
- Enabled with `@CrossOrigin(origins = "*", maxAge = 3600)`
- 1-hour cache for preflight requests

---

#### Timer System

**24-Hour Deadline Management:**
```
Exchange Created → +24 hours → Deadline
                     ↓
                  LIVE (can post)
                     ↓
                  EXPIRED (cannot post)
```

**Time Remaining Calculation:**
- Hours remaining: `Duration.between(now, expiresAt).toHours()`
- Minutes remaining: `Duration.between(now, expiresAt).toMinutes() % 60`

---

#### Status Flow

```
PENDING
   ↓
[Requester Posts] → REQUESTER_POSTED
   ↓
[Acceptor Posts] → COMPLETE (both posted)
   ↓
Eligible for Rating
   ↓
RATE (both parties can rate each other)
   ↓
COMPLETED (exchange finished)
```

---

#### Integration Points

**Services Required:**
- `ShoutoutExchangeService` - Exchange business logic
- `ComplianceService` - Compliance and strike system
- `UserService` - User data access
- `NotificationService` - Notification creation
- `JwtTokenProvider` - Token validation

**Repositories Required:**
- `ShoutoutExchangeRepository` - Exchange data access

---

## Models Required

### UserRating
Expected fields:
- `id: Long`
- `rater: User`
- `ratee: User`
- `exchange: ShoutoutExchange`
- `rating: Integer` (1-5)
- `review: String` (optional)
- `category: RatingCategory` (enum: RESPONSIVENESS, QUALITY, COMMUNICATION, etc.)
- `createdAt: LocalDateTime`

### ShoutoutExchange
Expected fields:
- `id: Long`
- `requester: User`
- `acceptor: User`
- `status: ExchangeStatus` (enum: PENDING, ACCEPTED, COMPLETED, etc.)
- `requesterPosted: Boolean`
- `acceptorPosted: Boolean`
- `requesterPostedAt: LocalDateTime`
- `acceptorPostedAt: LocalDateTime`
- `expiresAt: LocalDateTime`
- `postUrl: String`
- `requesterPostUrl: String`
- `acceptorPostUrl: String`

---

## Testing Endpoints

### Get Exchange Details
```bash
curl -X GET "http://localhost:8080/api/exchanges/123" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Confirm Repost
```bash
curl -X POST "http://localhost:8080/api/exchanges/123/confirm-repost" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"postUrl": "https://instagram.com/p/abc123"}'
```

### Rate Exchange
```bash
curl -X POST "http://localhost:8080/api/exchanges/123/rate" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"rating": 5, "ratedUserId": 456}'
```

### Get Active Exchanges
```bash
curl -X GET "http://localhost:8080/api/exchanges/user/active" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Error Handling

All endpoints implement consistent error handling:
```json
{
  "error": "Description of what went wrong"
}
```

Common error messages:
- "Unauthorized" - Missing or invalid JWT token
- "Forbidden" - User not authorized for this action
- "Exchange must be completed before rating" - Cannot rate incomplete exchanges
- "Rating must be between 1 and 5" - Invalid rating value
- "User not found" - User account doesn't exist

---

## Production Considerations

✅ **Transactional Integrity**
- Rating updates wrapped in @Transactional
- Ensures database consistency

✅ **Logging**
- All operations logged via @Slf4j
- Useful for debugging and auditing

✅ **JWT Validation**
- Token validation on every protected endpoint
- Bearer token extraction with proper error handling

✅ **Authorization Checks**
- Prevents unauthorized access to other users' exchanges
- Ensures only participants can rate

---

*All implementations follow Spring Boot best practices and are production-ready.*
