ALTER TABLE students
    ADD student_city VARCHAR(255);

DROP TABLE event_comments_liked_by CASCADE;

ALTER TABLE complaints
    DROP COLUMN complaint_text;

ALTER TABLE complaints
    DROP COLUMN event_id;

ALTER TABLE complaints
    DROP COLUMN type;

ALTER TABLE complaints
    DROP COLUMN vacancy_id;

ALTER TABLE event_comments
    DROP COLUMN liked_by;

ALTER TABLE events
    ALTER COLUMN description TYPE VARCHAR USING (description::VARCHAR);

ALTER TABLE events
    ALTER COLUMN name_of_event TYPE VARCHAR USING (name_of_event::VARCHAR);

ALTER TABLE student_certificates
    ALTER COLUMN student_certificates TYPE VARCHAR USING (student_certificates::VARCHAR);

ALTER TABLE student_resumes
    ALTER COLUMN student_resumes TYPE VARCHAR USING (student_resumes::VARCHAR);

ALTER TABLE events
    ALTER COLUMN venue TYPE VARCHAR USING (venue::VARCHAR);