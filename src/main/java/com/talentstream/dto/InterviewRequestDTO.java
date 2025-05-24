package com.talentstream.dto;

public class InterviewRequestDTO {

	private Long applicantId;

	public InterviewRequestDTO(Long applicantId) {
		super();
		this.applicantId = applicantId;
	}

	public InterviewRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(Long applicantId) {
		this.applicantId = applicantId;
	}
}