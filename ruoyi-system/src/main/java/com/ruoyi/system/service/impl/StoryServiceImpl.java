package com.ruoyi.system.service.impl;

import com.ruoyi.system.mapper.StoryCharacterMapper;
import com.ruoyi.system.domain.StoryCharacter;
import com.ruoyi.system.service.IStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoryServiceImpl implements IStoryService {

    @Autowired
    private StoryCharacterMapper storyCharacterMapper;

    @Override
    public String generateStory() {
        // Implement story generation logic here
        return "Generated story content.";
    }

    @Override
    @Transactional
    public void addStory(StoryCharacter story) {
        try {
            storyCharacterMapper.insert(story);
        } catch (Exception e) {
            // Handle error
            throw new RuntimeException("Error adding story: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateStory(StoryCharacter story) {
        try {
            storyCharacterMapper.update(story);
        } catch (Exception e) {
            // Handle error
            throw new RuntimeException("Error updating story: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteStory(Long id) {
        try {
            storyCharacterMapper.delete(id);
        } catch (Exception e) {
            // Handle error
            throw new RuntimeException("Error deleting story: " + e.getMessage());
        }
    }

    @Override
    public List<StoryCharacter> listAllStories() {
        return storyCharacterMapper.selectAll();
    }
}