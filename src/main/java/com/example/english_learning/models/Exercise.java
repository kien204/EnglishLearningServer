package com.example.english_learning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = true)
    private Topic topic;

    @Column(name = "group_word")
    private Integer groupWord;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String title;
    private int type; // 0:Single Choice 1:Multiple Choice 2: Dropdown 3:Điền chữ / Text Input 4:Nói

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;


    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "audio_url")
    private String audioUrl;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Question> questions;

    @JsonProperty("topic")
    private Long getTopicName() {
        return topic != null ? topic.getId() : null;
    }
}
