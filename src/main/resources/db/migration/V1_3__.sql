ALTER TABLE students
    ADD student_city VARCHAR(255);

ALTER TABLE events
    ALTER COLUMN description TYPE VARCHAR USING (description::VARCHAR);

ALTER TABLE events
    ALTER COLUMN name_of_event TYPE VARCHAR USING (name_of_event::VARCHAR);

ALTER TABLE events
    ALTER COLUMN venue TYPE VARCHAR USING (venue::VARCHAR);