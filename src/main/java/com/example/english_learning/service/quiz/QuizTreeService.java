package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.QuizTreeRequest;
import com.example.english_learning.dto.response.QuizTreeResponse;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.Question;
import com.example.english_learning.models.QuestionOption;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.repository.quiz.QuestionOptionRepository;
import com.example.english_learning.repository.quiz.QuestionRepository;
import com.example.english_learning.service.ToEntityService;
import com.example.english_learning.service.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class QuizTreeService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ToEntityService quizEntityService;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionOptionRepository questionOptionRepository;
    @Autowired
    private TopicService topicService;

    /**
     * ---------------- GET QUIZ TREE ----------------
     */
    public List<QuizTreeResponse> getQuizTree(Long topicId) {
        Topic topic = topicService.getById(topicId);

        if (topic.getSkillId() == 2) {
            List<Exercise> exercises = exerciseRepository.findByTopic(topic);
            QuizTreeResponse mergedResponse = mapExercisesToMergedResponse(exercises);

            // Trả về List chứa 1 phần tử duy nhất (là bài đã gộp)
            return List.of(mergedResponse);
        } else {

            List<Exercise> exercises = exerciseRepository.findByTopic(topic);

            return exercises.stream()
                    .map(this::mapExerciseToResponse)
                    .toList();
        }
    }

    public List<QuizTreeResponse> getQuizByGroupWord(int groupWord) {
        List<Exercise> exercises = exerciseRepository.findByGroupWord(groupWord);

        return exercises.stream()
                .map(this::mapExerciseToResponse)
                .toList();
    }

    /**
     * ---------------- SUBMIT QUIZ TREE ----------------
     */
//    public List<ResultSubmitQuiz> submitQuizs(List<SubmitQuizRequest> submitQuizRequests) {
//
//    }


    /**
     * ---------------- CREATE QUIZ TREE ----------------
     */
    public void uploadQuizFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }

        ObjectMapper mapper = new ObjectMapper();
        List<QuizTreeRequest> requestList =
                Arrays.asList(mapper.readValue(file.getInputStream(), QuizTreeRequest[].class));

        for (QuizTreeRequest request : requestList) {
            saveQuizTree(request);
        }
    }

    public ResponseEntity<?> createQuizTree(QuizTreeRequest request) {
        saveQuizTree(request);
        return ResponseEntity.ok("Tạo cây câu hỏi thành công");
    }

    public QuizTreeResponse mapExercisesToMergedResponse(List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            return null;
        }

        QuizTreeResponse resp = new QuizTreeResponse();
        Exercise firstEx = exercises.get(0);

        resp.setExerciseId(firstEx.getId());
        resp.setTopic(firstEx.getTopic().getName());
        resp.setTitle("Tổng hợp: " + firstEx.getTopic().getName());
        resp.setType(firstEx.getType());
        resp.setDescription("Bài tập tổng hợp gồm " + exercises.size() + " phần.");
        resp.setImageUrl(firstEx.getImageUrl());
        resp.setAudioUrl(firstEx.getAudioUrl());

        List<QuizTreeResponse.SubQuestionNode> allQuestionNodes = new ArrayList<>();

        int sl = exercises.size();

        for (Exercise exercise : exercises) {
            List<Question> questions;

            if (sl == 1) {
                questions = questionRepository.findByExercise_Id(exercise.getId());
            } else if (sl == 2) {
                questions = questionRepository.findRandomByExerciseId(exercise.getId(), 15);
            } else {
                questions = questionRepository.findRandomByExerciseId(exercise.getId(), 10);
            }

            List<QuizTreeResponse.SubQuestionNode> nodes = questions.stream().map(q -> {
                QuizTreeResponse.SubQuestionNode qNode = new QuizTreeResponse.SubQuestionNode();
                qNode.setQuestionId(q.getId());
                qNode.setQuestionText(q.getQuestionText());

                List<QuestionOption> options = questionOptionRepository.findByQuestion_Id(q.getId());
                List<QuizTreeResponse.SubQuestionNode.SubOptionNode> optNodes = options.stream().map(o -> {
                    QuizTreeResponse.SubQuestionNode.SubOptionNode opt = new QuizTreeResponse.SubQuestionNode.SubOptionNode();
                    opt.setOptionId(o.getId());
                    opt.setOptionText(o.getOptionText());
                    return opt;
                }).toList();

                qNode.setOptions(optNodes);
                return qNode;
            }).toList();

            allQuestionNodes.addAll(nodes);
        }

        Collections.shuffle(allQuestionNodes);

        resp.setSubQuestionNodes(allQuestionNodes);

        return resp;
    }

    private QuizTreeResponse mapExerciseToResponse(Exercise exercise) {
        QuizTreeResponse resp = new QuizTreeResponse();
        resp.setExerciseId(exercise.getId());
        resp.setTopic(exercise.getTopic().getName());
        resp.setGroupWord(exercise.getGroupWord());
        resp.setTitle(exercise.getTitle());
        resp.setType(exercise.getType());
        resp.setDescription(exercise.getDescription());
        resp.setImageUrl(exercise.getImageUrl());
        resp.setAudioUrl(exercise.getAudioUrl());

        List<Question> questions = questionRepository.findByExercise_Id(exercise.getId());
        List<QuizTreeResponse.SubQuestionNode> questionNodes = questions.stream().map(q -> {
            QuizTreeResponse.SubQuestionNode qNode = new QuizTreeResponse.SubQuestionNode();
            qNode.setQuestionId(q.getId());
            qNode.setQuestionText(q.getQuestionText());

            List<QuestionOption> options = questionOptionRepository.findByQuestion_Id(q.getId());
            List<QuizTreeResponse.SubQuestionNode.SubOptionNode> optNodes = options.stream().map(o -> {
                QuizTreeResponse.SubQuestionNode.SubOptionNode opt = new QuizTreeResponse.SubQuestionNode.SubOptionNode();
                opt.setOptionId(o.getId());
                opt.setOptionText(o.getOptionText());
                return opt;
            }).toList();
            qNode.setOptions(optNodes);
            return qNode;
        }).toList();

        resp.setSubQuestionNodes(questionNodes);
        return resp;
    }

    @Transactional
    private void saveQuizTree(QuizTreeRequest request) {

        Exercise exercise = new Exercise();
        exercise.setTopic(topicService.getById(request.getTopicId()));
        exercise.setGroupWord(request.getGroupWord());
        exercise.setTitle(request.getTitle());
        exercise.setType(request.getType());
        exercise.setDescription(request.getDescription());
        exercise.setImageUrl(request.getImageUrl());
        exercise.setAudioUrl(request.getAudioUrl());
        exerciseRepository.save(exercise);

        if (request.getSubQuestionNodes() != null) {
            request.getSubQuestionNodes()
                    .forEach(node -> {

                        Question q = new Question();
                        q.setQuestionText(node.getQuestionText());
                        q.setVocabulary(quizEntityService.getVocabulary(node.getVocabulary_id()));
                        q.setExercise(exercise);
                        questionRepository.save(q);

                        if (node.getOptions() != null) {
                            List<QuestionOption> options = node.getOptions()
                                    .stream()
                                    .map(optNode -> {
                                        QuestionOption qo = new QuestionOption();
                                        qo.setOptionText(optNode.getOptionText());
                                        qo.setIsCorrect(optNode.getIsCorrect());
                                        qo.setQuestion(q);
                                        return qo;
                                    })
                                    .toList();
                            questionOptionRepository.saveAll(options);
                        }
                    });
        }
    }

    @Transactional
    public void importQuizTreeFromXLSX(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Map<String, Long> exerciseMap = new HashMap<>();
        Map<String, Long> questionMap = new HashMap<>();

        // --- SHEET EXERCISE ---
        Sheet exSheet = workbook.getSheet("Exercise");
        if (exSheet != null) {
            for (int i = 1; i <= exSheet.getLastRowNum(); i++) {
                Row r = exSheet.getRow(i);
                // Kiểm tra dòng null hoặc ô mã code (cột 0) trống thì bỏ qua
                if (r == null || isCellEmpty(r.getCell(0))) continue;

                Exercise ex = new Exercise();

                // Cột 1: Topic ID (An toàn)
                Long topicId = getLongCell(r, 1);
                if (topicId != null) {
                    ex.setTopic(quizEntityService.getTopic(topicId));
                }

                // Cột 2: Group Word (Dùng hàm helper đã có)
                ex.setGroupWord(getIntegerCell(r, 2));

                // Cột 3: Title
                ex.setTitle(getString(r, 3));

                // Cột 4: Type (An toàn hơn ép kiểu trực tiếp)
                Integer typeVal = getIntegerCell(r, 4);
                ex.setType(typeVal != null ? typeVal : 0);

                ex.setDescription(getString(r, 5));
                ex.setImageUrl(getString(r, 6));
                ex.setAudioUrl(getString(r, 7));

                ex = exerciseRepository.save(ex); // Gán lại để lấy ID sau khi save
                exerciseMap.put(getString(r, 0), ex.getId());
            }
        }

        // --- SHEET QUESTION ---
        Sheet qSheet = workbook.getSheet("Question");
        if (qSheet != null) {
            for (int i = 1; i <= qSheet.getLastRowNum(); i++) {
                Row r = qSheet.getRow(i);
                if (r == null || isCellEmpty(r.getCell(0))) continue;

                String exCode = getString(r, 1);
                Long exId = exerciseMap.get(exCode);
                if (exId == null) continue;

                Question q = new Question();
                q.setExercise(exerciseRepository.findById(exId).orElse(null));
                q.setQuestionText(getString(r, 2));

                Long vocaId = getLongCell(r, 3);
                if (vocaId != null) {
                    q.setVocabulary(quizEntityService.getVocabulary(vocaId));
                }

                q = questionRepository.save(q);
                questionMap.put(getString(r, 0), q.getId());
            }
        }

        // --- SHEET OPTION ---
        Sheet opSheet = workbook.getSheet("Option");
        if (opSheet != null) {
            for (int i = 1; i <= opSheet.getLastRowNum(); i++) {
                Row r = opSheet.getRow(i);
                if (r == null || isCellEmpty(r.getCell(0))) continue;

                String qCode = getString(r, 0);
                Long qId = questionMap.get(qCode);
                if (qId == null) continue;

                QuestionOption op = new QuestionOption();
                op.setQuestion(questionRepository.findById(qId).orElse(null));
                op.setOptionText(getString(r, 1));

                // Xử lý Boolean an toàn
                Cell cb = r.getCell(2);
                op.setIsCorrect(cb != null && cb.getCellType() == CellType.BOOLEAN ? cb.getBooleanCellValue() : false);

                questionOptionRepository.save(op);
            }
        }
        workbook.close();
    }

    // --- HÀM BỔ TRỢ ĐỂ CHẶN DÒNG TRỐNG ---
    private boolean isCellEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK ||
                (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty());
    }

    private Long getLongCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        if (cell.getCellType() == CellType.NUMERIC) return (long) cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.parseLong(cell.getStringCellValue().trim());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


    public ByteArrayInputStream exportQuizTreeXLSX(Long id) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet exSheet = workbook.createSheet("Exercise");
        Sheet qSheet = workbook.createSheet("Question");
        Sheet opSheet = workbook.createSheet("Option");

        // ===== HEADER =====
        exSheet.createRow(0);
        String[] exCols = {
                "exerciseCode", "topicId", "groupWord", "title",
                "type", "description", "imageUrl", "audioUrl"
        };
        for (int i = 0; i < exCols.length; i++)
            exSheet.getRow(0).createCell(i).setCellValue(exCols[i]);

        qSheet.createRow(0);
        String[] qCols = {"questionCode", "exerciseCode", "questionText", "vocabulary_id"};
        for (int i = 0; i < qCols.length; i++)
            qSheet.getRow(0).createCell(i).setCellValue(qCols[i]);

        opSheet.createRow(0);
        String[] oCols = {"questionCode", "optionText", "isCorrect"};
        for (int i = 0; i < oCols.length; i++)
            opSheet.getRow(0).createCell(i).setCellValue(oCols[i]);

        int exRow = 1, qRow = 1, oRow = 1;

        // ===== DATA =====
        List<Exercise> exerciseList = new ArrayList<>();
        if (id == 0) {
            exerciseList = exerciseRepository.findAll();
        } else if (id == 1) {
            exerciseList = exerciseRepository.findByTopic_Skill_Id(1L);
        } else if (id == 2) {
            exerciseList = exerciseRepository.findByTopic_Skill_Id(2L);
        } else if (id == 3) {
            exerciseList = exerciseRepository.findByTopic_Skill_Id(3L);
        } else if (id == 4) {
            exerciseList = exerciseRepository.findByTopic_Skill_Id(4L);
        } else if (id == 5) {
            exerciseList = exerciseRepository.findByTopic_Skill_Id(5L);
        } else if (id == 6) {
            exerciseList = exerciseRepository.findByTopic_Skill_Id(6L);
        }


        for (Exercise ex : exerciseList) {

            String exerciseCode = "EX_" + ex.getId();

            // ---- Exercise ----
            Row er = exSheet.createRow(exRow++);
            er.createCell(0).setCellValue(exerciseCode);
            er.createCell(1).setCellValue(ex.getTopic().getId());
            if (ex.getGroupWord() != null)
                er.createCell(2).setCellValue(ex.getGroupWord());
            er.createCell(3).setCellValue(ex.getTitle());
            er.createCell(4).setCellValue(ex.getType());
            er.createCell(5).setCellValue(ex.getDescription());
            er.createCell(6).setCellValue(ex.getImageUrl());
            er.createCell(7).setCellValue(ex.getAudioUrl());

            for (Question q : ex.getQuestions()) {

                String questionCode = "Q_" + q.getId();

                // ---- Question ----
                Row qr = qSheet.createRow(qRow++);
                qr.createCell(0).setCellValue(questionCode);
                qr.createCell(1).setCellValue(exerciseCode);
                qr.createCell(2).setCellValue(q.getQuestionText());
                if (q.getVocabulary() != null) {
                    qr.createCell(3).setCellValue(q.getVocabulary().getId());
                } else {
                    qr.createCell(3).setCellValue(""); // hoặc -1
                }

                for (QuestionOption op : q.getQuestionOptions()) {

                    // ---- Option ----
                    Row or = opSheet.createRow(oRow++);
                    or.createCell(0).setCellValue(questionCode);
                    or.createCell(1).setCellValue(op.getOptionText());
                    or.createCell(2).setCellValue(op.getIsCorrect());
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }


    private String getString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        String value = cell.getStringCellValue();
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private Integer getIntegerCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }

        if (cell.getCellType() == CellType.STRING) {
            String v = cell.getStringCellValue().trim();
            return v.isEmpty() ? null : Integer.parseInt(v);
        }

        return null;
    }

}
