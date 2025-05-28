package com.talentstream.dto;

import java.util.List;

public class InterviewResponseDTO {
	private List<String> questions;
	
	

	public InterviewResponseDTO(List<String> questions) {
		super();
		this.questions = questions;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public void setQuestions(List<String> questions) {
		this.questions = questions;
	}
	
	
}