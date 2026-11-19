package com.kiet.profile.service;

import com.kiet.profile.model.Student;
import com.kiet.profile.model.StudentSummary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class StudentAnalytics {

    private StudentAnalytics() {
    }

    public static double average(Student s) {
        return (s.getProgrammingScore() + s.getMathematicsScore()) / 2.0;
    }

    public static String category(double average) {
        if (average >= 85) {
            return "Excellent";
        } else if (average >= 70) {
            return "Good";
        } else if (average >= 50) {
            return "Satisfactory";
        }
        return "Needs Improvement";
    }

    public static String strongerSubject(Student s) {
        if (s.getProgrammingScore() > s.getMathematicsScore()) {
            return "Programming";
        } else if (s.getMathematicsScore() > s.getProgrammingScore()) {
            return "Mathematics";
        }
        return "Both Equal";
    }

    public static StudentSummary summarize(Student s) {
        double avg = average(s);
        return new StudentSummary(s.getStudentId(), s.getStudentName(), s.getCourseName(),
                avg, category(avg), strongerSubject(s));
    }

    public static List<StudentSummary> meritList(List<Student> students) {
        List<StudentSummary> list = students.stream()
                .map(StudentAnalytics::summarize)
                .sorted(Comparator.comparingDouble(StudentSummary::getAverageScore).reversed()
                        .thenComparing(StudentSummary::getStudentName))
                .collect(Collectors.toList());
        int rank = 1;
        for (StudentSummary summary : list) {
            summary.setRank(rank++);
        }
        return list;
    }

    public static Map<String, List<String>> studentsSharingSkills(List<Student> students) {
        Map<String, List<String>> bySkill = new LinkedHashMap<>();
        for (Student s : students) {
            for (String skill : s.getTechnicalSkills()) {
                bySkill.computeIfAbsent(skill.toLowerCase(), k -> new ArrayList<>()).add(s.getStudentName());
            }
        }
        bySkill.values().removeIf(names -> names.size() < 2);
        return bySkill;
    }

    public static List<List<String>> studentsWithIdenticalSkillSets(List<Student> students) {
        Map<Set<String>, List<String>> bySet = new LinkedHashMap<>();
        for (Student s : students) {
            Set<String> key = new TreeSet<>();
            for (String skill : s.getTechnicalSkills()) {
                key.add(skill.toLowerCase());
            }
            if (!key.isEmpty()) {
                bySet.computeIfAbsent(key, k -> new ArrayList<>()).add(s.getStudentName());
            }
        }
        return bySet.values().stream()
                .filter(names -> names.size() > 1)
                .collect(Collectors.toList());
    }

    public static List<Student> needImprovementInProgrammingStrongInMaths(List<Student> students) {
        return students.stream()
                .filter(s -> category(s.getProgrammingScore()).equals("Needs Improvement"))
                .filter(s -> s.getMathematicsScore() >= 75)
                .collect(Collectors.toList());
    }

    public static void validateScores(Student s) {
        validateScore("Programming", s.getProgrammingScore());
        validateScore("Mathematics", s.getMathematicsScore());
    }

    private static void validateScore(String subject, double score) {
        if (Double.isNaN(score) || score < 0 || score > 100) {
            throw new IllegalArgumentException(subject + " score must be between 0 and 100");
        }
    }
}
