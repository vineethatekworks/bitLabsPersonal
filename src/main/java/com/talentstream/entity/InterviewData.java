package com.talentstream.entity;

import javax.persistence.*;

@Entity
@Table(name = "interview_data")
public class InterviewData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "session_id", nullable = false)
	private InterviewSession session;

	@Column(name = "question_number", nullable = false)
	private int questionNumber;

	@Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
	private String questionText;

	@Column(name = "answer_text", columnDefinition = "TEXT")
	private String answerText;

	@Column(name = "feedback", columnDefinition = "TEXT")
	private String feedback;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InterviewSession getSession() {
		return session;
	}

	public void setSession(InterviewSession session) {
		this.session = session;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

}
