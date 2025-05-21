package com.talentstream.dto;

import java.util.List;

public class InterviewRequestDTO {

	private String name;
	private String skill;
	private List<String> questions;
	private List<String> answers;
	
	

	public InterviewRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}


	public InterviewRequestDTO(String name, String skill, List<String> questions, List<String> answers) {
		super();
		this.name = name;
		this.skill = skill;
		this.questions = questions;
		this.answers = answers;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public void setQuestions(List<String> questions) {
		this.questions = questions;
	}

}
