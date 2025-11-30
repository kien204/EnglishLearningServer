package com.example.english_learning.service;

import com.example.english_learning.dto.request.VocabularyRequest;
import com.example.english_learning.mapper.VocabularyMapper;
import com.example.english_learning.models.Vocabulary;
import com.example.english_learning.repository.VocabularyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final VocabularyMapper vocabularyMapper;
    private final ToEntityService toEntityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // -------------------- CRUD --------------------
    public ResponseEntity<?> createVocabulary(VocabularyRequest req) {
        if (vocabularyRepository.existsByWord(req.getWord()) && vocabularyRepository.existsByPos(req.getPos())) {
            throw new RuntimeException("Từ vựng đã tồn tại");
        }

        Vocabulary vocab = vocabularyMapper.toEntity(req);
        vocab.setLevel(toEntityService.getLevel(req.getLevelId()));
        vocab.setTopic(toEntityService.getTopic(req.getTopicId()));

        vocabularyRepository.save(vocab);
        return ResponseEntity.ok("Tạo từ vựng thành công");
    }

    public Vocabulary updateVocabulary(Long id, VocabularyRequest req) {
        Vocabulary vocab = toEntityService.getVocabulary(id);
        vocab.setWord(req.getWord());
        vocab.setPos(req.getPos());
        vocab.setPron(req.getPron());
        vocab.setMeaningVn(req.getMeaningVn());
        vocab.setV2(req.getV2());
        vocab.setV3(req.getV3());
        vocab.setExampleEn(req.getExampleEn());
        vocab.setExampleVn(req.getExampleVn());
        vocab.setGroupWord(req.getGroupWord());
        vocab.setLevel(toEntityService.getLevel(req.getLevelId()));
        vocab.setTopic(toEntityService.getTopic(req.getTopicId()));

        return vocabularyRepository.save(vocab);
    }

    public ResponseEntity<?> deleteById(Long id) {
        Vocabulary vocab = getById(id);
        vocabularyRepository.delete(vocab);
        return ResponseEntity.ok("Đã xóa từ vựng " + vocab.getWord());
    }

    public ResponseEntity<?> deleteAll() {
        vocabularyRepository.deleteAll();
        return ResponseEntity.ok("Đã xóa tất cả từ vựng");
    }

    public List<Vocabulary> getAllVocabulary() {
        return vocabularyRepository.findAll();
    }

    public List<Vocabulary> getByTopic(Long topicId) {
        toEntityService.getTopic(topicId); // kiểm tra tồn tại
        return vocabularyRepository.findByTopic_Id(topicId);
    }

    public Vocabulary getById(Long id) {
        return toEntityService.getVocabulary(id);
    }

    public Vocabulary getByWord(String word) {
        Vocabulary vocab = vocabularyRepository.findByWord(word);
        if (vocab == null) throw new RuntimeException("Không tìm thấy từ vựng " + word);
        return vocab;
    }

    // -------------------- Import --------------------
    public void importFromJson(MultipartFile file) throws IOException {
        List<Map<String, Object>> list = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {
        });
        for (Map<String, Object> data : list) {
            importVocabularyMap(data);
        }
    }

    public void importFromCsv(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    if (line.toLowerCase().contains("word")) continue;
                }
                List<String> cols = new ArrayList<>();
                Matcher m = Pattern.compile("(\"[^\"]*\"|[^,]+)").matcher(line);
                while (m.find()) {
                    String val = m.group(1);
                    if (val.startsWith("\"") && val.endsWith("\"")) val = val.substring(1, val.length() - 1);
                    cols.add(val);
                }
                if (cols.size() < 10) continue;
                importVocabularyCsv(cols);
            }
        }
    }

    public void importFromXlsx(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    if (row.getCell(0) != null && row.getCell(0).getStringCellValue().toLowerCase().contains("word"))
                        continue;
                }
                if (row.getPhysicalNumberOfCells() < 10) continue;
                importVocabularyXlsx(row);
            }
        }
    }

    // -------------------- Helper --------------------
    private void importVocabularyMap(Map<String, Object> data) {
        Vocabulary vocab = new Vocabulary();
        vocab.setWord((String) data.get("word"));
        vocab.setPos((String) data.get("pos"));
        vocab.setPron((String) data.get("pron"));
        vocab.setMeaningVn((String) data.get("meaningVn"));
        vocab.setV2((data.get("v2") == null || ((String) data.get("v2")).isEmpty()) ? null : (String) data.get("v2"));
        vocab.setV3((data.get("v3") == null || ((String) data.get("v3")).isEmpty()) ? null : (String) data.get("v3"));
        vocab.setExampleEn((String) data.get("exampleEn"));
        vocab.setExampleVn((String) data.get("exampleVn"));
        vocab.setGroupWord((Integer) data.get("groupWord"));
        vocab.setLevel(toEntityService.getLevel(getLong(data.get("levelId"))));
        vocab.setTopic(toEntityService.getTopic(getLong(data.get("topicId"))));

        vocabularyRepository.save(vocab);
    }

    private void importVocabularyCsv(List<String> cols) {
        Vocabulary vocab = new Vocabulary();
        vocab.setWord(cols.get(0).trim());
        vocab.setPos(cols.get(1).trim());
        vocab.setPron(cols.get(2).trim());
        vocab.setMeaningVn(cols.get(3).trim());
        vocab.setV2(cols.get(4).trim().isEmpty() ? null : cols.get(4).trim());
        vocab.setV3(cols.get(5).trim().isEmpty() ? null : cols.get(5).trim());
        vocab.setExampleEn(cols.get(8).trim());
        vocab.setExampleVn(cols.get(9).trim());
        vocab.setGroupWord(Integer.parseInt(cols.get(10).trim()));
        vocab.setLevel(toEntityService.getLevel(Long.parseLong(cols.get(6).trim())));
        vocab.setTopic(toEntityService.getTopic(Long.parseLong(cols.get(7).trim())));

        vocabularyRepository.save(vocab);
    }

    private void importVocabularyXlsx(Row row) {
        Vocabulary vocab = new Vocabulary();
        vocab.setWord(getCellValue(row.getCell(0)));
        vocab.setPos(getCellValue(row.getCell(1)));
        vocab.setPron(getCellValue(row.getCell(2)));
        vocab.setMeaningVn(getCellValue(row.getCell(3)));
        vocab.setV2(getCellValue(row.getCell(4)));
        vocab.setV3(getCellValue(row.getCell(5)));
        vocab.setExampleEn(getCellValue(row.getCell(8)));
        vocab.setExampleVn(getCellValue(row.getCell(9)));
        vocab.setGroupWord(Integer.parseInt(getCellValue(row.getCell(10))));
        vocab.setLevel(toEntityService.getLevel(Long.parseLong(getCellValue(row.getCell(6)))));
        vocab.setTopic(toEntityService.getTopic(Long.parseLong(getCellValue(row.getCell(7)))));

        vocabularyRepository.save(vocab);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getRichStringCellValue().getString().trim();
            default -> "";
        };
    }

    private Long getLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

}
