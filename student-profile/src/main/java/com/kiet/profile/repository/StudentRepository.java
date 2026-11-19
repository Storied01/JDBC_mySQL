package com.kiet.profile.repository;

import com.kiet.profile.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class StudentRepository {

    private static final String SELECT_STUDENTS =
            "SELECT student_id, student_name, email, course_name, programming_score, mathematics_score FROM students";

    private final JdbcTemplate jdbc;

    private final RowMapper<Student> mapper = (rs, rowNum) -> {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setStudentName(rs.getString("student_name"));
        s.setEmail(rs.getString("email"));
        s.setCourseName(rs.getString("course_name"));
        s.setProgrammingScore(rs.getDouble("programming_score"));
        s.setMathematicsScore(rs.getDouble("mathematics_score"));
        return s;
    };

    public StudentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsById(int studentId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM students WHERE student_id = ?", Integer.class, studentId);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM students WHERE LOWER(email) = LOWER(?)", Integer.class, email);
        return count != null && count > 0;
    }

    @Transactional
    public void save(Student s) {
        jdbc.update("INSERT INTO students (student_id, student_name, email, course_name, programming_score, mathematics_score) VALUES (?, ?, ?, ?, ?, ?)",
                s.getStudentId(), s.getStudentName(), s.getEmail(), s.getCourseName(),
                s.getProgrammingScore(), s.getMathematicsScore());
        insertSkills(s.getStudentId(), s.getTechnicalSkills());
    }

    public List<Student> findAll() {
        return withSkills(jdbc.query(SELECT_STUDENTS + " ORDER BY student_id", mapper));
    }

    public Optional<Student> findById(int studentId) {
        List<Student> list = withSkills(jdbc.query(SELECT_STUDENTS + " WHERE student_id = ?", mapper, studentId));
        return list.stream().findFirst();
    }

    public Optional<Student> findByEmail(String email) {
        List<Student> list = withSkills(jdbc.query(SELECT_STUDENTS + " WHERE LOWER(email) = LOWER(?)", mapper, email));
        return list.stream().findFirst();
    }

    public List<Student> findByCourse(String courseName) {
        return withSkills(jdbc.query(SELECT_STUDENTS + " WHERE LOWER(course_name) = LOWER(?) ORDER BY student_id", mapper, courseName));
    }

    @Transactional
    public boolean updateScoresAndSkills(int studentId, double programmingScore, double mathematicsScore, List<String> skills) {
        int rows = jdbc.update("UPDATE students SET programming_score = ?, mathematics_score = ? WHERE student_id = ?",
                programmingScore, mathematicsScore, studentId);
        if (rows == 0) {
            return false;
        }
        jdbc.update("DELETE FROM student_skills WHERE student_id = ?", studentId);
        insertSkills(studentId, skills);
        return true;
    }

    public boolean deleteById(int studentId) {
        return jdbc.update("DELETE FROM students WHERE student_id = ?", studentId) > 0;
    }

    private void insertSkills(int studentId, List<String> skills) {
        jdbc.batchUpdate("INSERT INTO student_skills (student_id, skill) VALUES (?, ?)", skills, skills.size(),
                (ps, skill) -> {
                    ps.setInt(1, studentId);
                    ps.setString(2, skill);
                });
    }

    private List<Student> withSkills(List<Student> students) {
        if (students.isEmpty()) {
            return students;
        }
        Map<Integer, List<String>> skillMap = new HashMap<>();
        jdbc.query("SELECT student_id, skill FROM student_skills ORDER BY skill",
                (RowCallbackHandler) rs -> skillMap
                        .computeIfAbsent(rs.getInt("student_id"), k -> new ArrayList<>())
                        .add(rs.getString("skill")));
        for (Student s : students) {
            s.setTechnicalSkills(skillMap.getOrDefault(s.getStudentId(), new ArrayList<>()));
        }
        return students;
    }
}
