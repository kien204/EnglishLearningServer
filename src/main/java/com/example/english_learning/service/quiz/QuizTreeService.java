package com.example.english_learning.service.quiz;

import com.example.english_learning.dto.request.quiz.QuizTreeRequest;
import com.example.english_learning.dto.response.QuizTreeResponse;
import com.example.english_learning.models.*;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import com.example.english_learning.repository.quiz.GroupOptionRepository;
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
import java.util.Arrays;
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
    private GroupOptionRepository groupOptionRepository;
    @Autowired
    private TopicService topicService;

    /**
     * ---------------- GET QUIZ TREE ----------------
     */
    public List<QuizTreeResponse> getQuizTree(Long topicId) {
        Topic topic = topicService.getById(topicId);
        List<Exercise> exercises = exerciseRepository.findByTopicAndGroupWord(topic, null);

        return exercises.stream()
                .map(this::mapExerciseToResponse)
                .toList();
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
        resp.setGroupOptionList(exercise.getType() == 2 ?
                groupOptionRepository.findOptionTextByExerciseId(exercise.getId()) : null);

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
    public void saveQuizTree(QuizTreeRequest request) {

        // 1. Tạo Exercise
        Exercise exercise = new Exercise();
        exercise.setTopic(quizEntityService.getTopic(request.getTopicId()));
        exercise.setGroupWord(request.getGroupWord());
        exercise.setTitle(request.getTitle());
        exercise.setType(request.getType());
        exercise.setImageUrl(request.getImageUrl());
        exercise.setAudioUrl(request.getAudioUrl());
        exerciseRepository.save(exercise);

        // 2. Lưu GroupOption nếu type = 2
        if (request.getType() == 2 && request.getGroupOptionList() != null) {
            List<GroupOption> groupOptions = request.getGroupOptionList()
                    .stream()
                    .map(text -> {
                        GroupOption g = new GroupOption();
                        g.setExercise(exercise);
                        g.setOptionText(text);
                        return g;
                    })
                    .toList();

            groupOptionRepository.saveAll(groupOptions);
        }

        // 3. Lưu Question + Options
        if (request.getSubQuestionNodes() != null) {
            request.getSubQuestionNodes()
                    .forEach(node -> {

                        // Question
                        Question q = new Question();
                        q.setQuestionText(node.getQuestionText());
                        q.setCorrect(node.getCorrect());
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
