package com.talentstream.dto;

public class AnswerRequestDTO {
	private Long sessionId;
	private int questionNumber;
	private String answer;
	

	public AnswerRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AnswerRequestDTO(Long sessionId, int questionNumber, String answer) {
		super();
		this.sessionId = sessionId;
		this.questionNumber = questionNumber;
		this.answer = answer;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
