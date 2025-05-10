package com.talentstream.dto;

import jakarta.validation.constraints.NotNull;

public class AiTestResponseSDTO {

	
	@NotNull(message = "Applicant ID must not be null")
	private Long applicantId;

	@NotNull(message = "Skill name is required")
	private String skillName;

	@NotNull(message = "Score is required")
	private Double score;

	public AiTestResponseSDTO() {
	}

	public AiTestResponseSDTO(Long applicantId, String skillName, Double score) {
		this.applicantId = applicantId;
		this.skillName = skillName;
		this.score = score;
	}

	public Long getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(Long applicantId) {
		this.applicantId = applicantId;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

}
