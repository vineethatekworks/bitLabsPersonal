package com.talentstream.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.InterviewSession;

@Repository
public interface InterviewSessionRepo extends JpaRepository<InterviewSession, UUID>{

	Optional<InterviewSession> findByApplicantIdAndStatus(long id, String string);

}
