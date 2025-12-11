package com.example.english_learning.repository.quiz;

import com.example.english_learning.dto.request.quiz.SubmitQuizRequest;
import com.example.english_learning.dto.response.SubmitQuizResponse;
import com.example.english_learning.models.Exercise;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SubmitAndResultQuizService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    public List<SubmitQuizResponse> submitQuiz(List<SubmitQuizRequest> listRequest) {
        List<SubmitQuizResponse> responses = new ArrayList<>();
        for (SubmitQuizRequest request : listRequest) {
            Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(() ->
                    new EntityNotFoundException("Không tìm thấy bài tập")
            );

            if (exercise.getType() == 0) {
                long totalQuestions = request.getAnswers().size();

                Set<Long> correctOptionIds = new HashSet<>(
                        questionOptionRepository.findCorrectOptionIdsByExercise(request.getExerciseId())
                );

                List<SubmitQuizResponse.SelectQuestion> answerResults = new ArrayList<>();

                long correctCount = request.getAnswers().stream()
                        .map(answer -> {
                            boolean isCorrect = answer.getSelectedOptionId() != null &&
                                    correctOptionIds.contains(answer.getSelectedOptionId());

                            answerResults.add(new SubmitQuizResponse.SelectQuestion(
                                    answer.getQuestionId(),
                                    answer.getSelectedOptionId(),
                                    isCorrect
                            ));

                            return isCorrect;
                        })
                        .filter(isCorrect -> isCorrect)
                        .count();

                SubmitQuizResponse response = new SubmitQuizResponse();

                float score = (float) (Math.round((correctCount * 10.0 / totalQuestions) * 100) / 100.0);

                response.setScore(score);
                response.setCorrectCount(correctCount);
                response.setExerciseId(request.getExerciseId());
                response.setTotalQuestions(totalQuestions);
                response.setResults(answerResults);

                responses.add(response);
            }


        }


        return responses;
    }
}
