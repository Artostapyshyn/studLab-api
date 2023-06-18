ALTER TABLE events
    ALTER COLUMN description TYPE VARCHAR(255) USING (description::VARCHAR(255));

ALTER TABLE students
    ADD COLUMN registration_date TIMESTAMP;
