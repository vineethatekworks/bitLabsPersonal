package com.talentstream.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "interview_sessions")
public class InterviewSession {

	@Id
	@Column(name = "session_id", nullable = false)
	private UUID sessionId;

	@ManyToOne
	@JoinColumn(name = "applicant_id", nullable = false)
	private Applicant applicant;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InterviewData> interviewDataList = new ArrayList<>();

	@Column(name = "overall_feedback", columnDefinition = "TEXT")
	private String overallFeedback;

	@Column(name = "score")
	private int score;

	public UUID getSessionId() {
		return sessionId;
	}

	public void setSessionId(UUID sessionId) {
		this.sessionId = sessionId;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<InterviewData> getInterviewDataList() {
		return interviewDataList;
	}

	public void setInterviewDataList(List<InterviewData> interviewDataList) {
		this.interviewDataList = interviewDataList;
	}

	public String getOverallFeedback() {
		return overallFeedback;
	}

	public void setOverallFeedback(String overallFeedback) {
		this.overallFeedback = overallFeedback;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
