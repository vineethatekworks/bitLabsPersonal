package com.talentstream.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talentstream.dto.InterviewRequestDTO;
import com.talentstream.service.InterviewService;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {
	
	@Autowired
	private InterviewService interviewService;
	
	
	@PostMapping
	public ResponseEntity<?> interview(@RequestBody InterviewRequestDTO request){
		if(request.getAnswers() == null || request.getAnswers().isEmpty()) {
            return interviewService.generateQuestions(request.getName(), request.getSkill());
		}
		else
		{
            return interviewService.evaluateAnswers(request.getName(), request.getSkill(), request.getAnswers(), request.getQuestions());
		}
	}

}
