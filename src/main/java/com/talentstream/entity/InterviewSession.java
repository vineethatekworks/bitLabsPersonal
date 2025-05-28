package com.talentstream.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "interview_session")
public class InterviewSession {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

    private LocalDateTime startedAt;

    private String status;  // e.g. "IN_PROGRESS", "COMPLETED"

    private int currentSkillIndex;

    private String currentDifficulty;

    @Column(columnDefinition = "TEXT")
    private String skillsJson;

    @OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewData> interviewDataList;

    @Transient
    private List<String> skills;
    
    @Column(columnDefinition = "TEXT",length = 2000)
    private String Overallfeedback;

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCurrentSkillIndex() {
        return currentSkillIndex;
    }

    public void setCurrentSkillIndex(int currentSkillIndex) {
        this.currentSkillIndex = currentSkillIndex;
    }

    public String getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(String currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public String getSkillsJson() {
        return skillsJson;
    }

    public void setSkillsJson(String skillsJson) {
        this.skillsJson = skillsJson;
    }

    public List<InterviewData> getInterviewDataList() {
        return interviewDataList;
    }

    public void setInterviewDataList(List<InterviewData> interviewDataList) {
        this.interviewDataList = interviewDataList;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

	public String getOverallfeedback() {
		return Overallfeedback;
	}

	public void setOverallfeedback(String overallfeedback) {
		Overallfeedback = overallfeedback;
	}
}
