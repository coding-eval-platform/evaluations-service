CREATE TABLE exams
(
    id                     BIGSERIAL PRIMARY KEY NOT NULL,
    description            VARCHAR(64),
    starting_at            TIMESTAMP WITHOUT TIME ZONE,
    duration               BIGINT,
    state                  VARCHAR,
    actual_starting_moment TIMESTAMP,
    actual_duration        BIGINT
);

CREATE TABLE exercises
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    question   TEXT,
    belongs_to BIGINT,
    FOREIGN KEY (belongs_to) REFERENCES exams (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE test_cases
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    visibility VARCHAR,
    belongs_to BIGINT,
    FOREIGN KEY (belongs_to) REFERENCES exercises (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE test_case_inputs
(
    test_case_id BIGINT,
    input        VARCHAR,
    input_order  INT,
    FOREIGN KEY (test_case_id) REFERENCES test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE test_case_expected_outputs
(
    test_case_id          BIGINT,
    expected_output       VARCHAR,
    expected_output_order INT,
    FOREIGN KEY (test_case_id) REFERENCES test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE exercise_solutions
(
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    answer     TEXT,
    belongs_to BIGINT,
    FOREIGN KEY (belongs_to) REFERENCES exercises (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE exercise_solution_results
(
    id           BIGSERIAL PRIMARY KEY NOT NULL,
    solution_id  BIGINT,
    test_case_id BIGINT,
    result       VARCHAR,
    FOREIGN KEY (solution_id) REFERENCES exercise_solutions (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (test_case_id) REFERENCES test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE
);
