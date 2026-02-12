package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StoryCharacter;
import com.ruoyi.system.mapper.StoryCharacterMapper;

// ...

public class StoryServiceImpl {

    private final StoryCharacterMapper storyCharacterMapper;

    public StoryServiceImpl(StoryCharacterMapper storyCharacterMapper) {
        this.storyCharacterMapper = storyCharacterMapper;
    }

    // Other methods that use StoryCharacter...

}