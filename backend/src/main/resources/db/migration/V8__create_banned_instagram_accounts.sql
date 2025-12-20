-- Create banned_instagram_accounts table for permanent ID blacklisting
CREATE TABLE IF NOT EXISTS banned_instagram_accounts (
    id BIGSERIAL PRIMARY KEY,
    instagram_id VARCHAR(255) NOT NULL UNIQUE,
    banned_reason VARCHAR(500),
    banned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    banned_by_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    notes TEXT
);

-- Create index for fast lookup when registering new accounts
CREATE INDEX IF NOT EXISTS idx_banned_instagram_accounts_instagram_id ON banned_instagram_accounts(instagram_id);
CREATE INDEX IF NOT EXISTS idx_banned_instagram_accounts_banned_at ON banned_instagram_accounts(banned_at DESC);

-- Add comment for documentation
COMMENT ON TABLE banned_instagram_accounts IS 'Permanently blacklists Instagram IDs of banned users (3-strike rule)';
COMMENT ON COLUMN banned_instagram_accounts.instagram_id IS 'Instagram account ID to blacklist';
COMMENT ON COLUMN banned_instagram_accounts.banned_reason IS 'Reason for ban (e.g., "3 STRIKES - PERMANENT BAN")';
