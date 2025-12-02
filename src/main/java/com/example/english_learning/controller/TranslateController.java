package com.example.english_learning.controller;

import com.example.english_learning.dto.request.TranslateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/translate")
public class TranslateController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String flaskUrl = "http://127.0.0.1:5000/translate"; // Flask server

    @PostMapping
    public ResponseEntity<?> translate(@RequestBody TranslateRequest payload) {
        try {
            // Gửi request tới Python Flask
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(flaskUrl, payload, Map.class);

            Map<String, Object> result = response.getBody();
            return ResponseEntity.ok(result);

        } catch (ResourceAccessException e) {
            // Lỗi không kết nối được Flask: Flask tắt, sai port, timeout...
            return ResponseEntity.status(503).body(Map.of(
                    "error", "Chức năng dịch tạm không hoạt động. Thử lại sau",
                    "chiTiet", e.getMessage()
            ));

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Flask trả lỗi HTTP (4xx hoặc 5xx)
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", "Chức năng dịch tạm đang có lỗi. Thử lại sau",
                    "chiTiet", e.getResponseBodyAsString()
            ));

        } catch (Exception e) {
            // Các lỗi khác
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Chức năng dịch tạm đang có lỗi. Thử lại sau",
                    "chiTiet", e.getMessage()
            ));
        }
    }


}
