package com.example.english_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupTreeResponse {
    private Long id;
    private String title;
    private List<SubGroupNode> subgroups;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubGroupNode {
        private Long id;
        private String title;
        private List<GrammarItemNode> items;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class GrammarItemNode {
            private Long id;
            private String title;
            private String structure;
            private String explanation;
            private String example;
            private String tip;
            private String imageUrl;
        }
    }
}
