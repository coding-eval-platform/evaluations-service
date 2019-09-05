CREATE TABLE exam_solution_submission
(
    id        BIGSERIAL PRIMARY KEY NOT NULL,
    exam_id   BIGINT,
    submitter VARCHAR,
    state     VARCHAR,
    FOREIGN KEY (exam_id) REFERENCES exams (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE UNIQUE INDEX exam_solution_submission_exam_id_submitter_unique_index
    ON exam_solution_submission (exam_id, submitter);

-- Add the submission_id to the exercise_solutions table, and remove all rows
-- Fix the exercise_solutions table to match the new model.
-- This includes:
--      - Change the belongs_to column to exercise_id.
--      - Add the submission_id column.
--      - Remove all existing rows.
--      - Make the submission_id reference the exam_solution_submission table.
--      - Add an unique index for the <exercise_id, submission_id> tuple.
ALTER TABLE exercise_solutions
    RENAME COLUMN belongs_to TO exercise_id;
ALTER TABLE exercise_solutions
    ADD COLUMN submission_id BIGINT;
DELETE
FROM exercise_solutions
WHERE submission_id is NULL;
ALTER TABLE exercise_solutions
    ADD FOREIGN KEY (submission_id) REFERENCES exam_solution_submission (id) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE UNIQUE INDEX exercise_solutions_exercise_id_submission_id_unique_index
    ON exercise_solutions (exercise_id, submission_id);
