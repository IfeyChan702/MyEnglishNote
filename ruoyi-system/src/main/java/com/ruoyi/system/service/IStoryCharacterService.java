package com.ruoyi.system.service;

import com.ruoyi.system.domain.StoryCharacter;
import java.util.List;

public interface IStoryCharacterService {
    public StoryCharacter selectCharacterById(Long id);
    public List<StoryCharacter> selectCharacterList(StoryCharacter storyCharacter);
    public List<StoryCharacter> selectCharacterListByUserId(Long userId);
    public int insertCharacter(StoryCharacter storyCharacter);
    public int updateCharacter(StoryCharacter storyCharacter);
    public int deleteCharacterById(Long id);
    public int deleteCharacterByIds(Long[] ids);
    public List<StoryCharacter> searchCharacters(Long userId, String keyword);
}