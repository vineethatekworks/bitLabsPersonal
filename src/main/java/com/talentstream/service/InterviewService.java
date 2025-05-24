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
    private static final int TOTAL_QUESTIONS = 3;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Autowired
    private  InterviewSessionRepo sessionRepository;
    @Autowired
    private  InterviewDataRepo dataRepository;


    // Start interview, pass Applicant entity (load this in your controller)
    public QuestionResponseDTO startInterview(Applicant applicant, List<String> skills) {
        String prompt = buildPrompt(skills);
        List<String> questions = callGemini(prompt);

        InterviewSession session = new InterviewSession();
        session.setSessionId(UUID.randomUUID());
        session.setApplicant(applicant);
        session.setCreatedAt(LocalDateTime.now());

        List<InterviewData> interviewDataList = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            InterviewData data = new InterviewData();
            data.setSession(session);
            data.setQuestionNumber(i + 1);
            data.setQuestionText(questions.get(i));
            interviewDataList.add(data);
        }
        session.setInterviewDataList(interviewDataList);

        sessionRepository.save(session);

        return new QuestionResponseDTO(session.getSessionId().toString(), 1, questions.get(0), false);
    }

    // Submit answer and get next question or score
    public QuestionResponseDTO submitAnswer(String sessionIdStr, int questionNumber, String answer) {
        UUID sessionId = UUID.fromString(sessionIdStr);
        InterviewSession session = sessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Invalid sessionId"));

        InterviewData data = session.getInterviewDataList().stream().filter(d -> d.getQuestionNumber() == questionNumber).findFirst().orElseThrow(() -> new RuntimeException("Question not found"));

        data.setAnswerText(answer);
        dataRepository.save(data);

        int nextQuestionNumber = questionNumber + 1;

        if (nextQuestionNumber > session.getInterviewDataList().size()) {
            return completeInterview(session, sessionIdStr);
        } else {
            String nextQuestion = session.getInterviewDataList().stream()
                    .filter(d -> d.getQuestionNumber() == nextQuestionNumber)
                    .findFirst()
                    .map(InterviewData::getQuestionText)
                    .orElseThrow(() -> new RuntimeException("Next question not found"));

            return new QuestionResponseDTO(sessionIdStr, nextQuestionNumber, nextQuestion, false);
        }
    }

    private List<String> callGemini(String prompt) {
        try {
            Map<String, Object> content = Map.of("contents",
                List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(content)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray candidates = json.getAsJsonArray("candidates");
            JsonObject parts = candidates.get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0)
                    .getAsJsonObject();

            String text = parts.get("text").getAsString();

            return Arrays.stream(text.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return List.of("Gemini API Error: " + e.getMessage());
        }
    }

    
    private QuestionResponseDTO completeInterview(InterviewSession session, String sessionIdStr) {
        Map<String, String> feedbackMap = new LinkedHashMap<>();

        for (InterviewData d : session.getInterviewDataList()) {
        	String prompt = "Evaluate this technical answer in 3 lines:\n" +
                    "1. Briefly evaluate the answer.\n" +
                    "2. What did the candidate include that contributes to a good score?\n" +
                    "3. What suggestions do you have for improving the answer?\n" +
                    "Q: " + d.getQuestionText() + "\n" +
                    "A: " + d.getAnswerText();

            String feedback = callGemini(prompt).get(0);
            d.setFeedback(feedback);

            // Add entry like "q1 feedback": "..."
            feedbackMap.put("Analysis" + d.getQuestionNumber() + " :", feedback);
        }

        dataRepository.saveAll(session.getInterviewDataList());

        String overallPrompt = buildOverallPrompt(session);
        List<String> resultLines = callGemini(overallPrompt);

        String overallFeedback = "";
        int score = 0;

        for (String line : resultLines) {
            if (line.toLowerCase().startsWith("feedback:")) {
                overallFeedback = line.substring("feedback:".length()).trim();
            } else if (line.toLowerCase().startsWith("score:")) {
                try {
                    score = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    score = 0;
                }
            }
        }

        session.setOverallFeedback(overallFeedback);
        session.setScore(score);
        sessionRepository.save(session);

        // Use the updated constructor
        return new QuestionResponseDTO(sessionIdStr, true, feedbackMap, overallFeedback, score);
    }


    private String buildOverallPrompt(InterviewSession session) {
        StringBuilder sb = new StringBuilder("You are an AI interviewer. Given the following Q&A pairs:\n");

        for (InterviewData d : session.getInterviewDataList()) {
            sb.append("Q: ").append(d.getQuestionText()).append("\n")
              .append("A: ").append(d.getAnswerText()).append("\n");
        }

        sb.append("Now provide an overall feedback and a score (out of 100).\n")
          .append("Format:\nFeedback: <your feedback>\nScore: <numeric score>");

        return sb.toString();
    }
    

    private String buildPrompt(List<String> skills) {
        return "You are an AI interviewer.\nCandidate skills: " + skills +
                "\nGenerate " + TOTAL_QUESTIONS + " technical interview questions. Respond ONLY with the questions, numbered from 1 to " + TOTAL_QUESTIONS + ", each on a new line.";
    }
}
