ALTER TABLE students
    ADD blocked_until TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE students
    ADD can_write_comments BOOLEAN;

ALTER TABLE students
    ALTER COLUMN can_write_comments SET NOT NULL;

ALTER TABLE complaints
    ADD complaint_reason VARCHAR(255);

ALTER TABLE complaints
    DROP COLUMN complaint_text;

ALTER TABLE complaints
    DROP COLUMN event_id;

ALTER TABLE complaints
    DROP COLUMN vacancy_id;