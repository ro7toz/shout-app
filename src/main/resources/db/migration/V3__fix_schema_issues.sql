ALTER TABLE users ALTER COLUMN follower_count SET DEFAULT 0;
ALTER TABLE users ALTER COLUMN strike_count SET DEFAULT 0;
ALTER TABLE users ALTER COLUMN account_banned SET DEFAULT false;
ALTER TABLE users ALTER COLUMN is_active SET DEFAULT true;
ALTER TABLE users ALTER COLUMN plan_type SET DEFAULT 'BASIC';

-- Ensure timestamps are set
ALTER TABLE users ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_instagram_id ON users(instagram_id);
CREATE INDEX IF NOT EXISTS idx_users_plan_type ON users(plan_type);

-- Ensure shoutout_requests has proper structure
ALTER TABLE shoutout_requests 
    ALTER COLUMN status SET DEFAULT 'PENDING',
    ALTER COLUMN media_type SET DEFAULT 'STORY',
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

-- Add missing indexes
CREATE INDEX IF NOT EXISTS idx_requests_status ON shoutout_requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_created_at ON shoutout_requests(created_at);

-- Ensure exchanges table structure
ALTER TABLE shoutout_exchanges 
    ALTER COLUMN status SET DEFAULT 'PENDING',
    ALTER COLUMN requester_posted SET DEFAULT false,
    ALTER COLUMN acceptor_posted SET DEFAULT false;
