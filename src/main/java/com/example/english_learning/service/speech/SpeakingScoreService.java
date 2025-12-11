package com.example.english_learning.service.speech;

import com.example.english_learning.service.other.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpeakingScoreService {

    private final GeminiService geminiService;
    private final PronunciationService pronunciationService;

    public Map<String, Object> evaluate(Long exerciseId, Map<String, Object> pythonResult) {

        String transcript = (String) pythonResult.get("text");

        // 1) Pronunciation scoring
        var pron = pronunciationService.score(pythonResult);

        // 2) Content scoring (Grammar - Vocabulary - Coherence - Relevance)
        Map<String, Object> aiScore = geminiService.gradeContent(exerciseId, transcript);

        double grammar = toDouble(aiScore.get("grammar_score"));
        double vocab = toDouble(aiScore.get("vocab_score"));
        double coherence = toDouble(aiScore.get("coherence_score"));
        double relevance = toDouble(aiScore.get("relevance_score"));

        double overall = (pron.score() + grammar + vocab + coherence + relevance) / 5;

        if (relevance <= 2) overall *= 0.20;
        else if (relevance <= 4) overall *= 0.50;
        else if (relevance <= 6) overall *= 0.80;

        Map<String, Object> finalResult = new LinkedHashMap<>();
        finalResult.put("transcript", transcript);
        finalResult.put("overall_score", round(overall));

        finalResult.put("pronunciation_score", pron.score());
        finalResult.put("speaking_speed_wpm", pron.wpm());
        finalResult.put("pause_count", pron.pauses());
        finalResult.put("accuracy_score", pron.accuracy());

        finalResult.put("grammar_score", grammar);
        finalResult.put("vocab_score", vocab);
        finalResult.put("coherence_score", coherence);
        finalResult.put("relevance_score", relevance);

        finalResult.put("feedback", aiScore.get("feedback"));
        finalResult.put("corrected_version", aiScore.get("corrected_version"));

        return finalResult;
    }

    private double toDouble(Object obj) {
        return obj instanceof Number n ? n.doubleValue() : Double.parseDouble(obj.toString());
    }

    private double round(double val) {
        return Math.round(val * 10.0) / 10.0;
    }
}

