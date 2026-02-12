package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StoryCharacter;
import com.ruoyi.system.mapper.StoryCharacterMapper;
import com.ruoyi.system.service.IStoryCharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CharacterServiceImpl implements IStoryCharacterService {
    @Autowired
    private StoryCharacterMapper storyCharacterMapper;

    @Override
    public StoryCharacter selectCharacterById(Long id) {
        return storyCharacterMapper.selectStoryCharacterById(id);
    }

    @Override
    public List<StoryCharacter> selectCharacterList(StoryCharacter storyCharacter) {
        return storyCharacterMapper.selectStoryCharacterList(storyCharacter);
    }

    @Override
    public List<StoryCharacter> selectCharacterListByUserId(Long userId) {
        return storyCharacterMapper.selectStoryCharacterListByUserId(userId);
    }

    @Override
    @Transactional
    public int insertCharacter(StoryCharacter storyCharacter) {
        storyCharacter.setDelFlag("0");
        storyCharacter.setStoryCount(0);
        storyCharacter.setCreatedAt(new Date());
        storyCharacter.setUpdatedAt(new Date());
        return storyCharacterMapper.insertStoryCharacter(storyCharacter);
    }

    @Override
    @Transactional
    public int updateCharacter(StoryCharacter storyCharacter) {
        storyCharacter.setUpdatedAt(new Date());
        return storyCharacterMapper.updateStoryCharacter(storyCharacter);
    }

    @Override
    @Transactional
    public int deleteCharacterById(Long id) {
        return storyCharacterMapper.deleteStoryCharacterById(id);
    }

    @Override
    @Transactional
    public int deleteCharacterByIds(Long[] ids) {
        int count = 0;
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                count += storyCharacterMapper.deleteStoryCharacterById(id);
            }
        }
        return count;
    }

    @Override
    public List<StoryCharacter> searchCharacters(Long userId, String keyword) {
        return storyCharacterMapper.searchStoryCharacters(userId, keyword);
    }
}