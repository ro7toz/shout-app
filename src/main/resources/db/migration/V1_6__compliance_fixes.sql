-- ===== DATABASE MIGRATION V1.6: COMPLIANCE & FILTERING SUPPORT =====
-- This migration adds support for:
-- 1. Media type enforcement (STORY/POST/REEL)
-- 2. Compliance strikes and account bans
-- 3. Optimized filtering by follower count and category

-- ===== 1. Add media_type column to shoutout_requests =====
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='shoutout_requests' AND column_name='media_type'
    ) THEN
        ALTER TABLE shoutout_requests
        ADD COLUMN media_type VARCHAR(50) DEFAULT 'STORY' NOT NULL;
        RAISE NOTICE 'Added media_type column to shoutout_requests';
    END IF;
END $$;

-- ===== 2. Add compliance columns to users table =====
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='strike_count'
    ) THEN
        ALTER TABLE users
        ADD COLUMN strike_count INTEGER DEFAULT 0 NOT NULL;
        RAISE NOTICE 'Added strike_count column to users';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='account_banned'
    ) THEN
        ALTER TABLE users
        ADD COLUMN account_banned BOOLEAN DEFAULT false NOT NULL;
        RAISE NOTICE 'Added account_banned column to users';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='social_login_banned'
    ) THEN
        ALTER TABLE users
        ADD COLUMN social_login_banned BOOLEAN DEFAULT false NOT NULL;
        RAISE NOTICE 'Added social_login_banned column to users';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='banned_at'
    ) THEN
        ALTER TABLE users
        ADD COLUMN banned_at TIMESTAMP;
        RAISE NOTICE 'Added banned_at column to users';
    END IF;
END $$;

-- ===== 3. Create indexes for media type filtering =====
CREATE INDEX IF NOT EXISTS idx_request_media_type 
    ON shoutout_requests(media_type);

-- ===== 4. Create indexes for follower count filtering =====
CREATE INDEX IF NOT EXISTS idx_user_follower_count 
    ON users(follower_count);

CREATE INDEX IF NOT EXISTS idx_user_follower_range 
    ON users(follower_count, is_active);

-- ===== 5. Create combined indexes for optimized discovery =====
CREATE INDEX IF NOT EXISTS idx_user_category_followers 
    ON users(category, follower_count, is_active);

-- ===== 6. Create indexes for compliance queries =====
CREATE INDEX IF NOT EXISTS idx_user_banned 
    ON users(account_banned, is_active);

CREATE INDEX IF NOT EXISTS idx_user_strikes 
    ON users(strike_count);

-- ===== 7. Create index for exchange expiry (scheduled task) =====
CREATE INDEX IF NOT EXISTS idx_exchange_expiry 
    ON shoutout_exchanges(status, expires_at);

-- ===== 8. Create indexes for subscription queries =====
CREATE INDEX IF NOT EXISTS idx_subscription_user 
    ON user_subscriptions(user_id);

CREATE INDEX IF NOT EXISTS idx_subscription_status 
    ON user_subscriptions(status, renewal_date);

-- ===== 9. Create indexes for compliance records =====
CREATE INDEX IF NOT EXISTS idx_compliance_user 
    ON compliance_records(user_id);

CREATE INDEX IF NOT EXISTS idx_compliance_banned 
    ON compliance_records(account_banned);

-- ===== 10. Data integrity updates =====
-- Ensure all existing records have valid media_type
UPDATE shoutout_requests SET media_type = 'STORY' WHERE media_type IS NULL;

-- Initialize compliance fields for all users
UPDATE users SET strike_count = 0 WHERE strike_count IS NULL;
UPDATE users SET account_banned = false WHERE account_banned IS NULL;
UPDATE users SET social_login_banned = false WHERE social_login_banned IS NULL;

-- ===== 11. Verify migration =====
DO $$
DECLARE
    v_media_type_count INTEGER;
    v_strike_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_media_type_count FROM information_schema.columns
    WHERE table_name='shoutout_requests' AND column_name='media_type';
    
    SELECT COUNT(*) INTO v_strike_count FROM information_schema.columns
    WHERE table_name='users' AND column_name='strike_count';
    
    IF v_media_type_count > 0 AND v_strike_count > 0 THEN
        RAISE NOTICE '✅ Migration V1.6 completed successfully';
    ELSE
        RAISE EXCEPTION '❌ Migration V1.6 failed';
    END IF;
END $$;
