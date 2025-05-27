package com.talentstream.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentstream.entity.InterviewData;
import com.talentstream.entity.InterviewSession;

public interface InterviewDataRepo extends JpaRepository<InterviewData, Long> {
    Optional<InterviewData> findByInterviewSessionAndQuestionNumber(InterviewSession interviewSession, int questionNumber);
}
