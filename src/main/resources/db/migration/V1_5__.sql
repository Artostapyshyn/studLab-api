CREATE TABLE IF NOT EXISTS student_friendships
(
    id         BIGSERIAL,
    friend_id  BIGINT,
    student_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT student_friend_unique
        UNIQUE (student_id, friend_id),
    CONSTRAINT friend_id_fk
        FOREIGN KEY (friend_id) REFERENCES STUDENTS,
    CONSTRAINT student_id_fk
        FOREIGN KEY (student_id) REFERENCES STUDENTS
);

CREATE TABLE IF NOT EXISTS friend_requests
(
    id          BIGSERIAL,
    status      VARCHAR(255),
    receiver_id BIGINT,
    sender_id   BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT receiver_id_fk
        FOREIGN KEY (receiver_id) REFERENCES STUDENTS,
    CONSTRAINT sender_id_fk
        FOREIGN KEY (sender_id) REFERENCES STUDENTS
);
