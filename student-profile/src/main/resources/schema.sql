CREATE TABLE IF NOT EXISTS students (
    student_id        INT PRIMARY KEY,
    student_name      VARCHAR(100) NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    course_name       VARCHAR(100) NOT NULL,
    programming_score DOUBLE NOT NULL CHECK (programming_score BETWEEN 0 AND 100),
    mathematics_score DOUBLE NOT NULL CHECK (mathematics_score BETWEEN 0 AND 100)
);

CREATE TABLE IF NOT EXISTS student_skills (
    student_id INT NOT NULL,
    skill      VARCHAR(50) NOT NULL,
    PRIMARY KEY (student_id, skill),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);
