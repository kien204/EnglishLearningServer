package com.example.english_learning.service;

import com.example.english_learning.dto.request.TopicRequest;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.TopicRepository;
import com.example.english_learning.repository.VocabularyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ToEntityService toEntityService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private LevelService levelService;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    // -------------------- CRUD --------------------
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(topicRepository.findAll());
    }

    public ResponseEntity<?> getBySkillAndLevel(Long skillId, Long levelId) {
        return ResponseEntity.ok(topicRepository.findBySkill_IdAndLevel_Id(skillId, levelId));
    }

    public ResponseEntity<?> getBySkill(Long skillId) {
        return ResponseEntity.ok(topicRepository.findBySkill_Id(skillId));
    }

    public Topic getById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chủ đề."));
    }

    public ResponseEntity<?> createTopic(TopicRequest req) {
        Topic topic = new Topic();
        topic.setName(req.getName());
        topic.setDescription(req.getDescription());
        topic.setSkill(skillService.getById(req.getSkillId()));
        topic.setLevel(levelService.getById(req.getLevelId()));
        topicRepository.save(topic);
        return ResponseEntity.ok("Tạo Chủ đề thành công.");
    }

    public ResponseEntity<?> updateTopic(Long id, TopicRequest req) {
        Topic topic = getById(id);
        topic.setName(req.getName());
        topic.setDescription(req.getDescription());
        topic.setSkill(skillService.getById(req.getSkillId()));
        topic.setLevel(levelService.getById(req.getLevelId()));
        topicRepository.save(topic);
        return ResponseEntity.ok("Cập nhật Chủ đề thành công.");
    }

    public ResponseEntity<?> deleteById(Long id) {
        Topic topic = getById(id);
        topicRepository.delete(topic);
        return ResponseEntity.ok("Xóa Chủ đề thành công.");
    }

    public ResponseEntity<?> deleteAll() {
        topicRepository.deleteAll();
        return ResponseEntity.ok("Xóa tất cả Chủ đề thành công.");
    }

    // -------------------- Import --------------------
    public void importFromCsv(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    if (line.toLowerCase().contains("description")) {
                        isFirstLine = false;
                        continue;
                    }
                    isFirstLine = false;
                }

                String[] data = line.split("[,;|]", -1);
                if (data.length < 4) continue;

                Topic topic = new Topic();
                topic.setSkill(toEntityService.getSkill(Long.parseLong(data[0].trim())));
                topic.setLevel(toEntityService.getLevel(Long.parseLong(data[1].trim())));
                topic.setName(data[2].trim());
                topic.setDescription(data[3].trim());

                topicRepository.save(topic);
            }
        }
    }

    public void importFromXlsx(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (isFirstRow) {
                    Cell headerCell = row.getCell(0);
                    if (headerCell != null && headerCell.getStringCellValue().toLowerCase().contains("description")) {
                        isFirstRow = false;
                        continue;
                    }
                    isFirstRow = false;
                }

                if (row.getPhysicalNumberOfCells() < 4) continue;

                Topic topic = new Topic();
                topic.setSkill(toEntityService.getSkill(Long.parseLong(getCellValue(row.getCell(0)))));
                topic.setLevel(toEntityService.getLevel(Long.parseLong(getCellValue(row.getCell(1)))));
                topic.setName(getCellValue(row.getCell(2)));
                topic.setDescription(getCellValue(row.getCell(3)));

                topicRepository.save(topic);
            }
        }
    }

    public void importFromJson(MultipartFile topicFile) throws IOException {
        List<Map<String, Object>> list = objectMapper.readValue(
                topicFile.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        for (Map<String, Object> data : list) {
            Topic topic = new Topic();
            topic.setName(data.get("name").toString());
            topic.setDescription(data.get("description").toString());
            topic.setSkill(toEntityService.getSkill(Long.parseLong(data.get("skillId").toString())));
            topic.setLevel(toEntityService.getLevel(Long.parseLong(data.get("levelId").toString())));

            topicRepository.save(topic);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getRichStringCellValue().getString().trim();
            default:
                return "";
        }
    }
}
