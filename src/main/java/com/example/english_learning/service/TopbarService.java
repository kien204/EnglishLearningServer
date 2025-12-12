package com.example.english_learning.service;

import com.example.english_learning.dto.response.TopbarResponse;
import com.example.english_learning.models.Level;
import com.example.english_learning.models.Skill;
import com.example.english_learning.repository.LevelRepository;
import com.example.english_learning.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopbarService {
    @Autowired
    private SkillService skillService;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TopicRepository topicRepository;

    public List<TopbarResponse> getMenuTopbar() {
        List<Skill> list = topicRepository.getTopbarSkills();
        return list.stream().map(this::mapToResponse).toList();
    }

    private TopbarResponse mapToResponse(Skill skill) {
        List<Level> levelList = topicRepository.getLevelsBySkillId(skill.getId());
        TopbarResponse response = new TopbarResponse();
        response.setLevels(levelList);
        response.setSkillId(skill.getId());
        response.setName(skill.getName());

        return response;
    }
}
