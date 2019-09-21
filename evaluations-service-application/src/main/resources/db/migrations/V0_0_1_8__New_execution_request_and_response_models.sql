ALTER TABLE test_case_inputs
    RENAME TO test_case_program_arguments;
ALTER TABLE test_case_program_arguments
    RENAME COLUMN input TO program_argument;
ALTER TABLE test_case_program_arguments
    RENAME COLUMN input_order TO program_argument_order;

CREATE TABLE test_case_stdin
(
    test_case_id BIGINT,
    input        VARCHAR,
    input_order  INT,
    FOREIGN KEY (test_case_id) REFERENCES test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE exercise_solutions
    ADD COLUMN compiler_flags VARCHAR;
