package com.talentstream.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.google.gson.*;
import com.talentstream.dto.EvaluateResponseDTO;
import com.talentstream.dto.InterviewResponseDTO;
import java.net.URI;
import java.net.http.*;
import java.util.*;

@Service
public class InterviewService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // Generate beginner-level programming questions
    public ResponseEntity<InterviewResponseDTO> generateQuestions(String name, String skill) {
        String prompt = "Generate 3 simple beginner-level programming questions for a candidate skilled in " 
            + skill 
            + ". The questions should be easy to understand and implement, like: 'Write a Java method named addNumbers that takes two integers as input and returns their sum.' Format questions clearly and no extra explanation.";
        
        List<String> questions = callGemini(prompt);

        InterviewResponseDTO response = new InterviewResponseDTO(questions);
        return ResponseEntity.ok(response);
    }

    // Evaluate candidate's answers with short feedback and a score out of 100
    public ResponseEntity<EvaluateResponseDTO> evaluateAnswers(String name, String skill, List<String> questions, List<String> answers) {
        List<String> feedback = new ArrayList<>();
        int totalScore = 0;

        for (int i = 0; i < answers.size(); i++) {
            String prompt = "You are evaluating a beginner-level " + skill + " interview answer.\n"
                + "Question: \"" + questions.get(i) + "\"\n"
                + "Candidate's Answer: \"" + answers.get(i) + "\"\n"
                + "Provide short feedback specific to this question and answer.\n"
                + "Then on a new line, write 'Score: X' where X is a number between 0 and 100.";

            List<String> response = callGemini(prompt);

            // Build feedback string excluding the score line
            StringBuilder feedbackBuilder = new StringBuilder();
            for (String line : response) {
                if (!line.trim().toLowerCase().startsWith("score:")) {
                    feedbackBuilder.append(line).append(" ");
                }
            }

            String feedbackText = feedbackBuilder.toString().trim();
            feedback.add("Q" + (i + 1) + ": " + feedbackText);

            int score = extractScore(String.join("\n", response));
            System.out.println("Extracted score for Q" + (i + 1) + ": " + score);
            totalScore += score;
        }

        int averageScore = answers.size() > 0 ? totalScore / answers.size() : 0;
        EvaluateResponseDTO evaluationResponse = new EvaluateResponseDTO(feedback, averageScore);

        return ResponseEntity.ok(evaluationResponse);
    }

    // Call Gemini API and parse the result
    private List<String> callGemini(String prompt) {
        try {
            Map<String, Object> content = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                )
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(content)))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray candidates = json.getAsJsonArray("candidates");
            JsonObject parts = candidates.get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject();

            String text = parts.get("text").getAsString();
            return Arrays.asList(text.split("\n"));
        } catch (Exception e) {
            return List.of("Gemini API Error: " + e.getMessage());
        }
    }

    // Extract score from feedback string
    private int extractScore(String feedback) {
        try {
            for (String line : feedback.split("\n")) {
                line = line.trim();
                if (line.toLowerCase().startsWith("score:")) {
                    String scoreStr = line.substring(6).trim(); // after "Score:"
                    int score = Integer.parseInt(scoreStr);
                    if (score >= 0 && score <= 100) {
                        System.out.println("Extracted score from line: " + score);
                        return score;
                    }
                }
            }
        } catch (Exception ignored) {}
        System.out.println("No valid score found, returning default 60");
        return 60;
    }
}
