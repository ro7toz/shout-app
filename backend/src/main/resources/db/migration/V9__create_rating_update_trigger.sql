-- Create function to auto-update user's average rating when a new rating is added
CREATE OR REPLACE FUNCTION update_user_average_rating()
RETURNS TRIGGER AS $$
BEGIN
    -- Update the average_rating in users table based on all ratings for this user
    UPDATE users
    SET average_rating = (
        SELECT COALESCE(AVG(rating), 0)
        FROM exchange_ratings
        WHERE rated_user_id = NEW.rated_user_id
    )
    WHERE id = NEW.rated_user_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger that fires after a rating is inserted
DROP TRIGGER IF EXISTS rating_inserted ON exchange_ratings;
CREATE TRIGGER rating_inserted
AFTER INSERT ON exchange_ratings
FOR EACH ROW
EXECUTE FUNCTION update_user_average_rating();

-- Create trigger that fires after a rating is updated
DROP TRIGGER IF EXISTS rating_updated ON exchange_ratings;
CREATE TRIGGER rating_updated
AFTER UPDATE ON exchange_ratings
FOR EACH ROW
EXECUTE FUNCTION update_user_average_rating();

-- Add comment for documentation
COMMENT ON FUNCTION update_user_average_rating() IS 'Automatically updates users.average_rating when ratings are inserted or updated';
