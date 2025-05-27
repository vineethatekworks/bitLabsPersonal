package com.talentstream.service;

import com.google.gson.*;
import com.talentstream.dto.QuestionResponseDTO;
import com.talentstream.entity.*;
import com.talentstream.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InterviewService {

	@Value("${gemini.api.key}")
	private String apiKey;

	private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final Gson gson = new Gson();
	
	@Autowired
	private ApplicantProfileService ApplicantProfileService;

	@Autowired
	private InterviewSessionRepo sessionRepository;

	@Autowired
	private InterviewDataRepo dataRepository;

	public QuestionResponseDTO startInterview(Applicant applicant, List<String> skills) {
		if (skills == null || skills.isEmpty()) {
			throw new RuntimeException("No skills provided for the candidate");
		}

		String prompt = "Generate a beginner-level technical interview question about " + skills.get(0) + " that:\n" +
                "1. Tests core conceptual understanding (not advanced topics)\n" +
                "2. Is appropriate for recent graduates/freshers\n" +
                "3. Can be answered in 1-2 minutes\n" +
                "4. Has not been asked before in this interview\n" +
                "5. Focuses on fundamental principles rather than memorization\n" +
                "6. Does not require prior work experience\n\n" +
                "For the skill '" + skills.get(0) + "', provide ONLY the question (no numbering or extra text).";


		List<String> questions = callGemini(prompt);
		String firstQuestion = questions.isEmpty() ? "No question generated." : questions.get(0);

		InterviewSession session = new InterviewSession();
		session.setApplicant(applicant);
		session.setStartedAt(LocalDateTime.now());
		session.setStatus("IN_PROGRESS");

		InterviewData questionData = new InterviewData(session, 1, firstQuestion);
		session.setInterviewDataList(List.of(questionData));

		sessionRepository.save(session);

		return new QuestionResponseDTO(session.getId().toString(), 1, firstQuestion, false);
	}

	

	private List<String> callGemini(String prompt) {
		try {
			Map<String, Object> content = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GEMINI_URL + apiKey))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(content))).build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
			JsonArray candidates = json.getAsJsonArray("candidates");
			JsonObject parts = candidates.get(0).getAsJsonObject().getAsJsonObject("content").getAsJsonArray("parts")
					.get(0).getAsJsonObject();

			String text = parts.get("text").getAsString();

			return Arrays.stream(text.split("\n")).map(String::trim).filter(line -> !line.isEmpty())
					.collect(Collectors.toList());

		} catch (Exception e) {
			return List.of("Gemini API Error: " + e.getMessage());
		}
	}



	public QuestionResponseDTO evaluateAnswerAndGetNextQuestion(Long sessionId, int questionNumber, String answer) {
		// Retrieve the session and current question
		InterviewSession session = sessionRepository.findById(sessionId)
				.orElseThrow(() -> new RuntimeException("Session not found"));
		List<InterviewData> interviewDataList = session.getInterviewDataList();

		//get the skills from the session
		Applicant applicant = session.getApplicant();
		List<String> skills = ApplicantProfileService.getSkillNamesByApplicantId(applicant.getId());

				
		// Find the current question
		InterviewData currentQuestion = interviewDataList.stream()
				.filter(data -> data.getQuestionNumber() == questionNumber)
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Question not found"));

		// Evaluate the answer
		String feedback = evaluation(currentQuestion.getQuestionText(), answer, skills);
		return null;


	}

	



	private String evaluation(String question, String answer, List<String> skills) {

    String prompt = "Evaluate the following answer to the question: '" + question + "'\n" +
            "Answer: '" + answer + "'\n" +
            "give two line feedback on the answer what is good and what is bad and where it could be improved.\n" +
            "Return ONLY the feedback, no extra text or numbering.\n" +
            "give a score in percentage from 30 to 100 based on the answer, where 30 is the worst and 100 is the best.\n" +
			"based on the feedback, generate the next question. The next question should be related to the topic of the previous question, but at a slightly higher level of difficulty.\n" +
			"Return the feedback and score in the format:\n" +
			"Feedback: <feedback>\n" +
			"Score: <score>\n" +
			"Next Question: <next question>";
	return prompt;

  
	}
}
