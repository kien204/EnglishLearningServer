package com.example.english_learning.service;

import com.example.english_learning.models.Grammar;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.GrammarRepository;
import com.example.english_learning.repository.TopicRepository;
import com.example.english_learning.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TreeLearnService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private GrammarRepository grammarRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    public ResponseEntity<?> getTreeVocabulary() {
        List<Topic> listTopic = topicRepository.findBySkill_Id(1L);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Topic topic : listTopic) {
            List<Integer> groupWords =
                    vocabularyRepository.findGroupWordByTopic(topic);

            result.add(Map.of("topic_id", topic.getId(),
                    "topic_name", topic.getName(),
                    "description", topic.getDescription(),
                    "group_words", groupWords));
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getTreeGrammar() {

        List<Topic> listTopic = topicRepository.findBySkill_Id(2L);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Topic topic : listTopic) {
            List<Grammar> grammarList = grammarRepository.findAllByTopicId(topic.getId());

            result.add(Map.of("topic_id", topic.getId(),
                    "topic_name", topic.getName(),
                    "description", topic.getDescription(),
                    "grammar_list", grammarList));
        }

        return ResponseEntity.ok(result);
    }
}
