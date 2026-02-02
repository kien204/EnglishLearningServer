package com.example.english_learning.service.speech;

import com.example.english_learning.dto.request.TranslateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class PythonService {

    // Địa chỉ của Python Server (Chạy ở cổng 5000)
    private final String PYTHON_URL = "http://127.0.0.1:5000";
    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> translateText(TranslateRequest request) {
        String url = PYTHON_URL + "/translate";

        // 1. Tạo Header báo là gửi JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Tạo Body (Khớp với class TranslationRequest bên Python)
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", request.getText());
        requestBody.put("target_lang", request.getTarget_lang());
        requestBody.put("source_lang", request.getSource_lang()); // Tự động phát hiện ngôn ngữ nguồn

        // 3. Đóng gói request
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 4. Gửi POST và nhận kết quả
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chức năng hiện không thể sử dụng. Vui lòng thử lại sau");
        }
    }

    public Map<String, Object> scoreAudio(MultipartFile file) {
        String url = PYTHON_URL + "/transcribe";
        File tempFile = null;

        try {
            Path tempDir = Files.createTempDirectory("upload_audio_temp");
            tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
            file.transferTo(tempFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            return response.getBody();

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Xử lý file không thành công");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chức năng hiện không thể sử dụng. Vui lòng thử lại sau");
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) System.err.println("Không thể xóa file tạm: " + tempFile.getAbsolutePath());
            }
        }
    }
}