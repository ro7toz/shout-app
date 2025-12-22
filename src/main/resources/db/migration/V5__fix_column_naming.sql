-- Fix column naming inconsistencies
DO $$
BEGIN
    -- Ensure follower_count exists (not followers)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='users' AND column_name='followers') THEN
        ALTER TABLE users RENAME COLUMN followers TO follower_count;
    END IF;
    
    -- Ensure strike_count exists (not strikes)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='users' AND column_name='strikes') THEN
        ALTER TABLE users RENAME COLUMN strikes TO strike_count;
    END IF;
    
    -- Add missing columns if not exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='follower_count') THEN
        ALTER TABLE users ADD COLUMN follower_count INTEGER DEFAULT 0;
    END IF;
END $$;

-- Fix foreign key references in shoutout_requests
ALTER TABLE shoutout_requests 
    DROP CONSTRAINT IF EXISTS shoutout_requests_requester_id_fkey,
    DROP CONSTRAINT IF EXISTS shoutout_requests_target_id_fkey;

ALTER TABLE shoutout_requests 
    ADD CONSTRAINT shoutout_requests_requester_id_fkey 
        FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT shoutout_requests_target_id_fkey 
        FOREIGN KEY (target_id) REFERENCES users(id) ON DELETE CASCADE;

-- Ensure all critical indexes exist
CREATE INDEX IF NOT EXISTS idx_users_instagram_username ON users(instagram_username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_exchanges_status_expiry ON shoutout_exchanges(status, expires_at);

-- Verify migration
DO $$
BEGIN
    RAISE NOTICE '✅ Migration V5 completed successfully';
END $$;
