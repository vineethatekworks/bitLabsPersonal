package com.talentstream.service;

import com.google.gson.*;
import com.talentstream.dto.QuestionResponseDTO;
import com.talentstream.entity.*;
import com.talentstream.exception.InterviewException;
import com.talentstream.exception.ResourceNotFoundException;
import com.talentstream.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InterviewService {

	private static final Logger logger = LoggerFactory.getLogger(InterviewService.class);
	private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
	private static final Set<String> VALID_DIFFICULTIES = Set.of("easy", "medium", "hard");
	private static final String DEFAULT_DIFFICULTY = "easy";

	@Value("${gemini.api.key}")
	private String apiKey;

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final Gson gson = new GsonBuilder().create();

	@Autowired
	private ApplicantProfileService applicantProfileService;

	@Autowired
	private InterviewSessionRepo sessionRepository;

	@Autowired
	private InterviewDataRepo dataRepository;

	@Transactional
	public QuestionResponseDTO startInterview(Applicant applicant, List<String> skills) throws InterviewException {
		validateApplicantAndSkills(applicant, skills);

		// Check for existing active session
//		Optional<InterviewSession> existingSession = sessionRepository.findByApplicantIdAndStatus(applicant.getId(),
//				"IN_PROGRESS");
//		if (existingSession.isPresent()) {
//			throw new InterviewException("Applicant already has an active interview session");
//		}

		String firstSkill = skills.get(0);
		String prompt = generatePromptForQuestion(firstSkill, DEFAULT_DIFFICULTY, null, null);

		try {
			List<String> questions = callGemini(prompt);
			String firstQuestion = questions.stream().findFirst()
					.orElseThrow(() -> new InterviewException("Failed to generate initial question"));

			InterviewSession session = new InterviewSession();
			session.setApplicant(applicant);
			session.setStartedAt(LocalDateTime.now());
			session.setStatus("IN_PROGRESS");
			session.setCurrentSkillIndex(0);
			session.setCurrentDifficulty(DEFAULT_DIFFICULTY);

			InterviewData questionData = new InterviewData(session, 1, firstQuestion);
			session.setInterviewDataList(List.of(questionData));

			session = sessionRepository.save(session);
			System.out.println("Started new interview session: {}" + session.getId());

			return new QuestionResponseDTO(session.getId().toString(), 1, firstQuestion, false,
					"Let's begin the interview with this question");
		} catch (Exception e) {
			logger.error("Failed to start interview", e);
			throw new InterviewException("Failed to start interview: " + e.getMessage());
		}
	}

	@Transactional
	public QuestionResponseDTO evaluateAnswer(UUID sessionId, String applicantAnswer, int questionNum)
			throws InterviewException, ResourceNotFoundException {

		InterviewSession session = sessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException("Interview session not found"));

		if ("COMPLETED".equals(session.getStatus())) {
			throw new InterviewException("Interview session has already been completed");
		}

		List<String> skills = applicantProfileService.getSkillNamesByApplicantId(session.getApplicant().getId());
		if (skills.isEmpty()) {
			throw new InterviewException("No skills found for applicant");
		}

		int currentSkillIndex = session.getCurrentSkillIndex();
		if (currentSkillIndex >= skills.size()) {
			completeSession(session,null);
			return new QuestionResponseDTO(session.getId().toString(), questionNum,
					"Interview completed - all skills assessed", true, "You've completed all skill assessments");
		}

		String currentSkill = skills.get(currentSkillIndex);
		String currentDifficulty = session.getCurrentDifficulty();

		// Save the applicant's answer to the current question
		Optional<InterviewData> currentQuestionData = session.getInterviewDataList().stream()
				.filter(data -> data.getQuestionNumber() == questionNum).findFirst();

		

		String evalPrompt = generatePromptForEvaluation(currentSkill, currentDifficulty, applicantAnswer);
		try {
			List<String> responseLines = callGemini(evalPrompt);

			JsonObject responseJson = parseAIResponse(responseLines);
			String action = responseJson.get("action").getAsString().toLowerCase();
			String feedback = responseJson.has("feedback") ? responseJson.get("feedback").getAsString()
					: "No feedback provided";
			
			
			if (currentQuestionData.isPresent()) {
				InterviewData data = currentQuestionData.get();
				data.setFeedback(feedback);
				//dataRepository.save(data);
			}

			switch (action) {
			case "next_skill":
				return handleNextSkill(session, skills, currentSkillIndex, questionNum, feedback);
			case "simpler_question":
				return handleSimplerQuestion(session, currentSkill, questionNum, feedback);
			case "next_question":
				return handleNextQuestion(session, currentSkill, questionNum, feedback);
			case "end":
				return completeInterview(session, questionNum, feedback);
			default:
				logger.warn("Unknown action from Gemini: {}", action);
				return handleDefaultAction(session, currentSkill, questionNum, feedback);
			}
		} catch (JsonSyntaxException e) {
			logger.error("Failed to parse Gemini response", e);
			throw new InterviewException("Failed to parse evaluation response");
		} catch (Exception e) {
			logger.error("Error during answer evaluation", e);
			throw new InterviewException("Evaluation failed: " + e.getMessage());
		}
	}
	
	private String generateOverallFeedback(InterviewSession session) throws InterviewException {
	    List<String> allFeedback = session.getInterviewDataList().stream()
	            .filter(data -> data.getFeedback() != null && !data.getFeedback().isEmpty())
	            .map(InterviewData::getFeedback)
	            .collect(Collectors.toList());

	    if (allFeedback.isEmpty()) {
	        return "Interview completed. No specific feedback available.";
	    }

	    String prompt = "You're an expert technical interviewer. Based on these individual feedback points:\n\n" +
	    	    String.join("\n", allFeedback) +
	    	    "\n\nGenerate a concise overall feedback (3-4 sentences) summarizing the candidate's performance " +
	    	    "across all technical skills assessed. Highlight strengths and areas for improvement. " +
	    	    "Provide only the feedback text without any additional formatting or headings. " +
	    	    "IMPORTANT: The feedback must be at most 255 characters long. " +
	    	    "If necessary, shorten or rephrase to fit this limit exactly. " +
	    	    "For example: 'Candidate shows strong Java skills but needs improvement in JavaScript. Overall, a solid foundation.' (under 255 chars)";

	    try {
	        List<String> response = callGemini(prompt);
	        System.out.println("response :"+response);
	        if (response == null || response.isEmpty()) {
	            logger.warn("callGemini returned empty response.");
	            return "Interview completed. Overall feedback unavailable.";
	        }
	        String feedback = response.toString().replace("[", "").replace("]", "");
	        System.out.println("feedback :"+feedback);
	        
	        return feedback;
	    } catch (Exception e) {
	        logger.error("Failed to generate overall feedback", e);
	        return "Interview completed. Overall feedback unavailable.";
	    }
	}

	private QuestionResponseDTO handleNextSkill(InterviewSession session, List<String> skills, int currentSkillIndex,
			int questionNum, String feedback) throws InterviewException {
		
		//store anser and feedback
		int nextSkillIndex = currentSkillIndex + 1;

		if (nextSkillIndex >= skills.size()) {
			return completeInterview(session, questionNum, feedback);
		}

		session.setCurrentSkillIndex(nextSkillIndex);
		session.setCurrentDifficulty(DEFAULT_DIFFICULTY);

		String nextSkill = skills.get(nextSkillIndex);
		String nextQuestion = generateQuestion(nextSkill, DEFAULT_DIFFICULTY);

		return createNextQuestionResponse(session, questionNum + 1, nextQuestion,
				String.format("%s Moving to next skill: %s", feedback, nextSkill));
	}

	private QuestionResponseDTO handleSimplerQuestion(InterviewSession session, String currentSkill, int questionNum,
			String feedback) throws InterviewException {
		String newDifficulty = downgradeDifficulty(session.getCurrentDifficulty());
		session.setCurrentDifficulty(newDifficulty);

		String nextQuestion = generateQuestion(currentSkill, newDifficulty);

		return createNextQuestionResponse(session, questionNum + 1, nextQuestion,
				String.format("%s Let's try a simpler question", feedback));
	}

	private QuestionResponseDTO handleNextQuestion(InterviewSession session, String currentSkill, int questionNum,
			String feedback) throws InterviewException {
		String newDifficulty = upgradeDifficulty(session.getCurrentDifficulty());
		session.setCurrentDifficulty(newDifficulty);

		String nextQuestion = generateQuestion(currentSkill, newDifficulty);

		return createNextQuestionResponse(session, questionNum + 1, nextQuestion,
				String.format("%s Let's try a more challenging question", feedback));
	}

	private QuestionResponseDTO handleDefaultAction(InterviewSession session, String currentSkill, int questionNum,
			String feedback) throws InterviewException {
		// Fallback: continue with same difficulty
		String nextQuestion = generateQuestion(currentSkill, session.getCurrentDifficulty());
		return createNextQuestionResponse(session, questionNum + 1, nextQuestion, feedback);
	}

	private QuestionResponseDTO completeInterview(InterviewSession session, int questionNum, String feedback) throws InterviewException {
	    String overallFeedback = generateOverallFeedback(session);
	    logger.info("overallfeedback: ",overallFeedback);

	    
	    completeSession(session,overallFeedback);
	    return new QuestionResponseDTO(
	        session.getId().toString(),  
	        true, 
	        overallFeedback
	    );
	}

	private QuestionResponseDTO createNextQuestionResponse(InterviewSession session, int nextQuestionNum,
			String nextQuestion, String feedback) {
		InterviewData newData = new InterviewData(session, nextQuestionNum, nextQuestion,feedback);

// Modify the existing list directly instead of creating a new one
		session.getInterviewDataList().add(newData);

		sessionRepository.save(session);

		return new QuestionResponseDTO(session.getId().toString(), nextQuestionNum, nextQuestion, false, feedback);
	}

	private void completeSession(InterviewSession session,String overallFeedback) {
		System.out.println("overall feedabcak :"+ overallFeedback);
		session.setStatus("COMPLETED");
		session.setOverallfeedback(overallFeedback);
		sessionRepository.save(session);
		logger.info("Completed interview session: {}", session.getId());
	}

	private List<String> callGemini(String prompt) throws InterviewException {
		try {
			Map<String, Object> content = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GEMINI_URL + apiKey))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(content))).build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				throw new InterviewException("Gemini API returned status: " + response.statusCode());
			}

			JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

			if (json.has("error")) {
				throw new InterviewException("Gemini API error: " + json.get("error").toString());
			}

			JsonArray candidates = json.getAsJsonArray("candidates");
			if (candidates == null || candidates.isEmpty()) {
				throw new InterviewException("No candidates in Gemini response");
			}

			JsonObject cont = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
			JsonArray parts = cont.getAsJsonArray("parts");
			if (parts == null || parts.isEmpty()) {
				throw new InterviewException("No parts in Gemini response content");
			}

			String text = parts.get(0).getAsJsonObject().get("text").getAsString();

			return Arrays.stream(text.split("\n")).map(String::trim).filter(line -> !line.isBlank())
					.collect(Collectors.toList());

		} catch (Exception e) {
			logger.error("Error calling Gemini API", e);
			throw new InterviewException("Error calling Gemini API: " + e.getMessage());
		}
	}

	private String generatePromptForQuestion(String skill, String difficulty, String previousAnswer,
			String extraInstructions) {
		difficulty = validateDifficulty(difficulty);

		StringBuilder prompt = new StringBuilder();
		prompt.append("You're an expert technical interviewer assessing a candidate's knowledge of ").append(skill).append(".\n")
	      .append("Generate a ").append(difficulty).append(" level question that:\n")
	      .append("- Tests practical knowledge of ").append(skill).append("\n")
	      .append("- Is clear and concise and do not ask programming-based questions\n")
	      .append("- Has a definitive answer\n")
	      .append("- Is unique and not a repeat of commonly asked interview questions\n")
	      .append("- Should not be repeated across sessions\n")
	      .append("Provide ONLY the question text without numbering or additional explanation.");


		if (previousAnswer != null) {
			prompt.append("\nPrevious answer was: ").append(previousAnswer);
		}
		if (extraInstructions != null) {
			prompt.append("\nAdditional instructions: ").append(extraInstructions);
		}

		return prompt.toString();
	}

	private String generatePromptForEvaluation(String skill, String difficulty, String applicantAnswer) {
	    difficulty = validateDifficulty(difficulty);
	    
	    return "Evaluate this " + difficulty + " level " + skill + " answer from a fresh grad:\n\"" 
	            + applicantAnswer + "\"\n\n"
	            + "Score (0-100) based on:\n"
	            + "- Technical accuracy (30)\n"
	            + "- Knowledge depth (25)\n"
	            + "- Explanation clarity (25)\n"
	            + "- Logical thinking (20)\n\n"
	            + "Action rules:\n"
	            + ">65: next_skill\n"
	            + "50-65: similar question\n"
	            + "35-49: easier question\n"
	            + "<35/I don't know/irrelavant: next_skill\n\n"
	            + "Respond with JSON containing:\n"
	            + "1. Action based on score\n"
	            + "2. Calculated score\n"
	            + "3. Next question (if applicable)\n"
	            + "4. Brief feedback including the score (e.g., 'Your score: 72/100. [feedback]')\n\n"
	            + "Format:\n"
	            + "{\"action\":\"...\", \"score\":0-100, \"question\":\"\", "
	            + "\"feedback\":\"Your score: X/100. [3-line feedback]\"}";
	}
	private JsonObject parseAIResponse(List<String> response) throws InterviewException {
	    try {
	        System.out.println("response: " + response);

	        String combined = String.join(" ", response).trim();

	        String cleaned = combined.replaceAll("```json", "")
	                                 .replaceAll("```", "")
	                                 .trim();

	        int start = cleaned.indexOf("{");
	        int end = cleaned.lastIndexOf("}");

	        if (start == -1 || end == -1 || end <= start) {
	            throw new InterviewException("Invalid JSON content in response");
	        }

	        String jsonPart = cleaned.substring(start, end + 1).trim();
	        System.out.println("jsonPart: " + jsonPart);

	        return JsonParser.parseString(jsonPart).getAsJsonObject();

	    } catch (Exception e) {
	        logger.error("Failed to parse response: {}", response, e);
	        throw new InterviewException("Invalid response format from Gemini");
	    }
	}


	private String generateQuestion(String skill, String difficulty) throws InterviewException {
		String prompt = generatePromptForQuestion(skill, difficulty, null, null);
		List<String> questions = callGemini(prompt);
		return questions.stream().findFirst().orElseThrow(() -> new InterviewException("Failed to generate question"));
	}

	private String upgradeDifficulty(String difficulty) {
		difficulty = validateDifficulty(difficulty);

		switch (difficulty) {
		case "easy":
			return "medium";
		case "medium":
			return "hard";
		default:
			return "hard";
		}
	}

	private String downgradeDifficulty(String difficulty) {
		difficulty = validateDifficulty(difficulty);

		switch (difficulty) {
		case "hard":
			return "medium";
		case "medium":
			return "easy";
		default:
			return "easy";
		}
	}

	private String validateDifficulty(String difficulty) {
		return VALID_DIFFICULTIES.contains(difficulty.toLowerCase()) ? difficulty.toLowerCase() : DEFAULT_DIFFICULTY;
	}

	private void validateApplicantAndSkills(Applicant applicant, List<String> skills) throws InterviewException {
		if (applicant == null) {
			throw new InterviewException("Applicant cannot be null");
		}
		if (skills == null || skills.isEmpty()) {
			throw new InterviewException("At least one skill is required");
		}
	}
}