package com.example.english_learning.service;

import com.example.english_learning.models.GrammarCategory;
import com.example.english_learning.models.GrammarGroup;
import com.example.english_learning.models.Topic;
import com.example.english_learning.repository.TopicRepository;
import com.example.english_learning.repository.VocabularyRepository;
import com.example.english_learning.repository.grammar.GrammarCategoryRepository;
import com.example.english_learning.repository.grammar.GrammarGroupRepository;
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
    private GrammarGroupRepository grammarGroupRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private GrammarCategoryRepository grammarCategoryRepository;

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
        List<GrammarGroup> listTopic = grammarGroupRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (GrammarGroup topic : listTopic) {

            List<GrammarCategory> category =
                    grammarCategoryRepository.findByGroupId(topic.getId());

            result.add(Map.of(
                    "title_group", topic.getTitle(),
                    "categories", category));
        }

        return ResponseEntity.ok(result);
    }
}
