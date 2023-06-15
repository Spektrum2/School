-- liquibase formatted sql

-- changeset aboturlov:1
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'faculty'
CREATE TABLE faculty
(
    id    SERIAL PRIMARY KEY,
    name  varchar(255),
    color varchar(255)
)

-- changeset aboturlov:2
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'avatar'
CREATE TABLE avatar
(
    id         SERIAL PRIMARY KEY,
    file_path  VARCHAR(255),
    file_size  BIGINT NOT NULL,
    media_type VARCHAR(255),
    data       BYTEA,
    student_id BIGINT
);

-- changeset aboturlov:3
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'student'
CREATE TABLE student
(
    id         SERIAL PRIMARY KEY,
    name       varchar(255)       NOT NULL,
    age        INTEGER DEFAULT 20 NOT NULL,
    faculty_id BIGINT REFERENCES faculty (id),
    avatar_id  BIGINT REFERENCES avatar (id)
);

-- changeset aboturlov:4
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_indexes WHERE tablename = 'student' AND indexname = 'student_name_index'
CREATE INDEX student_name_index ON student (name);

-- changeset aboturlov:5
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_indexes WHERE tablename = 'faculty' AND indexname = 'faculty_name_color_index'
CREATE INDEX faculty_name_color_index ON faculty (name, color);