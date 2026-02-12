package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StoryCharacter;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StoryCharacterMapper {
    public StoryCharacter selectStoryCharacterById(Long id);
    public List<StoryCharacter> selectStoryCharacterList(StoryCharacter storyCharacter);
    public List<StoryCharacter> selectStoryCharacterListByUserId(Long userId);
    public int insertStoryCharacter(StoryCharacter storyCharacter);
    public int updateStoryCharacter(StoryCharacter storyCharacter);
    public int deleteStoryCharacterById(Long id);
    public int deleteStoryCharacterByIds(Long[] ids);
    public int incrementStoryCount(Long characterId);
    public int decrementStoryCount(Long characterId);
    public List<StoryCharacter> searchStoryCharacters(@Param("userId") Long userId, @Param("keyword") String keyword);
}