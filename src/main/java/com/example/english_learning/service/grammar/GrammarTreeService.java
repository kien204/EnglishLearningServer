package com.example.english_learning.service.grammar;

import com.example.english_learning.dto.response.GroupTreeResponse;
import com.example.english_learning.dto.response.GroupTreeResponse.SubGroupNode;
import com.example.english_learning.dto.response.GroupTreeResponse.SubGroupNode.GrammarItemNode;
import com.example.english_learning.models.GrammarCategory;
import com.example.english_learning.models.GrammarGroup;
import com.example.english_learning.models.GrammarItem;
import com.example.english_learning.repository.grammar.GrammarCategoryRepository;
import com.example.english_learning.repository.grammar.GrammarGroupRepository;
import com.example.english_learning.repository.grammar.GrammarItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrammarTreeService {

    private final GrammarGroupRepository groupRepo;
    private final GrammarCategoryRepository categoryRepo;
    private final GrammarItemRepository itemRepo;

    public List<GroupTreeResponse> buildTree() {

        // 1. Lấy toàn bộ group
        List<GrammarGroup> groups = groupRepo.findAll();

        return groups.stream().map(group -> {

            // 2. Lấy subgroups theo groupId
            List<GrammarCategory> subgroups =
                    categoryRepo.findByGroupId(group.getId());

            // 3. Convert subgroup → SubGroupNode
            List<SubGroupNode> subNodes = subgroups.stream().map(sub -> {

                // 4. Lấy items theo subGroupId
                List<GrammarItem> items =
                        itemRepo.findByCategoryId(sub.getId());

                // 5. Convert items → GrammarItemNode
                List<GrammarItemNode> itemNodes = items.stream().map(i ->
                        new GrammarItemNode(
                                i.getId(),
                                i.getTitle(),
                                i.getStructure(),
                                i.getExplanation(),
                                i.getExample(),
                                i.getTip(),
                                i.getImageUrl()
                        )
                ).toList();

                // 6. Tạo node subgroup
                return new SubGroupNode(
                        sub.getId(),
                        sub.getTitle(),
                        itemNodes
                );

            }).toList();

            // 7. Trả về group node hoàn chỉnh
            return new GroupTreeResponse(
                    group.getId(),
                    group.getTitle(),
                    subNodes
            );

        }).toList();
    }
}
