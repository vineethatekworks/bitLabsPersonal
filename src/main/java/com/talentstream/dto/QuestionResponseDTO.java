package com.talentstream.dto;


public class QuestionResponseDTO {

	private String sessionId;
	private int QuestionNumber;
	private String Question;
	private boolean interviewCompleted;

	

	public QuestionResponseDTO(String sessionId, int questionNumber, String question, boolean interviewCompleted) {
		super();
		this.sessionId = sessionId;
		QuestionNumber = questionNumber;
		Question = question;
		this.interviewCompleted = interviewCompleted;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getQuestionNumber() {
		return QuestionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		QuestionNumber = questionNumber;
	}

	public String getQuestion() {
		return Question;
	}

	public void setQuestion(String question) {
		Question = question;
	}

	public boolean isInterviewCompleted() {
		return interviewCompleted;
	}

	public void setInterviewCompleted(boolean interviewCompleted) {
		this.interviewCompleted = interviewCompleted;
	}

}
