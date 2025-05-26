package com.talentstream.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "interview_skill")
public class InterviewSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;

    private int noOfQuestionsAsked;

    private double score;

    @Column(columnDefinition = "TEXT")
    private String feedbackOnSkill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private InterviewSession interviewSession;

    @OneToMany(mappedBy = "interviewSkill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InterviewData> interviewDataList;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getNoOfQuestionsAsked() {
        return noOfQuestionsAsked;
    }

    public void setNoOfQuestionsAsked(int noOfQuestionsAsked) {
        this.noOfQuestionsAsked = noOfQuestionsAsked;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getFeedbackOnSkill() {
        return feedbackOnSkill;
    }

    public void setFeedbackOnSkill(String feedbackOnSkill) {
        this.feedbackOnSkill = feedbackOnSkill;
    }

    public InterviewSession getInterviewSession() {
        return interviewSession;
    }

    public void setInterviewSession(InterviewSession interviewSession) {
        this.interviewSession = interviewSession;
    }

    public List<InterviewData> getInterviewDataList() {
        return interviewDataList;
    }

    public void setInterviewDataList(List<InterviewData> interviewDataList) {
        this.interviewDataList = interviewDataList;
    }
}
