CREATE TABLE exam_solution_submission
(
    id        BIGSERIAL PRIMARY KEY NOT NULL,
    exam_id   BIGINT,
    submitter VARCHAR,
    FOREIGN KEY (exam_id) REFERENCES exams (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE UNIQUE INDEX exam_solution_submission_exam_id_submitter_unique_index
    ON exam_solution_submission (exam_id, submitter);
