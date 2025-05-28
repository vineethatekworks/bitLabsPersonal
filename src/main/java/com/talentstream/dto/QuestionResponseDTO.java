package com.talentstream.dto;

public class QuestionResponseDTO {
	private String sessionId;
	private int questionNumber;
	private String question;
	private boolean completed;
	private String feedback;
	
	

	public QuestionResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuestionResponseDTO(String sessionId, int questionNumber, String question, boolean completed,
			String feedback) {
		super();
		this.sessionId = sessionId;
		this.questionNumber = questionNumber;
		this.question = question;
		this.completed = completed;
		this.feedback = feedback;
	}

	

	public QuestionResponseDTO(String sessionId, boolean completed, String overallFeedback) {
		this.sessionId = sessionId;
		this.completed = completed;
		this.feedback = overallFeedback;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

}