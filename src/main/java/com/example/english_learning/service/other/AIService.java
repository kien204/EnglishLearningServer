package com.example.english_learning.service.other;

import com.example.english_learning.models.Exercise;
import com.example.english_learning.service.ToEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ToEntityService toEntityService;

    public Map<String, Object> gradeContent(Long exerciseId, String transcript) {

        // URL của Groq (Tuân theo chuẩn OpenAI Chat Completions)
        String API_URL = "https://api.groq.com/openai/v1/chat/completions";

        Exercise exercise = toEntityService.getExercise(exerciseId);
        String title = (exercise.getTitle() != null) ? "Topic: " + exercise.getTitle() : "";

        // Prompt giữ nguyên logic, nhưng thêm chỉ dẫn rõ ràng hơn về JSON cho Llama 3
        String safeTranscript = transcript.replace("\"", "\\\"");
        String prompt = """
                You are a strict but helpful English writing tutor.
                
                Topic/Question: "%s"
                Student Answer: "%s"
                
                Task 0: Check if the student answer is relevant to the topic.
                - If the answer does not address the topic, assign a low relevance_score (0-2) and note clearly in the feedback.
                - If the answer partially addresses the topic, assign a medium relevance_score (3-6).
                - If the answer fully addresses the topic, assign a high relevance_score (7-10).
                
                Task 1: Analyze the writing and score from 0 to 10 for:
                - grammar
                - vocabulary
                - coherence
                - relevance (from Task 0)
                
                Task 2: Create a "corrected_version" in natural, advanced (C1 level) English.
                
                Task 3: Write a detailed "feedback" in Vietnamese.
                - Include a note about how relevant the answer is to the topic.
                - Use the following format:
                
                - **Nhận xét chung:** [Tóm tắt điểm mạnh/yếu, nêu rõ nếu lạc đề]
                - **Phân tích từ vựng (Vocabulary):** [List specific words the student used -> Better words used in the corrected version. Explain why the new word is better.]
                - **Phân tích ngữ pháp & Cấu trúc (Grammar):** [Point out specific errors or awkward sentences -> How to fix them. Explain the rule.]
                
                OUTPUT FORMAT:
                Return ONLY a valid JSON object:
                {
                  "grammar_score": number,
                  "vocab_score": number,
                  "coherence_score": number,
                  "relevance_score": number,
                  "feedback": "string",
                  "corrected_version": "string"
                }
                """.formatted(title, safeTranscript);


        // Build Request Body theo chuẩn OpenAI (mà Groq sử dụng)
        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile", // Model rất mạnh và free hiện tại
                "messages", List.of(
                        // System message giúp model định hình format JSON tốt hơn
                        Map.of("role", "system", "content", "You are a helpful assistant designed to output JSON."),
                        Map.of("role", "user", "content", prompt)
                ),
                // Quan trọng: Bật JSON Mode để đảm bảo không bao giờ bị lỗi parsing
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.3 // Giảm nhiệt độ để chấm điểm ổn định hơn
        );

        // Headers (Groq dùng Bearer Token, không dùng query param như Google)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_URL,
                    new HttpEntity<>(requestBody, headers),
                    Map.class
            );

            Map<String, Object> respBody = response.getBody();

            // Parse Response theo chuẩn OpenAI: choices[0].message.content
            List<Map<String, Object>> choices = (List<Map<String, Object>>) respBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String cleanJson = (String) message.get("content");

            // Parse chuỗi JSON thành Map
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(cleanJson, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: Có thể trả về map lỗi hoặc throw tiếp
            throw new RuntimeException("Lỗi chấm điểm AI (Groq): " + e.getMessage());
        }
    }
}
