-- V1__Initial_schema.sql
-- Location: src/main/resources/db/migration/V1__Initial_schema.sql
-- CRITICAL: This file was MISSING and is required for Flyway

-- ===== USERS TABLE =====
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE,
    profile_picture VARCHAR(500),
    bio TEXT,
    
    -- Instagram Integration
    instagram_id VARCHAR(255),
    instagram_username VARCHAR(255),
    instagram_access_token TEXT,
    
    -- Facebook Integration
    facebook_id VARCHAR(255) UNIQUE,
    facebook_access_token TEXT,
    facebook_token_expires_at BIGINT,
    
    -- Account Details
    account_type VARCHAR(50) DEFAULT 'Creator',
    plan_type VARCHAR(50) DEFAULT 'BASIC',
    is_verified BOOLEAN DEFAULT false,
    follower_count INTEGER DEFAULT 0,
    category VARCHAR(100),
    
    -- Ratings
    rating DOUBLE PRECISION DEFAULT 0.0,
    total_ratings INTEGER DEFAULT 0,
    
    -- Compliance
    strike_count INTEGER DEFAULT 0,
    account_banned BOOLEAN DEFAULT false,
    social_login_banned BOOLEAN DEFAULT false,
    banned_at TIMESTAMP,
    
    -- Daily Limits
    daily_requests_sent INTEGER DEFAULT 0,
    daily_requests_accepted INTEGER DEFAULT 0,
    
    -- Subscription
    subscription_start_date TIMESTAMP,
    subscription_end_date TIMESTAMP,
    
    -- Status
    is_active BOOLEAN DEFAULT true,
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== USER MEDIA TABLE =====
CREATE TABLE IF NOT EXISTS user_media (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    url VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    source VARCHAR(50) DEFAULT 'instagram',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== SHOUTOUT REQUESTS TABLE =====
CREATE TABLE IF NOT EXISTS shoutout_requests (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_link VARCHAR(500),
    media_type VARCHAR(50) DEFAULT 'STORY' NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP,
    completed_at TIMESTAMP,
    requester_posted BOOLEAN DEFAULT false,
    requester_posted_at TIMESTAMP,
    target_posted BOOLEAN DEFAULT false,
    target_posted_at TIMESTAMP,
    is_expired BOOLEAN DEFAULT false,
    is_notified BOOLEAN DEFAULT false
);

-- ===== SHOUTOUT EXCHANGES TABLE =====
CREATE TABLE IF NOT EXISTS shoutout_exchanges (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES users(id),
    acceptor_id BIGINT NOT NULL REFERENCES users(id),
    request_id BIGINT NOT NULL REFERENCES shoutout_requests(id),
    
    post_url VARCHAR(500),
    post_caption TEXT,
    hashtags TEXT,
    media_type VARCHAR(50) NOT NULL,
    
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    
    requester_posted BOOLEAN DEFAULT false,
    requester_posted_at TIMESTAMP,
    requester_post_url VARCHAR(500),
    requester_removed BOOLEAN DEFAULT false,
    requester_removed_at TIMESTAMP,
    
    acceptor_posted BOOLEAN DEFAULT false,
    acceptor_posted_at TIMESTAMP,
    acceptor_post_url VARCHAR(500),
    acceptor_removed BOOLEAN DEFAULT false,
    acceptor_removed_at TIMESTAMP,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== NOTIFICATIONS TABLE =====
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    related_user_id BIGINT,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== SUBSCRIPTION PLANS TABLE =====
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    plan_type VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    monthly_price DECIMAL(10,2) NOT NULL,
    yearly_price DECIMAL(10,2) NOT NULL,
    original_yearly_price DECIMAL(10,2) NOT NULL,
    
    stories_supported BOOLEAN DEFAULT true,
    posts_supported BOOLEAN DEFAULT false,
    reels_supported BOOLEAN DEFAULT false,
    analytics_supported BOOLEAN DEFAULT false,
    advanced_analytics_supported BOOLEAN DEFAULT false,
    
    max_active_requests INTEGER DEFAULT 10,
    active BOOLEAN DEFAULT true
);

-- ===== USER SUBSCRIPTIONS TABLE =====
CREATE TABLE IF NOT EXISTS user_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    billing_cycle VARCHAR(50) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    renewal_date TIMESTAMP NOT NULL,
    auto_renew BOOLEAN DEFAULT true,
    status VARCHAR(50) NOT NULL,
    cancelled_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== PAYMENTS TABLE =====
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    subscription_id BIGINT REFERENCES user_subscriptions(id),
    transaction_id VARCHAR(255) UNIQUE NOT NULL,
    gateway VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    status VARCHAR(50) NOT NULL,
    gateway_response TEXT,
    receipt VARCHAR(255),
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== POST ANALYTICS TABLE =====
CREATE TABLE IF NOT EXISTS post_analytics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    shoutout_exchange_id BIGINT NOT NULL REFERENCES shoutout_exchanges(id),
    instagram_post_id VARCHAR(255),
    instagram_media_url VARCHAR(500),
    media_type VARCHAR(50) NOT NULL,
    
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    profile_visits BIGINT DEFAULT 0,
    shares BIGINT DEFAULT 0,
    saves BIGINT DEFAULT 0,
    comments BIGINT DEFAULT 0,
    likes BIGINT DEFAULT 0,
    engagement_rate DOUBLE PRECISION DEFAULT 0.0,
    
    analytics_verified BOOLEAN DEFAULT false,
    last_fetched_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== USER RATINGS TABLE =====
CREATE TABLE IF NOT EXISTS user_ratings (
    id BIGSERIAL PRIMARY KEY,
    rater_id BIGINT NOT NULL REFERENCES users(id),
    ratee_id BIGINT NOT NULL REFERENCES users(id),
    exchange_id BIGINT NOT NULL REFERENCES shoutout_exchanges(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(rater_id, ratee_id, exchange_id)
);

-- ===== COMPLIANCE RECORDS TABLE =====
CREATE TABLE IF NOT EXISTS compliance_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    violation_type VARCHAR(100) NOT NULL,
    exchange_id BIGINT NOT NULL REFERENCES shoutout_exchanges(id),
    strike_number INTEGER NOT NULL,
    description TEXT,
    account_banned BOOLEAN DEFAULT false,
    banned_at TIMESTAMP,
    social_login_banned BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== INDEXES FOR PERFORMANCE =====
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_instagram_id ON users(instagram_id);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_plan_type ON users(plan_type);
CREATE INDEX idx_users_follower_count ON users(follower_count);
CREATE INDEX idx_users_strikes ON users(strike_count);

CREATE INDEX idx_requests_status ON shoutout_requests(status);
CREATE INDEX idx_requests_requester ON shoutout_requests(requester_id);
CREATE INDEX idx_requests_target ON shoutout_requests(target_id);
CREATE INDEX idx_requests_media_type ON shoutout_requests(media_type);

CREATE INDEX idx_exchanges_status ON shoutout_exchanges(status);
CREATE INDEX idx_exchanges_expires_at ON shoutout_exchanges(expires_at);
CREATE INDEX idx_exchanges_requester ON shoutout_exchanges(requester_id);
CREATE INDEX idx_exchanges_acceptor ON shoutout_exchanges(acceptor_id);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);

-- ===== INSERT DEFAULT SUBSCRIPTION PLANS =====
INSERT INTO subscription_plans (plan_type, name, description, monthly_price, yearly_price, original_yearly_price,
    stories_supported, posts_supported, reels_supported, analytics_supported, advanced_analytics_supported, max_active_requests, active)
VALUES 
    ('BASIC', 'Basic', 'Free plan - Stories only, no analytics', 0, 0, 0, true, false, false, false, false, 10, true),
    ('PRO', 'Pro', 'Professional plan - Posts, Reels, Advanced Analytics', 499, 4999, 5988, true, true, true, true, true, 50, true)
ON CONFLICT (plan_type) DO NOTHING;
