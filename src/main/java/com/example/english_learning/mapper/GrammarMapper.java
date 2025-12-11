package com.example.english_learning.mapper;

import com.example.english_learning.dto.request.GrammarRequest;
import com.example.english_learning.models.Grammar;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GrammarMapper {
    public Grammar toEntity(GrammarRequest request) {
        Grammar grammarItem = new Grammar();
        grammarItem.setTip(request.getTip());
        grammarItem.setTitle(request.getTitle());
        grammarItem.setExample(request.getExample());
        grammarItem.setStructure(request.getStructure());
        grammarItem.setImageUrl(request.getImageUrl());
        grammarItem.setExplanation(request.getExplanation());
        return grammarItem;
    }
}
