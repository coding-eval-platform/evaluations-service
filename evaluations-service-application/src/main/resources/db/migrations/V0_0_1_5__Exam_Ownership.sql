CREATE TABLE exam_owners
(
    exam_id BIGINT,
    owner   VARCHAR,
    FOREIGN KEY (exam_id) REFERENCES exams (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX exam_owners_exam_id ON exam_owners (exam_id);
