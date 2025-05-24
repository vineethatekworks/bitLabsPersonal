package com.talentstream.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.AnswerRequestDTO;
import com.talentstream.dto.InterviewRequestDTO;
import com.talentstream.dto.QuestionResponseDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.service.ApplicantProfileService;
import com.talentstream.service.InterviewService;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {
	
	@Autowired
	private InterviewService interviewService;
	
	@Autowired
	private ApplicantProfileService ApplicantProfileService;
	
	
	@PostMapping("/start")
	public ResponseEntity<?> startInterview(@RequestBody InterviewRequestDTO request){
		    Applicant applicant = new Applicant();
	        applicant.setId(request.getApplicantId());
			List<String> skills = ApplicantProfileService.getSkillNamesByApplicantId(request.getApplicantId());
			System.out.println("skills"+skills);
	        QuestionResponseDTO firstQuestion = interviewService.startInterview(applicant,skills);
	        return ResponseEntity.ok(firstQuestion);
	}
	
	@PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(@RequestBody AnswerRequestDTO request) {
      try {
    	  QuestionResponseDTO response = interviewService.submitAnswer(
                  request.getSessionId(),
                  request.getQuestionNumber(),
                  request.getAnswer());
          return ResponseEntity.ok(response);
		
	}catch (Exception e) {
	    e.printStackTrace(); // or System.out.println(e.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("Error occurred: " + e.getMessage());
	}
    }

}
