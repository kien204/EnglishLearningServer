package com.example.english_learning.mapper;


import com.example.english_learning.dto.request.VocabularyRequest;
import com.example.english_learning.models.Vocabulary;
import org.springframework.stereotype.Component;

@Component
public class VocabularyMapper {
    public Vocabulary toEntity(VocabularyRequest vocabularyRequest) {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setWord(vocabularyRequest.getWord());
        vocabulary.setPhonetic(vocabularyRequest.getPhonetic());
        vocabulary.setWordType(vocabularyRequest.getWordType());
        vocabulary.setLevel(vocabularyRequest.getLevel());
        vocabulary.setV2(vocabularyRequest.getV2());
        vocabulary.setV3(vocabularyRequest.getV3());
        vocabulary.setMeaning(vocabularyRequest.getMeaning());
        vocabulary.setTopic(vocabularyRequest.getTopic());
        vocabulary.setExample(vocabularyRequest.getExample());
        return vocabulary;
    }
}
