ALTER TABLE students
    ADD blocked_until TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE students
    ADD can_write_comments BOOLEAN;

ALTER TABLE students
    ALTER COLUMN can_write_comments SET NOT NULL;

ALTER TABLE complaints
    ADD complaint_reason VARCHAR(255);
