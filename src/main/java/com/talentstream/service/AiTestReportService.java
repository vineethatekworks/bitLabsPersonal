package com.talentstream.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talentstream.entity.AiTestReport;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.AiTestReportRepository;
import com.talentstream.repository.ApplicantRepository;

@Service
public class AiTestReportService {

	@Autowired
	private AiTestReportRepository reportRepository;
	
	@Autowired
	private ApplicantRepository applicantRepository;

	public String saveOrUpdate(Long applicantId, String skillName, Double score) {

		 try {
			 
			    if (!applicantRepository.existsById(applicantId)) {
		            throw new CustomException("Applicant not found",HttpStatus.BAD_REQUEST);
		        }
	            Optional<AiTestReport> existing = reportRepository.findByApplicantIdAndSkillName(applicantId, skillName);

	            if (existing.isPresent()) {
	                // Update the existing record
	                AiTestReport report = existing.get();
	                report.setScore(score);
	                report.setNoOfAttempts(report.getNoOfAttempts() + 1);
	                reportRepository.save(report);
	                return "Existing record updated.";
	            } else {
	                // Insert a new record
	                AiTestReport report = new AiTestReport();
	                report.setApplicantId(applicantId);
	                report.setSkillName(skillName);
	                report.setScore(score);	
//	                report.setNoOfAttempts(1); 
	                reportRepository.save(report);
	                return "New record inserted.";
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("Failed to save or update AI test report: " + e.getMessage(), e);
	        }
	    }
}
