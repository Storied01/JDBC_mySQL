package com.kiet.profile.service;

import com.kiet.profile.exception.DuplicateStudentException;
import com.kiet.profile.exception.StudentNotFoundException;
import com.kiet.profile.model.Student;
import com.kiet.profile.model.StudentSummary;
import com.kiet.profile.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student addStudent(Student student) {
        validateDetails(student);
        StudentAnalytics.validateScores(student);
        student.setTechnicalSkills(cleanSkills(student.getTechnicalSkills()));

        if (repository.existsById(student.getStudentId())) {
            throw new DuplicateStudentException("Student ID " + student.getStudentId() + " already exists");
        }
        if (repository.existsByEmail(student.getEmail())) {
            throw new DuplicateStudentException("Email " + student.getEmail() + " is already registered");
        }
        repository.save(student);
        return student;
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public Student searchById(int studentId) {
        return repository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID " + studentId));
    }

    public Student searchByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with email " + email));
    }

    public List<Student> searchByCourse(String courseName) {
        return repository.findByCourse(courseName);
    }

    public Student updateScoresAndSkills(int studentId, double programmingScore, double mathematicsScore, List<String> skills) {
        Student check = new Student();
        check.setProgrammingScore(programmingScore);
        check.setMathematicsScore(mathematicsScore);
        StudentAnalytics.validateScores(check);

        boolean updated = repository.updateScoresAndSkills(studentId, programmingScore, mathematicsScore, cleanSkills(skills));
        if (!updated) {
            throw new StudentNotFoundException("Student not found with ID " + studentId);
        }
        return searchById(studentId);
    }

    public void deleteStudent(int studentId) {
        if (!repository.deleteById(studentId)) {
            throw new StudentNotFoundException("Student not found with ID " + studentId);
        }
    }

    public List<StudentSummary> getSummaries() {
        List<StudentSummary> list = new ArrayList<>();
        for (Student s : repository.findAll()) {
            list.add(StudentAnalytics.summarize(s));
        }
        return list;
    }

    public List<StudentSummary> getMeritList() {
        return StudentAnalytics.meritList(repository.findAll());
    }

    public Map<String, List<String>> getStudentsSharingSkills() {
        return StudentAnalytics.studentsSharingSkills(repository.findAll());
    }

    public List<List<String>> getStudentsWithIdenticalSkillSets() {
        return StudentAnalytics.studentsWithIdenticalSkillSets(repository.findAll());
    }

    public List<Student> getNeedImprovementInProgrammingButStrongInMaths() {
        return StudentAnalytics.needImprovementInProgrammingStrongInMaths(repository.findAll());
    }

    private void validateDetails(Student s) {
        if (s == null) {
            throw new IllegalArgumentException("Student details are required");
        }
        if (s.getStudentId() <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number");
        }
        if (isBlank(s.getStudentName()) || isBlank(s.getCourseName())) {
            throw new IllegalArgumentException("Student name and course name are required");
        }
        if (isBlank(s.getEmail()) || !s.getEmail().contains("@")) {
            throw new IllegalArgumentException("A valid email address is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private List<String> cleanSkills(List<String> skills) {
        Map<String, String> unique = new LinkedHashMap<>();
        if (skills != null) {
            for (String skill : skills) {
                if (skill != null && !skill.isBlank()) {
                    unique.putIfAbsent(skill.trim().toLowerCase(), skill.trim());
                }
            }
        }
        return new ArrayList<>(unique.values());
    }
}
