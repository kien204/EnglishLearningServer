package com.example.english_learning.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = true)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = true)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = true)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = true)
    private Vocabulary vocabulary;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grammar_item_id", nullable = true)
    private GrammarItem grammarItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = true)
    private Exercise exercise;

    @Column(name = "question_text", columnDefinition = "NVARCHAR(MAX)")
    private String questionText;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String correct;

    @Column(nullable = true)  // tạm thời CHO NULL
    private int ordering;

    @OneToMany(mappedBy = "question")
    @JsonIgnoreProperties("exercise")
    private List<QuestionOption> questionOptions;

    @JsonProperty("exercise")
    public Long getExerciseId() {
        return exercise != null ? exercise.getId() : null;
    }

    @JsonProperty("level")
    public String getLevelName() {
        return level != null ? level.getName() : null;
    }

    @JsonProperty("skill")
    public String getSkillName() {
        return skill != null ? skill.getName() : null;
    }

    @JsonProperty("topic")
    private String getTopicName() {
        return topic != null ? topic.getName() : null;
    }

    @JsonProperty("vocabulary")
    public String getVocabularyName() {
        return vocabulary != null ? vocabulary.getWord() : null;
    }
}
