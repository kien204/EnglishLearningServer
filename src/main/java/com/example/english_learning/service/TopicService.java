package com.example.english_learning.service;

import com.example.english_learning.dto.request.TopicRequest;
import com.example.english_learning.models.Level;
import com.example.english_learning.models.Skill;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.LevelRepository;
import com.example.english_learning.repository.SkillRepository;
import com.example.english_learning.repository.TopicRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class TopicService {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private LevelRepository levelRepository;

    public ResponseEntity<?> getAll() {
        List<Topic> topics = topicRepository.findAll();

        return ResponseEntity.ok(topics);
    }

    public ResponseEntity<?> findById(Long id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Không tìm thấy Chủ đề"));
        return ResponseEntity.ok(topic);
    }

    public ResponseEntity<?> createTopic(TopicRequest request) {
        Skill skill = skillRepository.findById(request.getSkillId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Kỹ năng"));
        Level Level = levelRepository.findById(request.getLevelId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm Cấp độ"));

        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        topic.setLevel(Level);
        topic.setSkill(skill);

        topicRepository.save(topic);
        return ResponseEntity.ok(Map.of("message", "Thêm Chủ đề mới thành công"));
    }

    public ResponseEntity<?> updateTopic(Long id, TopicRequest request) {
        Skill skill = skillRepository.findById(request.getSkillId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Kỹ năng"));
        Level Level = levelRepository.findById(request.getLevelId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm Cấp độ"));

        Topic topic = topicRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Không tìm thấy Chủ đề cần cập nhật"));
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        topic.setId(id);
        topic.setLevel(Level);
        topic.setSkill(skill);

        topicRepository.save(topic);
        return ResponseEntity.ok(Map.of("message", "Cập nhật Chủ đề thành công"));
    }

    public ResponseEntity<?> deleteTopic(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Chủ đề cần xóa");
        }

        topicRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "Xóa chủ đề thành công"));
    }

    public ResponseEntity<?> deleteAll() {
        topicRepository.deleteAll();

        return ResponseEntity.ok(Map.of("Message", "Xóa tất cả Chủ đề thành công"));
    }

    public void importFromCsv(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Bỏ header nếu có
                if (isFirstLine) {
                    if (line.toLowerCase().contains("description")) {
                        isFirstLine = false;
                        continue;
                    }
                    isFirstLine = false;
                }

                String[] data = line.split("[,;|]", -1);

                if (data.length < 4)
                    continue;

                Topic topic = new Topic();
                topic.setName(data[2].trim());
                topic.setDescription(data[3].trim());

                // fetch Level & Topic từ DB
                Long levelId = Long.parseLong(data[1].trim());
                Long skillId = Long.parseLong(data[0].trim());

                Level level = levelRepository.findById(levelId).orElse(null);
                Skill skill = skillRepository.findById(skillId).orElse(null);

                topic.setLevel(level);
                topic.setSkill(skill);

                topicRepository.save(topic);
            }
        }
    }


    public void importFromXlsx(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            boolean isFirstRow = true;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (isFirstRow) {
                    Cell headerCell = row.getCell(0);
                    if (headerCell != null && headerCell.getStringCellValue().toLowerCase().contains("description")) {
                        isFirstRow = false;
                        continue;
                    }
                    isFirstRow = false;
                }

                if (row.getPhysicalNumberOfCells() < 10) continue;

                Topic topic = new Topic();
                topic.setName(row.getCell(2).getStringCellValue());
                topic.setDescription(row.getCell(3).getStringCellValue());

                // fetch Level & Topic từ DB
                Long levelId = Long.parseLong(getCellValue(row.getCell(1)));
                Long skillId = Long.parseLong(getCellValue(row.getCell(0)));

                Level level = levelRepository.findById(levelId).orElse(null);
                Skill skill = skillRepository.findById(skillId).orElse(null);

                topic.setLevel(level);
                topic.setSkill(skill);

                topicRepository.save(topic);
            }
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue()).trim();

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                return cell.getRichStringCellValue().getString().trim();

            case BLANK:
            default:
                return "";
        }
    }

    public ResponseEntity<?> importFromJson(MultipartFile topicFile) throws IOException {
        List<Map<String, Object>> vocabList = objectMapper.readValue(
                topicFile.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        for (Map<String, Object> data : vocabList) {
            Topic topic = new Topic();
            topic.setName(data.get("name").toString());
            topic.setDescription(data.get("description").toString());

            // Lấy Level và Topic từ DB
            Long skillId = Long.parseLong(data.get("skillId").toString());
            Long levelId = Long.parseLong(data.get("levelId").toString());

            Level level = levelRepository.findById(levelId).orElse(null);
            Skill skill = skillRepository.findById(skillId).orElse(null);

            topic.setLevel(level);
            topic.setSkill(skill);

            topicRepository.save(topic);
        }

        return ResponseEntity.ok("Thêm danh sách Chủ đề thành công");
    }
}
