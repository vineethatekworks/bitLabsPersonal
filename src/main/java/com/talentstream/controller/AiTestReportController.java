package com.talentstream.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.AiTestResponseSDTO;
import com.talentstream.service.AiTestReportService;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/test-report")
public class AiTestReportController {


    @Autowired
    private AiTestReportService aiTestReportService;

    @PostMapping("/save-or-update")
    public ResponseEntity<String> createAiTestReport(@Valid @RequestBody AiTestResponseSDTO request) {
        try {
        	System.out.println("debugging: "+request.getApplicantId()+" "+ request.getSkillName()+" "+ request.getScore() );
            String result = aiTestReportService.saveOrUpdate(request.getApplicantId(), request.getSkillName(), request.getScore());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}
