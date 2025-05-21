package com.talentstream.dto;

import java.util.List;

public class EvaluateResponseDTO {
	private List<String> feedback;
	private double score;

	public EvaluateResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EvaluateResponseDTO(List<String> feedback, double score) {
		super();
		this.feedback = feedback;
		this.score = score;
	}

	public List<String> getFeedback() {
		return feedback;
	}

	public void setFeedback(List<String> feedback) {
		this.feedback = feedback;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}
