package com.example.english_learning.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vocabulary")
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String word; // từ tiếng Anh

    @Column(length = 50)
    private String pos; // Part of Speech: noun, verb, adj, adv...

    @Column(length = 200, columnDefinition = "NVARCHAR(255)")
    private String pron; // phiên âm

    @Column(columnDefinition = "NVARCHAR(255)")
    private String meaningVn; // nghĩa tiếng Việt

    @Column(length = 200)
    private String v2; // động từ bất quy tắc V2

    @Column(length = 200)
    private String v3; // động từ bất quy tắc V3

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = true)
    private Topic topic; // liên kết tới bảng topics

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String exampleEn; // ví dụ tiếng Anh

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String exampleVn; // ví dụ tiếng Việt

    @Column(name = "group_word")
    private Integer groupWord; // ví dụ tiếng Việt

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("topic")
    public long getTopicName() {
        return topic != null ? topic.getId() : null;
    }
}
