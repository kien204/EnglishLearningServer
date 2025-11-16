package com.example.english_learning.service;

import com.example.english_learning.dto.request.VocabularyRequest;
import com.example.english_learning.mapper.VocabularyMapper;
import com.example.english_learning.models.Vocabulary;
import com.example.english_learning.repository.VocabularyRepossitory;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class VocabularyService {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private VocabularyRepossitory vocabularyRepossitory;

    public String createVocabulary(VocabularyRequest vocabularyRequest) {
        Vocabulary vocabulary = vocabularyMapper.toEntity(vocabularyRequest);

        vocabularyRepossitory.save(vocabulary);

        return "Thêm từ vựng thành công";

    }

    public void importFromCsv(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Bỏ dòng tiêu đề nếu có
                if (isFirstLine) {
                    // nếu file có header "word,part_of_speech..." thì bỏ qua
                    if (line.toLowerCase().contains("word")) {
                        isFirstLine = false;
                        continue;
                    }
                    isFirstLine = false;
                }

                // Cắt theo dấu phẩy, giữ cả cột trống
                String[] data = line.split("[,;|]", -1);

                // Kiểm tra số cột
                if (data.length < 8) continue;

                Vocabulary vocab = new Vocabulary();
                vocab.setWord(data[0].trim());
                vocab.setWordType(data[1].trim());
                vocab.setPhonetic(data[2].trim());
                vocab.setMeaning(data[3].trim());
                vocab.setV2(data[4].trim());
                vocab.setV3(data[5].trim());
                vocab.setLevel(data[6].trim());
                vocab.setTopic(data[7].trim());

                vocabularyRepossitory.save(vocab);
            }
        }
    }

    public void importFromXlsx(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0); // đọc sheet đầu tiên
            Iterator<Row> rowIterator = sheet.iterator();

            boolean isFirstRow = true;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Bỏ header nếu có chữ "word"
                if (isFirstRow) {
                    Cell headerCell = row.getCell(0);
                    if (headerCell != null
                            && headerCell.getStringCellValue().toLowerCase().contains("word")) {
                        isFirstRow = false;
                        continue;
                    }
                    isFirstRow = false;
                }

                // Đảm bảo đủ 8 cột
                if (row.getPhysicalNumberOfCells() < 8) continue;

                Vocabulary vocab = new Vocabulary();
                vocab.setWord(getCellValue(row.getCell(0)));
                vocab.setWordType(getCellValue(row.getCell(1)));
                vocab.setPhonetic(getCellValue(row.getCell(2)));
                vocab.setMeaning(getCellValue(row.getCell(3)));
                vocab.setV2(getCellValue(row.getCell(4)));
                vocab.setV3(getCellValue(row.getCell(5)));
                vocab.setLevel(getCellValue(row.getCell(6)));
                vocab.setTopic(getCellValue(row.getCell(7)));

                vocabularyRepossitory.save(vocab);
            }
        }
    }

    public Object updateVocabulary(Long id, VocabularyRequest vocabularyRequest) {
        Vocabulary vocabulary = vocabularyRepossitory.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy từ vựng id = " + id
                ));


        vocabulary.setWord(vocabularyRequest.getWord());
        vocabulary.setWordType(vocabularyRequest.getWordType());
        vocabulary.setPhonetic(vocabularyRequest.getPhonetic());
        vocabulary.setMeaning(vocabularyRequest.getMeaning());
        vocabulary.setV2(vocabularyRequest.getV2());
        vocabulary.setV3(vocabularyRequest.getV3());
        vocabulary.setLevel(vocabularyRequest.getLevel());
        vocabulary.setTopic(vocabularyRequest.getTopic());
        vocabulary.setExample(vocabularyRequest.getExample());

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

}
