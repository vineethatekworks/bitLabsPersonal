package com.talentstream.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.AiTestReport;

@Repository
public interface AiTestReportRepository extends JpaRepository<AiTestReport, Long> {

    @Query("SELECT a FROM AiTestReport a WHERE a.applicantId = :applicantId AND a.skillName = :skillName")
    Optional<AiTestReport> findByApplicantIdAndSkillName(Long applicantId, String skillName);
}