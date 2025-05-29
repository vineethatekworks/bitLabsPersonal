package com.talentstream.entity;

import javax.persistence.*;

@Entity
@Table(name = "interview_data")
public class InterviewData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession interviewSession;

    @Column(nullable = false)
    private int questionNumber;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;
//
//    @Column(columnDefinition = "TEXT")
//    private String answerText;

//   private double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    public InterviewData() {
    }

    public InterviewData(InterviewSession interviewSession, int questionNumber, String questionText) {
        this.interviewSession = interviewSession;
        this.questionNumber = questionNumber;
        this.questionText = questionText;
    }
    public InterviewData(InterviewSession interviewSession, int questionNumber, String questionText, String feedback) {
        this.interviewSession = interviewSession;
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.feedback = feedback;
    }

	public Long getId() {
        return id;
    }

    public InterviewSession getInterviewSession() {
        return interviewSession;
    }

    public void setInterviewSession(InterviewSession interviewSession) {
        this.interviewSession = interviewSession;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

//    public String getAnswerText() {
//        return answerText;
//    }
//
//    public void setAnswerText(String answerText) {
//        this.answerText = answerText;
//    }
//
//    public double getScore() {
//        return score;
//    }
//
//    public void setScore(double score) {
//        this.score = score;
//    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
