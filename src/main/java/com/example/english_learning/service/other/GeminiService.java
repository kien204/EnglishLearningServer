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

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ToEntityService toEntityService;

    public Map<String, Object> gradeContent(Long exerciseId, String transcript) {
        // 1. Chuẩn bị câu hỏi và Prompt (Rất ngắn gọn)
        Exercise exercise = toEntityService.getExercise(exerciseId);
        String title = (exercise.getTitle() != null) ? "Topic: " + exercise.getTitle() : "";

        // Prompt không cần hướng dẫn format nữa, chỉ cần logic chấm điểm
        String prompt = """
                Role: English Evaluation Assistant.
                Task: Evaluate student answer based on the question.
                
                Question: "%s"
                Student Answer: "%s"
                
                Scoring Rules (0.0 - 10.0):
                1. Standard Criteria: Grammar, Vocabulary, Coherence.
                2. Relevance (CRITICAL):
                   - Completely off-topic -> Score 0-1.
                   - Weak link/General -> Score 2-5.
                   - On topic but missing details -> Score 6-8.
                   - Perfect -> Score 9-10.
                
                Output Requirements:
                - 'feedback': MUST be in VIETNAMESE. Include mistakes, fixes, and advice. Use "\\n" for line breaks.
                - 'corrected_version': Fluent natural English, keeping the meaning.
                """.formatted(title, transcript);

        // 2. ĐỊNH NGHĨA SCHEMA (Cái khuôn đúc)
        // Cấu trúc: Root (Object) -> Properties -> Các trường con
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "OBJECT");

        Map<String, Object> properties = new HashMap<>();

        // Định nghĩa từng trường dữ liệu bạn muốn nhận
        properties.put("grammar_score", Map.of("type", "NUMBER"));
        properties.put("vocab_score", Map.of("type", "NUMBER"));
        properties.put("coherence_score", Map.of("type", "NUMBER"));
        properties.put("relevance_score", Map.of("type", "NUMBER"));
        properties.put("feedback", Map.of("type", "STRING"));
        properties.put("corrected_version", Map.of("type", "STRING"));

        schema.put("properties", properties);
        // Bắt buộc AI phải trả về đủ các trường này, không được thiếu
        schema.put("required", Arrays.asList(
                "grammar_score", "vocab_score", "coherence_score",
                "relevance_score", "feedback", "corrected_version"
        ));

        // 3. Cấu hình Request Body
        Map<String, Object> requestBody = new HashMap<>();

        // A. Nội dung Prompt
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> contentBlock = new HashMap<>();
        contentBlock.put("parts", Collections.singletonList(part));
        requestBody.put("contents", Collections.singletonList(contentBlock));

        // B. Cấu hình Generation Config (Kèm Schema)
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseMimeType", "application/json"); // Bắt buộc
        generationConfig.put("responseSchema", schema);               // Gắn cái khuôn vào đây
        generationConfig.put("temperature", 0.2);                     // Giảm sáng tạo

        requestBody.put("generationConfig", generationConfig);

        // 4. Gửi Request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Lưu ý: Dùng model flash để tiết kiệm và nhanh
        String url = GEMINI_URL;

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // 5. Xử lý kết quả (Không cần replace markdown nữa!)
            Map<String, Object> respBody = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) respBody.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> partsRes = (List<Map<String, Object>>) content.get("parts");

            String cleanJson = (String) partsRes.get(0).get("text");

            // Parse trực tiếp
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(cleanJson, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi chấm điểm AI: " + e.getMessage());
        }
    }
}