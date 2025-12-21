-- V2__add_missing_columns.sql
-- Location: src/main/resources/db/migration/V2__add_missing_columns.sql
-- Adds missing columns and fixes for production readiness

-- Add missing columns to users table if they don't exist
DO $$
BEGIN
    -- Add followers column if missing
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='follower_count') THEN
        ALTER TABLE users ADD COLUMN follower_count INTEGER DEFAULT 0;
    END IF;

    -- Add fullName alias support
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='full_name') THEN
        ALTER TABLE users ADD COLUMN full_name VARCHAR(255);
        UPDATE users SET full_name = name WHERE full_name IS NULL;
    END IF;

    -- Add access token fields
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='access_token') THEN
        ALTER TABLE users ADD COLUMN access_token TEXT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='token_expires_at') THEN
        ALTER TABLE users ADD COLUMN token_expires_at TIMESTAMP;
    END IF;
END $$;

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_follower_count ON users(follower_count);
CREATE INDEX IF NOT EXISTS idx_users_category_followers ON users(category, follower_count) WHERE is_active = TRUE;
CREATE INDEX IF NOT EXISTS idx_users_strikes ON users(strike_count) WHERE account_banned = FALSE;

-- Add reminder tracking to exchanges
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='shoutout_exchanges' AND column_name='reminder_sent') THEN
        ALTER TABLE shoutout_exchanges ADD COLUMN reminder_sent BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers for updated_at
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_exchanges_updated_at ON shoutout_exchanges;
CREATE TRIGGER update_exchanges_updated_at BEFORE UPDATE ON shoutout_exchanges
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Verify migration
DO $$
BEGIN
    RAISE NOTICE '✅ Migration V2 completed successfully';
END $$;
