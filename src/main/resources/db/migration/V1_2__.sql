CREATE TABLE IF NOT EXISTS courses
(
    course_id          bigserial,
    course_description varchar(255) not null,
    course_link        varchar(255) not null,
    name_of_course     varchar(255) not null,
    course_photo       bytea        not null,
    date_of_creation   timestamp(6),
    primary key (course_id)
);

ALTER TABLE students
    DROP COLUMN can_write_comments;

ALTER TABLE complaints
    DROP COLUMN type;

ALTER TABLE complaints
    ALTER COLUMN comment_id SET NOT NULL;

ALTER TABLE complaints
    ALTER COLUMN complaint_reason SET NOT NULL;

ALTER TABLE complaints
    ALTER COLUMN student_id SET NOT NULL;