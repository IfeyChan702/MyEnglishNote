package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StoryCharacter;
import com.ruoyi.system.mapper.StoryCharacterMapper;
import java.util.List;

public class StoryCharacterServiceImpl implements StoryCharacterService {

    private final StoryCharacterMapper storyCharacterMapper;

    public StoryCharacterServiceImpl(StoryCharacterMapper storyCharacterMapper) {
        this.storyCharacterMapper = storyCharacterMapper;
    }

    @Override
    public List<StoryCharacter> getAllCharacters() {
        return storyCharacterMapper.selectAll();
    }

    // Other methods using StoryCharacter and StoryCharacterMapper...
}