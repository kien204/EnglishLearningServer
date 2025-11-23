package com.example.english_learning.service;

import com.example.english_learning.dto.request.VocabularyRequest;
import com.example.english_learning.mapper.VocabularyMapper;
import com.example.english_learning.models.Level;
import com.example.english_learning.models.Topic;
import com.example.english_learning.models.Vocabulary;
import com.example.english_learning.repository.LevelRepository;
import com.example.english_learning.repository.TopicRepository;
import com.example.english_learning.repository.VocabularyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class VocabularyService {

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private VocabularyMapper vocabularyMapper;
    @Autowired
    private VocabularyRepository vocabularyRepossitory;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TopicRepository topicRepository;

    public String createVocabulary(VocabularyRequest vocabularyRequest) {
        Level level = levelRepository.findById(vocabularyRequest.getLevelId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Level"));
        Topic topic = topicRepository.findById(vocabularyRequest.getTopicId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Topic"));
        Vocabulary vocabulary = vocabularyMapper.toEntity(vocabularyRequest);
        vocabulary.setLevel(level);
        vocabulary.setTopic(topic);

        vocabularyRepossitory.save(vocabulary);

        return "Thêm từ vựng thành công";

    }

    public ResponseEntity<?> importFromJson(MultipartFile vocabularyFile) throws IOException {
        List<Map<String, Object>> vocabList = objectMapper.readValue(
                vocabularyFile.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        for (Map<String, Object> data : vocabList) {
            Vocabulary vocab = new Vocabulary();
            vocab.setWord((String) data.get("word"));
            vocab.setPos((String) data.get("pos"));
            vocab.setPron((String) data.get("pron"));
            vocab.setMeaningVn((String) data.get("meaning_vn"));
            vocab.setV2(data.get("v2") == null || ((String) data.get("v2")).isEmpty() ? null : (String) data.get("v2"));
            vocab.setV3(data.get("v3") == null || ((String) data.get("v3")).isEmpty() ? null : (String) data.get("v3"));
            vocab.setExampleEn((String) data.get("example_en"));
            vocab.setExampleVn((String) data.get("example_vn"));

            // Lấy Level và Topic từ DB
            Long levelId = Long.parseLong(data.get("level_id").toString());
            Long topicId = Long.parseLong(data.get("topic_id").toString());

            Level level = levelRepository.findById(levelId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Level, id=" + levelId));
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Topic, id=" + topicId));

            vocab.setLevel(level);
            vocab.setTopic(topic);

            vocabularyRepossitory.save(vocab);
        }

        return ResponseEntity.ok("Thêm danh sách từ vựng thành công");
    }

    public void importFromCsv(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Bỏ header nếu có
                if (isFirstLine) {
                    if (line.toLowerCase().contains("word")) {
                        isFirstLine = false;
                        continue;
                    }
                    isFirstLine = false;
                }

                List<String> data = new ArrayList<>();
                Matcher m = Pattern.compile("(\"[^\"]*\"|[^,]+)").matcher(line);
                while (m.find()) {
                    String value = m.group(1);
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1); // bỏ dấu ngoặc kép
                    }
                    data.add(value);
                }

                if (data.size() < 10)
                    continue; // 10 cột: word,pos,pron,meaning,v2,v3,level_id,topic_id,example_en,example_vn

                Vocabulary vocab = new Vocabulary();
                vocab.setWord(data.get(0).trim());
                vocab.setPos(data.get(1).trim());
                vocab.setPron(data.get(2).trim());
                vocab.setMeaningVn(data.get(3).trim());
                vocab.setV2(data.get(4).trim().isEmpty() ? null : data.get(4).trim());
                vocab.setV3(data.get(5).trim().isEmpty() ? null : data.get(5).trim());
                vocab.setExampleEn(data.get(8).trim());
                vocab.setExampleVn(data.get(9).trim());

                // fetch Level & Topic từ DB
                Long levelId = Long.parseLong(data.get(6).trim());
                Long topicId = Long.parseLong(data.get(7).trim());

                Level level = levelRepository.findById(levelId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Level, id=" + levelId));
                Topic topic = topicRepository.findById(topicId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Topic, id=" + topicId));

                vocab.setLevel(level);
                vocab.setTopic(topic);

                vocabularyRepossitory.save(vocab);
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
                    if (headerCell != null && headerCell.getStringCellValue().toLowerCase().contains("word")) {
                        isFirstRow = false;
                        continue;
                    }
                    isFirstRow = false;
                }

                if (row.getPhysicalNumberOfCells() < 10) continue;

                Vocabulary vocab = new Vocabulary();
                vocab.setWord(getCellValue(row.getCell(0)));
                vocab.setPos(getCellValue(row.getCell(1)));
                vocab.setPron(getCellValue(row.getCell(2)));
                vocab.setMeaningVn(getCellValue(row.getCell(3)));
                vocab.setV2(getCellValue(row.getCell(4)));
                vocab.setV3(getCellValue(row.getCell(5)));
                vocab.setExampleEn(getCellValue(row.getCell(8)));
                vocab.setExampleVn(getCellValue(row.getCell(9)));

                // fetch Level & Topic từ DB
                Long levelId = Long.parseLong(getCellValue(row.getCell(6)));
                Long topicId = Long.parseLong(getCellValue(row.getCell(7)));

                Level level = levelRepository.findById(levelId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Level, id=" + levelId));
                Topic topic = topicRepository.findById(topicId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Topic, id=" + topicId));


                vocab.setLevel(level);
                vocab.setTopic(topic);

                vocabularyRepossitory.save(vocab);
            }
        }
    }


    public Object updateVocabulary(Long id, VocabularyRequest vocabularyRequest) {
        if (!vocabularyRepossitory.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy từ vựng id = " + id
            );
        }
        Level level = levelRepository.findById(vocabularyRequest.getLevelId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Level"));
        Topic topic = topicRepository.findById(vocabularyRequest.getTopicId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Topic"));

        Vocabulary vocabulary = vocabularyMapper.toEntity(vocabularyRequest);
        vocabulary.setLevel(level);
        vocabulary.setTopic(topic);
        vocabulary.setId(id);

        vocabularyRepossitory.save(vocabulary);

        return Map.of("message", "Sửa từ vựng thành công");

    }

    public List<Vocabulary> getAllVocabulary() {
        return vocabularyRepossitory.findAll();
    }

    public Vocabulary getVocabularyById(long id) {
        if (!vocabularyRepossitory.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy từ vựng id = " + id
            );
        }

        return vocabularyRepossitory.findById(id).get();
    }

    public Vocabulary getVocabularyByWork(String word) {
        if (!vocabularyRepossitory.existsByWord(word)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy từ vựng " + word
            );
        }

        return vocabularyRepossitory.findByWord(word);
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

    public ResponseEntity<?> deleteById(long id) {
        if (!vocabularyRepossitory.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Không tìm thất từ vựng cần xóa");
        }

        vocabularyRepossitory.deleteById(id);

        return ResponseEntity.ok("Xóa thành công");
    }

    public ResponseEntity<?> deleteAll() {


        vocabularyRepossitory.deleteAll();

        return ResponseEntity.ok("Xóa thành công");
    }
}
