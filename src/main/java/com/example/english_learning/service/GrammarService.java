package com.example.english_learning.service;

import com.example.english_learning.dto.request.GrammarRequest;
import com.example.english_learning.mapper.GrammarMapper;
import com.example.english_learning.models.Grammar;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.GrammarRepository;
import com.example.english_learning.repository.TopicRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public ResponseEntity<?> importFromExcel(MultipartFile file) throws IOException {

        List<Integer> dongLoi = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;

            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue; // bỏ header
                }

                if (row.getLastCellNum() < 7) {
                    dongLoi.add(row.getRowNum() + 1);
                    continue;
                }

                boolean success = importExcel(row);
                if (!success) {
                    dongLoi.add(row.getRowNum() + 1);
                }
            }
        }

        if (dongLoi.isEmpty()) {
            return ResponseEntity.ok("Import thành công 100%");
        }

        return ResponseEntity.ok(
                "Import hoàn tất. Các dòng thêm KHÔNG thành công: " + dongLoi
        );
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        List<Grammar> itemList = grammarItemRepository.findAll();

        String[] columns = {
                "topicId",
                "title",
                "structure",
                "explanation",
                "example",
                "tip",
                "imageUrl"
        };

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ngữ pháp");

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                headerRow.createCell(col).setCellValue(columns[col]);
            }

            int rowIdx = 1;
            for (Grammar item : itemList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(
                        item.getTopic() != null ? String.valueOf(item.getTopic().getId()) : ""
                );
                row.createCell(1).setCellValue(item.getTitle());
                row.createCell(2).setCellValue(item.getStructure());
                row.createCell(3).setCellValue(item.getExplanation());
                row.createCell(4).setCellValue(item.getExample());
                row.createCell(5).setCellValue(item.getTip());
                row.createCell(6).setCellValue(item.getImageUrl());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }


    public ResponseEntity<?> getAlḷ() {
        return ResponseEntity.ok(grammarItemRepository.findAll());
    }

    public ResponseEntity<?> getByTopicId(Long id) {
        return ResponseEntity.ok(grammarItemRepository.findAllByTopicId(id));
    }

    private boolean importExcel(Row row) {
        try {
            Long id = (long) row.getCell(0).getNumericCellValue();

            Topic topic = topicRepository.findById(id).orElse(null);
            if (topic == null) {
                return false;
            }

            Grammar item = new Grammar();
            item.setTitle(getStringCell(row, 1));
            item.setStructure(getStringCell(row, 2));
            item.setExplanation(getStringCell(row, 3));
            item.setExample(getStringCell(row, 4));
            item.setTip(getStringCell(row, 5));
            item.setImageUrl(getStringCell(row, 6));
            item.setTopic(topic);


            grammarItemRepository.save(item);
            return true;

        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        String value = cell.getStringCellValue();
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }


}
