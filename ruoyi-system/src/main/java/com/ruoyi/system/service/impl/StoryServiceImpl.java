package com.ruoyi.system.service.impl;

import com.ruoyi.project.domain.StoryCharacter; // updated import
import com.ruoyi.project.mapper.StoryCharacterMapper; // updated import

public class StoryServiceImpl {

    private StoryCharacterMapper storyCharacterMapper; // updated field

    // other existing methods

    public void someMethod() {
        StoryCharacter myCharacter = storyCharacterMapper.selectCharacterById(1); // updated usage
        // existing logic
    }
}