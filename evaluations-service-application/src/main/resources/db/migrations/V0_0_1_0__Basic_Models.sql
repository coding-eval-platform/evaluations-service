DROP SCHEMA IF EXISTS models;
CREATE SCHEMA models;

CREATE TABLE models.exams
(
    id                     BIGSERIAL PRIMARY KEY NOT NULL,
    description            VARCHAR(64),
    starting_at            TIMESTAMP WITHOUT TIME ZONE,
    duration               BIGINT,
    state                  VARCHAR,
    actual_starting_moment TIMESTAMP,
    actual_duration        BIGINT
);

CREATE TABLE models.exercises
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    question   TEXT,
    belongs_to BIGINT,
    FOREIGN KEY (belongs_to) REFERENCES models.exams (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE models.test_cases
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    visibility VARCHAR,
    belongs_to BIGINT,
    FOREIGN KEY (belongs_to) REFERENCES models.exercises (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE models.test_case_inputs
(
    test_case_id BIGINT,
    input        VARCHAR,
    input_order  INT,
    FOREIGN KEY (test_case_id) REFERENCES models.test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE models.test_case_expected_outputs
(
    test_case_id          BIGINT,
    expected_output       VARCHAR,
    expected_output_order INT,
    FOREIGN KEY (test_case_id) REFERENCES models.test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE models.exercise_solutions
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    answer     TEXT,
    belongs_to BIGINT,
    FOREIGN KEY (belongs_to) REFERENCES models.exercises (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE models.exercise_solution_results
(
    id           BIGSERIAL PRIMARY KEY NOT NULL,
    solution_id  BIGINT,
    test_case_id BIGINT,
    result       VARCHAR,
    FOREIGN KEY (solution_id) REFERENCES models.exercise_solutions (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (test_case_id) REFERENCES models.test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE
);
