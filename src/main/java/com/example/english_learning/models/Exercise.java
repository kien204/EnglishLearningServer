package com.example.english_learning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Exercise {
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

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String title;
    private int type;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;


    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    private int ordering;

    @OneToMany(mappedBy = "exercise")
    @JsonIgnore
    private List<Question> questions;

    @OneToMany(mappedBy = "exercise")
    @JsonIgnore
    private List<GroupOption> groupOptions;

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
}
