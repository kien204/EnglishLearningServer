package com.example.english_learning.mapper;

import com.example.english_learning.dto.request.grammar.GrammarItemRequest;
import com.example.english_learning.models.GrammarItem;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GrammarItemMapper {
    public GrammarItem toEntity(GrammarItemRequest request) {
        GrammarItem grammarItem = new GrammarItem();
        grammarItem.setTip(request.getTip());
        grammarItem.setTitle(request.getTitle());
        grammarItem.setExample(request.getExample());
        grammarItem.setStructure(request.getStructure());
        grammarItem.setImageUrl(request.getImageUrl());
        grammarItem.setExplanation(request.getExplanation());
        return grammarItem;
    }
}
