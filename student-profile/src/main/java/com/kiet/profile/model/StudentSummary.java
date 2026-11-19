package com.kiet.profile.model;

public class StudentSummary {

    private int rank;
    private int studentId;
    private String studentName;
    private String courseName;
    private double averageScore;
    private String performanceCategory;
    private String strongerSubject;

    public StudentSummary() {
    }

    public StudentSummary(int studentId, String studentName, String courseName,
                          double averageScore, String performanceCategory, String strongerSubject) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.averageScore = averageScore;
        this.performanceCategory = performanceCategory;
        this.strongerSubject = strongerSubject;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public String getPerformanceCategory() {
        return performanceCategory;
    }

    public void setPerformanceCategory(String performanceCategory) {
        this.performanceCategory = performanceCategory;
    }

    public String getStrongerSubject() {
        return strongerSubject;
    }

    public void setStrongerSubject(String strongerSubject) {
        this.strongerSubject = strongerSubject;
    }
}
