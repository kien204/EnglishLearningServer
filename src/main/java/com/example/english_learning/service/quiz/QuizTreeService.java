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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    // Hàm này nhận vào List<Exercise> thay vì 1 Exercise
    public QuizTreeResponse mapExercisesToMergedResponse(List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            return null;
        }

        // 1. Tạo đối tượng Response chung (Lấy thông tin từ bài đầu tiên hoặc set chung)
        QuizTreeResponse resp = new QuizTreeResponse();
        Exercise firstEx = exercises.get(0);

        // Set Metadata chung (bạn có thể tùy chỉnh lại tiêu đề cho phù hợp)
        resp.setExerciseId(firstEx.getId()); // ID 0 để đánh dấu là bài tổng hợp
        resp.setTopic(firstEx.getTopic().getName());
        resp.setTitle("Tổng hợp: " + firstEx.getTopic().getName());
        resp.setType(firstEx.getType());
        resp.setDescription("Bài tập tổng hợp gồm " + exercises.size() + " phần.");
        resp.setImageUrl(firstEx.getImageUrl());
        resp.setAudioUrl(firstEx.getAudioUrl());

        // 2. Khởi tạo List chứa TẤT CẢ các node câu hỏi
        List<QuizTreeResponse.SubQuestionNode> allQuestionNodes = new ArrayList<>();

        // Xác định logic số lượng (sl) dựa trên số lượng bài tập đầu vào
        int sl = exercises.size();

        // 3. Duyệt qua từng bài tập để lấy câu hỏi và gộp vào list tổng
        for (Exercise exercise : exercises) {
            List<Question> questions;

            // Logic lấy câu hỏi (như code cũ của bạn)
            if (sl == 1) {
                questions = questionRepository.findByExercise_Id(exercise.getId());
            } else if (sl == 2) {
                questions = questionRepository.findRandomByExerciseId(exercise.getId(), 15);
            } else {
                questions = questionRepository.findRandomByExerciseId(exercise.getId(), 10);
            }

            // Map Question -> SubQuestionNode
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

            // QUAN TRỌNG: Gộp list node con vào list tổng
            allQuestionNodes.addAll(nodes);
        }

        // 4. (Tùy chọn) Xáo trộn câu hỏi để bài tổng hợp ngẫu nhiên hơn
        Collections.shuffle(allQuestionNodes);

        // 5. Set list tổng vào response
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

        // 1. Tạo Exercise
        Exercise exercise = new Exercise();
        exercise.setTopic(topicService.getById(request.getTopicId()));
        exercise.setGroupWord(request.getGroupWord());
        exercise.setTitle(request.getTitle());
        exercise.setType(request.getType());
        exercise.setDescription(request.getDescription());
        exercise.setImageUrl(request.getImageUrl());
        exercise.setAudioUrl(request.getAudioUrl());
        exerciseRepository.save(exercise);

        // 3. Lưu Question + Options
        if (request.getSubQuestionNodes() != null) {
            request.getSubQuestionNodes()
                    .forEach(node -> {

                        // Question
                        Question q = new Question();
                        q.setQuestionText(node.getQuestionText());
                        q.setVocabulary(quizEntityService.getVocabulary(node.getVocabulary_id()));
                        q.setExercise(exercise);
                        questionRepository.save(q);

                        // Options
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

}
