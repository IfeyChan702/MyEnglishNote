package com.ruoyi.system.service;

import com.ruoyi.system.domain.StoryCharacter;
import java.util.List;

public interface IStoryCharacterService {
    public StoryCharacter selectStoryCharacterById(Long id);
    public List<StoryCharacter> selectStoryCharacterList(StoryCharacter storyCharacter);
    public List<StoryCharacter> selectStoryCharacterListByUserId(Long userId);
    public int insertStoryCharacter(StoryCharacter storyCharacter);
    public int updateStoryCharacter(StoryCharacter storyCharacter);
    public int deleteStoryCharacterById(Long id);
    public int deleteStoryCharacterByIds(Long[] ids);
    public List<StoryCharacter> searchStoryCharacters(Long userId, String keyword);
}