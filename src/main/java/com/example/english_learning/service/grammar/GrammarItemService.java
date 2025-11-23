package com.example.english_learning.service.grammar;

import com.example.english_learning.dto.request.grammar.GrammarItemRequest;
import com.example.english_learning.mapper.GrammarItemMapper;
import com.example.english_learning.models.GrammarCategory;
import com.example.english_learning.models.GrammarItem;
import com.example.english_learning.repository.grammar.GrammarCategoryRepository;
import com.example.english_learning.repository.grammar.GrammarItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GrammarItemService {

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private GrammarCategoryRepository grammarCategoryRepository;
    @Autowired
    private GrammarItemRepository grammarItemRepository;
    @Autowired
    private GrammarItemMapper grammarItemMapper;

    public ResponseEntity<?> create(GrammarItemRequest request) {
        GrammarCategory category = grammarCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy category id = " + request.getCategoryId()
                ));

        GrammarItem item = grammarItemMapper.toEntity(request);
        item.setCategory(category);

        grammarItemRepository.save(item);

        return ResponseEntity.ok("Thêm thành công ngữ pháp.");
    }

    public ResponseEntity<?> createWithJson(List<GrammarItemRequest> requests) {
        List<GrammarItem> categoryList = new ArrayList<>();
        for (GrammarItemRequest request : requests) {
            GrammarItem item = grammarItemMapper.toEntity(request);
            GrammarCategory category = grammarCategoryRepository.findById(request.getCategoryId()).orElse(null);
            item.setCategory(category);
            categoryList.add(item);
        }
        grammarItemRepository.saveAll(categoryList);

        return ResponseEntity.ok("Thêm ngữ pháp thành công");
    }

    public ResponseEntity<?> update(Long id, GrammarItemRequest request) {
        GrammarCategory category = grammarCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy category id = " + request.getCategoryId()
                ));

        if (!grammarItemRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy ngữ pháp cần sửa."
            );
        }
        GrammarItem grammarItem = grammarItemMapper.toEntity(request);
        grammarItem.setId(id);
        grammarItem.setCategory(category);

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

    public ResponseEntity<?> importCsv(MultipartFile file) {
        List<String> skipped = new ArrayList<>();
        int success = 0;

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
        )) {

            String[] d;
            int row = 0;

            while ((d = reader.readNext()) != null) {
                row++;

                // Bỏ header
                if (row == 1) continue;

                if (d.length < 8) {
                    skipped.add("Dòng " + row + " thiếu dữ liệu → bỏ qua");
                    continue;
                }

                Long categoryId;
                try {
                    categoryId = Long.valueOf(d[1].trim());
                } catch (Exception e) {
                    skipped.add("Dòng " + row + " lỗi category_id → " + d[1]);
                    continue;
                }

                Optional<GrammarCategory> categoryOpt = grammarCategoryRepository.findById(categoryId);
                if (categoryOpt.isEmpty()) {
                    skipped.add("Dòng " + row + ": category_id=" + categoryId + " không tồn tại → bỏ qua");
                    continue;
                }

                GrammarItem item = GrammarItem.builder()
                        .category(categoryOpt.get())
                        .title(d[2].trim())
                        .structure(d[3].trim())
                        .explanation(d[4].trim())
                        .example(d[5].trim())
                        .tip(d[6].trim())
                        .imageUrl(d[7].trim())
                        .build();

                grammarItemRepository.save(item);
                success++;
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi xử lý CSV: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
                "success", success,
                "skipped", skipped.size(),
                "skipped_detail", skipped
        ));
    }

    public ResponseEntity<?> importXlsx(MultipartFile file) {
        List<String> skipped = new ArrayList<>();
        int success = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;

            for (Row row : sheet) {
                rowNum++;

                if (rowNum == 1) continue; // bỏ header

                if (row.getPhysicalNumberOfCells() < 7) {
                    skipped.add("Dòng " + rowNum + " thiếu dữ liệu → bỏ qua");
                    continue;
                }

                Long categoryId = Long.valueOf(getCell(row, 1));
                String title = getCell(row, 2);
                String structure = getCell(row, 3);
                String explanation = getCell(row, 4);
                String example = getCell(row, 5);
                String tip = getCell(row, 6);
                String imageUrl = getCell(row, 7);

                Optional<GrammarCategory> categoryOpt = grammarCategoryRepository.findById(categoryId);

                if (categoryOpt.isEmpty()) {
                    skipped.add("Dòng " + rowNum + ": categoryId=" + categoryId + " không tồn tại → bỏ qua");
                    continue;
                }

                GrammarItem item = GrammarItem.builder()
                        .category(categoryOpt.get())
                        .title(title)
                        .structure(structure)
                        .explanation(explanation)
                        .example(example)
                        .tip(tip)
                        .imageUrl(imageUrl)
                        .build();

                grammarItemRepository.save(item);
                success++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File XLSX không hợp lệ");
        }

        return ResponseEntity.ok(Map.of(
                "success", success,
                "skipped", skipped.size(),
                "skipped_detail", skipped
        ));
    }

    public ResponseEntity<?> importFromJson(MultipartFile itemFile) throws IOException {
        List<Map<String, Object>> vocabList = objectMapper.readValue(
                itemFile.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        for (Map<String, Object> data : vocabList) {
            GrammarItem item = new GrammarItem();
            item.setTitle((String) data.get("title"));
            item.setStructure((String) data.get("structure"));
            item.setExplanation((String) data.get("explanation"));
            item.setImageUrl((String) data.get("imageUrl"));
            item.setTip((String) data.get("tip"));
            item.setExample((String) data.get("example"));
            Long id = Long.parseLong(data.get("categoryId").toString());

            GrammarCategory grammarCategory = grammarCategoryRepository.findById(id).orElse(null);

            item.setCategory(grammarCategory);

            grammarItemRepository.save(item);
        }

        return ResponseEntity.ok("Thêm danh sách Ngữ pháp thành công");
    }

    public ResponseEntity<?> getAlḷ() {
        return ResponseEntity.ok(grammarItemRepository.findAll());
    }

    private String getCell(Row row, int column) {
        Cell cell = row.getCell(column);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
