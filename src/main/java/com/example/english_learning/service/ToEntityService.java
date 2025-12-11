package com.example.english_learning.service;

import com.example.english_learning.models.*;
import com.example.english_learning.repository.*;
import com.example.english_learning.repository.quiz.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToEntityService {

    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private GrammarRepository grammarItemRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    public Skill getSkill(Long id) {
        return id == null ? null : skillRepository.findById(id).orElse(null);
    }

    public Level getLevel(Long id) {
        return id == null ? null : levelRepository.findById(id).orElse(null);
    }

    public Topic getTopic(Long id) {
        return id == null ? null : topicRepository.findById(id).orElse(null);
    }

    public Vocabulary getVocabulary(Long id) {
        return id == null ? null : vocabularyRepository.findById(id).orElse(null);
    }

    public Grammar getGrammarItem(Long id) {
        return id == null ? null : grammarItemRepository.findById(id).orElse(null);
    }

    public Exercise getExercise(Long id) {
        return id == null ? null : exerciseRepository.findById(id).orElse(null);
    }
}

