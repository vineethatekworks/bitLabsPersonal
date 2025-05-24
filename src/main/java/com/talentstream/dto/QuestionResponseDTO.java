package com.talentstream.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionResponseDTO {

	private String sessionId;
	private int nextQuestionNumber;
	private String nextQuestion;
	private boolean interviewCompleted;
	private String overallFeedback;
	private int score;

	// Dynamic feedbacks like "q1 feedback", "q2 feedback"
	private final Map<String, String> feedbacks = new LinkedHashMap<>();

	public QuestionResponseDTO() {
	}

	// Constructor for ongoing interview
	public QuestionResponseDTO(String sessionId, int nextQuestionNumber, String nextQuestion,
			boolean interviewCompleted) {
		this.sessionId = sessionId;
		this.nextQuestionNumber = nextQuestionNumber;
		this.nextQuestion = nextQuestion;
		this.interviewCompleted = interviewCompleted;
	}

	// Constructor for completed interview
	public QuestionResponseDTO(String sessionId, boolean interviewCompleted, Map<String, String> feedbacks,
			String overallFeedback, int score) {
		this.sessionId = sessionId;
		this.interviewCompleted = interviewCompleted;
		this.feedbacks.putAll(feedbacks);
		this.overallFeedback = overallFeedback;
		this.score = score;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getNextQuestionNumber() {
		return nextQuestionNumber;
	}

	public void setNextQuestionNumber(int nextQuestionNumber) {
		this.nextQuestionNumber = nextQuestionNumber;
	}

	public String getNextQuestion() {
		return nextQuestion;
	}

	public void setNextQuestion(String nextQuestion) {
		this.nextQuestion = nextQuestion;
	}

	public boolean isInterviewCompleted() {
		return interviewCompleted;
	}

	public void setInterviewCompleted(boolean interviewCompleted) {
		this.interviewCompleted = interviewCompleted;
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

	@JsonAnyGetter
	public Map<String, String> getFeedbacks() {
		return feedbacks;
	}

	public void addFeedback(String questionKey, String feedbackText) {
		this.feedbacks.put(questionKey, feedbackText);
	}
}
