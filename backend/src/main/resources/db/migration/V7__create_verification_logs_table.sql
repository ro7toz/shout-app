-- Create verification_logs table for tracking Instagram verification attempts
CREATE TABLE IF NOT EXISTS verification_logs (
    id BIGSERIAL PRIMARY KEY,
    exchange_id BIGINT NOT NULL REFERENCES shoutout_exchanges(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attempt_number INT NOT NULL,
    verification_result VARCHAR(50) NOT NULL,
    api_response_data JSONB,
    checked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) DEFAULT 'SYSTEM'
);

-- Create indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_verification_logs_exchange_id ON verification_logs(exchange_id);
CREATE INDEX IF NOT EXISTS idx_verification_logs_user_id ON verification_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_verification_logs_checked_at ON verification_logs(checked_at DESC);
CREATE INDEX IF NOT EXISTS idx_verification_logs_result ON verification_logs(verification_result);

-- Add comment for documentation
COMMENT ON TABLE verification_logs IS 'Tracks all Instagram media verification attempts for exchanges';
COMMENT ON COLUMN verification_logs.attempt_number IS 'Sequential attempt number for this exchange';
COMMENT ON COLUMN verification_logs.verification_result IS 'Result status: SUCCESS, FAILED, PENDING, EXPIRED, WRONG_MEDIA_TYPE, ERROR';
COMMENT ON COLUMN verification_logs.api_response_data IS 'Stores full Instagram API response for debugging';
