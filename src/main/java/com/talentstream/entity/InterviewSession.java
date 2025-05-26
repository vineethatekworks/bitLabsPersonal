package com.talentstream.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "interview_session")
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startedAt;

    private String status; 

    private double totalScore;

    private int totalQuestionsAsked;

    @Column(columnDefinition = "TEXT")
    private String overallFeedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

    @OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InterviewSkill> interviewSkills;

    @OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InterviewData> interviewDataList;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalQuestionsAsked() {
        return totalQuestionsAsked;
    }

    public void setTotalQuestionsAsked(int totalQuestionsAsked) {
        this.totalQuestionsAsked = totalQuestionsAsked;
    }

    public String getOverallFeedback() {
        return overallFeedback;
    }

    public void setOverallFeedback(String overallFeedback) {
        this.overallFeedback = overallFeedback;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public List<InterviewSkill> getInterviewSkills() {
        return interviewSkills;
    }

    public void setInterviewSkills(List<InterviewSkill> interviewSkills) {
        this.interviewSkills = interviewSkills;
    }

    public List<InterviewData> getInterviewDataList() {
        return interviewDataList;
    }

    public void setInterviewDataList(List<InterviewData> interviewDataList) {
        this.interviewDataList = interviewDataList;
    }
}
