package com.example.english_learning.service.speech;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PronunciationService {

    public PronunciationResult score(Map<String, Object> pythonResult) {

        List<Map<String, Object>> wordSegments = (List<Map<String, Object>>) pythonResult.get("word_segments");
        List<Map<String, Object>> segments = (List<Map<String, Object>>) pythonResult.get("segments");

        if (wordSegments == null || wordSegments.isEmpty() || segments == null) {
            return new PronunciationResult(0, 0, 0, 0);
        }

        double duration = ((Number) segments.get(segments.size() - 1).get("end")).doubleValue()
                - ((Number) segments.get(0).get("start")).doubleValue();

        int totalWords = wordSegments.size();
        double wpm = (totalWords / duration) * 60;

        double accuracy = wordSegments.stream()
                .mapToDouble(w -> ((Number) w.get("score")).doubleValue())
                .average().orElse(0);

        double fluency = Math.min(1.0, wpm / 160.0);

        long pauses = 0;
        for (int i = 0; i < wordSegments.size() - 1; i++) {
            double end = ((Number) wordSegments.get(i).get("end")).doubleValue();
            double nextStart = ((Number) wordSegments.get(i + 1).get("start")).doubleValue();
            if (nextStart - end > 0.6) pauses++;
        }

        double pausePenalty = Math.max(0.6, 1 - (pauses * 0.04));

        double raw = (accuracy * 0.7) + (fluency * 0.3);
        double finalScore = Math.round((raw * pausePenalty) * 10 * 10.0) / 10.0;

        return new PronunciationResult(finalScore, wpm, pauses, accuracy);
    }

    public record PronunciationResult(double score, double wpm, long pauses, double accuracy) {
    }
}

