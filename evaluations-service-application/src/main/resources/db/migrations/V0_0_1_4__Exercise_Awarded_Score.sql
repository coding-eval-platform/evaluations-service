ALTER TABLE exercises
    ADD COLUMN awarded_score INT;

-- Set a value to all exercises that already exist
UPDATE exercises
SET awarded_score = 1
WHERE awarded_score IS NULL;
