package com.talentstream.dto;

import java.util.UUID;

public class AnswerRequestDTO {
	private UUID sessionId;
	private int questionNumber;
	private String answer;
	

	public AnswerRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AnswerRequestDTO(UUID sessionId, int questionNumber, String answer) {
		super();
		this.sessionId = sessionId;
		this.questionNumber = questionNumber;
		this.answer = answer;
	}

	public UUID getSessionId() {
		return sessionId;
	}

	public void setSessionId(UUID sessionId) {
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
