package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StoryCharacter;
import java.util.List;

public interface StoryCharacterMapper {
    int insert(StoryCharacter storyCharacter);

    int update(StoryCharacter storyCharacter);

    int delete(Long id);

    StoryCharacter selectById(Long id);

    List<StoryCharacter> selectAll();
}