-- Create exchange_ratings table for 1-5 star rating system
CREATE TABLE IF NOT EXISTS exchange_ratings (
    id BIGSERIAL PRIMARY KEY,
    exchange_id BIGINT NOT NULL REFERENCES shoutout_exchanges(id) ON DELETE CASCADE,
    rater_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rated_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_rating_per_exchange UNIQUE(exchange_id, rater_user_id)
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_exchange_ratings_exchange_id ON exchange_ratings(exchange_id);
CREATE INDEX IF NOT EXISTS idx_exchange_ratings_rated_user_id ON exchange_ratings(rated_user_id);
CREATE INDEX IF NOT EXISTS idx_exchange_ratings_rater_user_id ON exchange_ratings(rater_user_id);

-- Add comment for documentation
COMMENT ON TABLE exchange_ratings IS 'Stores 1-5 star ratings given after exchange completion';
COMMENT ON COLUMN exchange_ratings.rating IS 'Rating value between 1 and 5 (integer)';
COMMENT ON COLUMN exchange_ratings.created_at IS 'Timestamp when rating was given';
