package com.example.english_learning.service;

import com.example.english_learning.dto.request.GrammarRequest;
import com.example.english_learning.mapper.GrammarMapper;
import com.example.english_learning.models.Grammar;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.GrammarRepository;
import com.example.english_learning.repository.TopicRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GrammarService {

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private GrammarRepository grammarItemRepository;
    @Autowired
    private GrammarMapper grammarItemMapper;
    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicRepository topicRepository;


    public ResponseEntity<?> create(GrammarRequest request) {
        Topic topic = topicService.getById(request.getTopicId());

        Grammar item = grammarItemMapper.toEntity(request);
        item.setTopic(topic);

        grammarItemRepository.save(item);

        return ResponseEntity.ok("Thêm thành công ngữ pháp.");
    }

    public ResponseEntity<?> createWithJson(List<GrammarRequest> requests) {
        List<Grammar> categoryList = new ArrayList<>();
        for (GrammarRequest request : requests) {
            Grammar item = grammarItemMapper.toEntity(request);
            Topic topic = topicRepository.findById(request.getTopicId()).orElse(null);
            item.setTopic(topic);
            categoryList.add(item);
        }
        grammarItemRepository.saveAll(categoryList);

        return ResponseEntity.ok("Thêm ngữ pháp thành công");
    }

    public ResponseEntity<?> update(Long id, GrammarRequest request) {
        Topic topic = topicService.getById(request.getTopicId());

        if (!grammarItemRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy ngữ pháp cần sửa."
            );
        }
        Grammar grammarItem = grammarItemMapper.toEntity(request);
        grammarItem.setId(id);
        grammarItem.setTopic(topic);

        grammarItemRepository.save(grammarItem);

        return ResponseEntity.ok("Cập nhật ngữ pháp thành công.");
    }

    public ResponseEntity<?> delete(Long id) {
        if (!grammarItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ngữ pháp cần xóa.");
        }

        grammarItemRepository.deleteById(id);
        return ResponseEntity.ok("Xóa ngữ pháp thành công.");
    }

    public ResponseEntity<?> deleteAll() {
        grammarItemRepository.deleteAll();
        return ResponseEntity.ok("Xoá tất cả ngữ pháp thành công");
    }

    public ResponseEntity<?> importFromJson(MultipartFile itemFile) throws IOException {
        List<Map<String, Object>> vocabList = objectMapper.readValue(
                itemFile.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        for (Map<String, Object> data : vocabList) {
            Grammar item = new Grammar();
            item.setTitle((String) data.get("title"));
            item.setStructure((String) data.get("structure"));
            item.setExplanation((String) data.get("explanation"));
            item.setImageUrl((String) data.get("imageUrl"));
            item.setTip((String) data.get("tip"));
            item.setExample((String) data.get("example"));
            Long id = Long.parseLong(data.get("topicId").toString());

            Topic topic = topicRepository.findById(id).orElse(null);

            item.setTopic(topic);

            grammarItemRepository.save(item);
        }

        return ResponseEntity.ok("Thêm danh sách Ngữ pháp thành công");
    }

    public ResponseEntity<?> getAlḷ() {
        return ResponseEntity.ok(grammarItemRepository.findAll());
    }

    public ResponseEntity<?> getByTopicId(Long id) {
        return ResponseEntity.ok(grammarItemRepository.findAllByTopicId(id));
    }
}
