package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.QuizTreeRequest;
import com.example.english_learning.dto.response.QuizTreeResponse;
import com.example.english_learning.models.Exercise;
import com.example.english_learning.models.GroupOption;
import com.example.english_learning.models.Question;
import com.example.english_learning.models.QuestionOption;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.repository.quiz.GroupOptionResponse;
import com.example.english_learning.repository.quiz.QuestionOptionRepository;
import com.example.english_learning.repository.quiz.QuestionRepository;
import com.example.english_learning.service.ToEntityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private GroupOptionResponse groupOptionResponse;

    /**
     * ---------------- GET QUIZ TREE ----------------
     */
    public List<QuizTreeResponse> getQuizTree(Long levelId, Long skillId, Long topicId) {
        List<Exercise> exercises = exerciseRepository.findByLevelIdAndSkillIdAndTopicId(levelId, skillId, topicId);

        return exercises.stream()
                .map(this::mapExerciseToResponse)
                .toList();
    }

    /**
     * ---------------- CREATE QUIZ TREE ----------------
     */
    public ResponseEntity<?> createQuizTree(List<QuizTreeRequest> requestList) {
        for (QuizTreeRequest request : requestList) {
            Exercise exercise = new Exercise();
            exercise.setSkill(quizEntityService.getSkill(request.getSkillId()));
            exercise.setLevel(quizEntityService.getLevel(request.getLevelId()));
            exercise.setTopic(quizEntityService.getTopic(request.getTopicId()));
            exercise.setTitle(request.getTitle());
            exercise.setType(request.getType());
            exercise.setImageUrl(request.getImageUrl());
            exercise.setAudioUrl(request.getAudioUrl());
            exercise.setOrdering(request.getOrdering());
            exerciseRepository.save(exercise);

            if (request.getType() == 1 && request.getGroupOptionList() != null) {
                List<GroupOption> groupOptions = request.getGroupOptionList().stream().map(text -> {
                    GroupOption g = new GroupOption();
                    g.setExercise(exercise);
                    g.setOptionText(text);
                    return g;
                }).toList();
                groupOptionResponse.saveAll(groupOptions);
            }

            for (QuizTreeRequest.SubQuestionNode node : request.getSubQuestionNodes()) {
                createQuestionAndOptions(node, exercise);
            }
        }
        return ResponseEntity.ok("Tạo cây câu hỏi thành công");
    }

    /**
     * ---------------- IMPORT JSON ----------------
     */
    public ResponseEntity<?> importFromJson(MultipartFile file) throws IOException {
        List<Map<String, Object>> dataList = objectMapper.readValue(file.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {
                });

        for (Map<String, Object> data : dataList) {
            importQuestionFromMap(data);
        }

        return ResponseEntity.ok("Import danh sách câu hỏi thành công");
    }

    /**
     * ---------------- HELPER METHODS ----------------
     */
    private QuizTreeResponse mapExerciseToResponse(Exercise exercise) {
        QuizTreeResponse resp = new QuizTreeResponse();
        resp.setId(exercise.getId());
        resp.setTitle(exercise.getTitle());
        resp.setType(exercise.getType());
        resp.setImageUrl(exercise.getImageUrl());
        resp.setAudioUrl(exercise.getAudioUrl());
        resp.setGroupOptionList(exercise.getType() == 1 ?
                groupOptionResponse.findOptionTextByExerciseId(exercise.getId()) : null);

        List<Question> questions = questionRepository.findByExercise_Id(exercise.getId());
        List<QuizTreeResponse.SubQuestionNode> questionNodes = questions.stream().map(q -> {
            QuizTreeResponse.SubQuestionNode qNode = new QuizTreeResponse.SubQuestionNode();
            qNode.setId(q.getId());
            qNode.setQuestionText(q.getQuestionText());
            qNode.setCorrect(exercise.getType() == 1 ? q.getCorrect() : null);

            List<QuestionOption> options = questionOptionRepository.findByQuestion_Id(q.getId());
            List<QuizTreeResponse.SubQuestionNode.SubOptionNode> optNodes = options.stream().map(o -> {
                QuizTreeResponse.SubQuestionNode.SubOptionNode opt = new QuizTreeResponse.SubQuestionNode.SubOptionNode();
                opt.setId(o.getId());
                opt.setOptionText(o.getOptionText());
                return opt;
            }).toList();
            qNode.setOptions(optNodes);
            return qNode;
        }).toList();

        resp.setSubQuestionNodes(questionNodes);
        return resp;
    }

    private Question createQuestionAndOptions(QuizTreeRequest.SubQuestionNode node, Exercise exercise) {
        Question q = new Question();
        q.setQuestionText(node.getQuestionText());
        q.setOrdering(node.getOrdering());
        q.setCorrect(node.getCorrect());
        q.setExercise(exercise);
        questionRepository.save(q);

        if (node.getOptions() != null) {
            List<QuestionOption> options = node.getOptions().stream().map(optNode -> {
                QuestionOption qo = new QuestionOption();
                qo.setOptionText(optNode.getOptionText());
                qo.setIsCorrect(optNode.getIsCorrect());
                qo.setQuestion(q);
                return qo;
            }).toList();
            questionOptionRepository.saveAll(options);
        }
        return q;
    }

    private void importQuestionFromMap(Map<String, Object> data) {
        Question q = new Question();
        q.setQuestionText((String) data.get("question_text"));
        q.setCorrect((String) data.get("correct"));
        q.setOrdering(getInteger(data.get("ordering")));

        q.setSkill(quizEntityService.getSkill(getLong(data.get("skillId"))));
        q.setLevel(quizEntityService.getLevel(getLong(data.get("levelId"))));
        q.setTopic(quizEntityService.getTopic(getLong(data.get("topicId"))));
        q.setVocabulary(quizEntityService.getVocabulary(getLong(data.get("vocabularyId"))));
        q.setGrammarItem(quizEntityService.getGrammarItem(getLong(data.get("grammarId"))));
        q.setExercise(quizEntityService.getExercise(getLong(data.get("exerciseId"))));

        questionRepository.save(q);

        List<Map<String, Object>> options = (List<Map<String, Object>>) data.get("options");
        if (options != null) {
            List<QuestionOption> questionOptions = options.stream().map(opt -> {
                QuestionOption qo = new QuestionOption();
                qo.setOptionText(opt.get("option_text").toString());
                qo.setIsCorrect(Boolean.parseBoolean(opt.get("is_correct").toString()));
                qo.setQuestion(q);
                return qo;
            }).toList();
            questionOptionRepository.saveAll(questionOptions);
        }
    }

    private Long getLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private Integer getInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

}
