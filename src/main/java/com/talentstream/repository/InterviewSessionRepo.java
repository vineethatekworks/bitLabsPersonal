package com.talentstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.InterviewSession;

@Repository
public interface InterviewSessionRepo extends JpaRepository<InterviewSession, Long>{

}
