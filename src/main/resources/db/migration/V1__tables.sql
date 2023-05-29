CREATE TABLE IF NOT EXISTS events
(
    event_id         BIGSERIAL,
    date_of_creation TIMESTAMP(6),
    date_of_event    VARCHAR(255) NOT NULL,
    description      VARCHAR(255) NOT NULL,
    event_photo      BYTEA        NOT NULL,
    favourite_count  INTEGER DEFAULT 0,
    name_of_event    VARCHAR(255) NOT NULL,
    venue            VARCHAR(255) NOT NULL,
    PRIMARY KEY (event_id)
);

CREATE TABLE IF NOT EXISTS favourite_events
(
    id         BIGSERIAL,
    student_id BIGINT NOT NULL,
    event_id   BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_favourite_events_event_id
    FOREIGN KEY (event_id) REFERENCES events(event_id)
);

CREATE TABLE IF NOT EXISTS universities
(
    id     BIGSERIAL,
    domain VARCHAR(255) NOT NULL,
    name   VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS students
(
    id                     BIGSERIAL,
    birth_date             VARCHAR(255),
    course                 VARCHAR(255),
    email                  VARCHAR(255),
    enabled                BOOLEAN NOT NULL,
    first_name             VARCHAR(255),
    has_new_messages       BOOLEAN,
    last_name              VARCHAR(255),
    major                  VARCHAR(255),
    password               VARCHAR(255),
    student_photo          BYTEA,
    student_photo_filename VARCHAR(255),
    role                   VARCHAR(255) NOT NULL,
    university_id          BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_students_email
    UNIQUE (email),
    CONSTRAINT fk_students_university_id
    FOREIGN KEY (university_id) REFERENCES universities(id)
);

CREATE TABLE IF NOT EXISTS event_comments
(
    id           BIGSERIAL,
    comment_text VARCHAR(255) NOT NULL,
    event_id     BIGINT       NOT NULL,
    student_id   BIGINT,
    comments_id  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_event_comments_event_id
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT fk_event_comments_student_id
    FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_event_comments_comments_id
    FOREIGN KEY (comments_id) REFERENCES events(event_id)
);

CREATE TABLE IF NOT EXISTS messages
(
    id              BIGSERIAL,
    message_content VARCHAR(255),
    time_sent       TIMESTAMP(6),
    student_id      BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_messages_student_id
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS replies
(
    id         BIGSERIAL,
    reply_text VARCHAR(255) NOT NULL,
    comment_id BIGINT,
    student_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_replies_comment_id
    FOREIGN KEY (comment_id) REFERENCES event_comments(id),
    CONSTRAINT fk_replies_student_id
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS student_certificates
(
    student_id           BIGINT NOT NULL,
    student_certificates BYTEA,
    CONSTRAINT fk_student_certificates_student_id
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS student_certificates_filenames
(
    student_id                     BIGINT NOT NULL,
    student_certificates_filenames VARCHAR(255),
    CONSTRAINT fk_student_certificates_filenames_student_id
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS student_resume_filenames
(
    student_id               BIGINT NOT NULL,
    student_resume_filenames VARCHAR(255),
    CONSTRAINT fk_student_resume_filenames_student_id
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS student_resumes
(
    student_id      BIGINT NOT NULL,
    student_resumes BYTEA,
    CONSTRAINT fk_student_resumes_student_id
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS user_sessions
(
    id         BIGSERIAL,
    session_id VARCHAR(255),
    user_id    VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vacancies
(
    vacancy_id      BIGSERIAL,
    description     VARCHAR(255) NOT NULL,
    name_of_vacancy VARCHAR(255) NOT NULL,
    PRIMARY KEY (vacancy_id)
);

CREATE TABLE IF NOT EXISTS saved_vacancies
(
    id         BIGSERIAL,
    student_id BIGINT NOT NULL,
    vacancy_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_saved_vacancies_vacancy_id
    FOREIGN KEY (vacancy_id) REFERENCES vacancies(vacancy_id)
);

CREATE TABLE IF NOT EXISTS verification_codes
(
    id              BIGSERIAL,
    code            INTEGER NOT NULL,
    student_email   VARCHAR(255),
    expiration_date TIMESTAMP(6),
    student_id      BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS saved_vacancy
(
    id         BIGSERIAL,
    student_id BIGINT NOT NULL,
    vacancy_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_saved_vacancy_vacancy_id
    FOREIGN KEY (vacancy_id) REFERENCES vacancies(vacancy_id)
);