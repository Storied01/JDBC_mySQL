package com.kiet.profile;

import com.kiet.profile.model.Student;
import com.kiet.profile.model.StudentSummary;
import com.kiet.profile.service.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class Q2Demo implements CommandLineRunner {

    private final StudentService service;

    public Q2Demo(StudentService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (service.getAllStudents().isEmpty()) {
            service.addStudent(new Student(1, "Aarav Sharma", "aarav@kiet.edu", "B.Tech CSE", 92, 88, List.of("Java", "SQL", "Spring Boot")));
            service.addStudent(new Student(2, "Diya Verma", "diya@kiet.edu", "B.Tech CSE", 45, 82, List.of("Python", "SQL")));
            service.addStudent(new Student(3, "Rohan Gupta", "rohan@kiet.edu", "B.Tech IT", 78, 65, List.of("Java", "SQL", "Spring Boot")));
            service.addStudent(new Student(4, "Sneha Rao", "sneha@kiet.edu", "B.Tech CSE", 55, 40, List.of("HTML", "CSS")));
            service.addStudent(new Student(5, "Kabir Singh", "kabir@kiet.edu", "B.Tech IT", 35, 90, List.of("Python", "SQL")));
            service.addStudent(new Student(6, "Meera Nair", "meera@kiet.edu", "B.Tech AI", 88, 91, List.of("Python", "Machine Learning")));
        }

        System.out.println("\n===== 1, 2, 3. AVERAGE, CATEGORY AND STRONGER SUBJECT =====");
        System.out.printf("%-4s %-15s %-8s %-18s %-12s%n", "ID", "Name", "Average", "Category", "Stronger");
        for (StudentSummary s : service.getSummaries()) {
            System.out.printf("%-4d %-15s %-8.2f %-18s %-12s%n", s.getStudentId(), s.getStudentName(),
                    s.getAverageScore(), s.getPerformanceCategory(), s.getStrongerSubject());
        }

        System.out.println("\n===== 4. SEARCH =====");
        System.out.println("By ID 3          : " + service.searchById(3));
        System.out.println("By email diya    : " + service.searchByEmail("diya@kiet.edu"));
        System.out.println("By course IT     : ");
        service.searchByCourse("B.Tech IT").forEach(s -> System.out.println("   " + s));

        System.out.println("\n===== 5. MERIT LIST =====");
        System.out.printf("%-5s %-15s %-8s %-18s%n", "Rank", "Name", "Average", "Category");
        for (StudentSummary s : service.getMeritList()) {
            System.out.printf("%-5d %-15s %-8.2f %-18s%n", s.getRank(), s.getStudentName(),
                    s.getAverageScore(), s.getPerformanceCategory());
        }

        System.out.println("\n===== 6. STUDENTS WITH THE SAME TECHNICAL SKILLS =====");
        System.out.println("Identical skill sets:");
        service.getStudentsWithIdenticalSkillSets().forEach(group -> System.out.println("   " + group));
        System.out.println("Students sharing each skill:");
        for (Map.Entry<String, List<String>> e : service.getStudentsSharingSkills().entrySet()) {
            System.out.println("   " + e.getKey() + " -> " + e.getValue());
        }

        System.out.println("\n===== 7. NEED IMPROVEMENT IN PROGRAMMING BUT MATHS >= 75 =====");
        service.getNeedImprovementInProgrammingButStrongInMaths().forEach(s ->
                System.out.println("   " + s.getStudentName() + " (Programming " + s.getProgrammingScore()
                        + ", Mathematics " + s.getMathematicsScore() + ")"));

        System.out.println("\n===== 8. VALIDATION =====");
        try {
            service.addStudent(new Student(1, "Duplicate Id", "dup@kiet.edu", "B.Tech CSE", 60, 60, List.of("Java")));
        } catch (RuntimeException e) {
            System.out.println("Duplicate ID rejected   : " + e.getMessage());
        }
        try {
            service.addStudent(new Student(7, "Bad Score", "bad@kiet.edu", "B.Tech CSE", 150, 60, List.of("Java")));
        } catch (RuntimeException e) {
            System.out.println("Invalid score rejected  : " + e.getMessage());
        }
        try {
            service.addStudent(new Student(7, "Negative Score", "neg@kiet.edu", "B.Tech CSE", 60, -5, List.of("Java")));
        } catch (RuntimeException e) {
            System.out.println("Negative score rejected : " + e.getMessage());
        }
    }
}
