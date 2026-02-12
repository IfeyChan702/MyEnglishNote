package com.ruoyi.system.service;

import com.ruoyi.system.domain.Story;
import com.ruoyi.system.domain.dto.GenerateStoryRequest;
import com.ruoyi.system.domain.dto.GenerateStoryResponse;
import java.util.List;

public interface IStoryService {
    public Story selectStoryById(Long id);
    public List<Story> selectStoryList(Story story);
    public List<Story> selectStoryListByUserId(Long userId);
    public List<Story> selectStoryListByCharacterId(Long characterId);
    public int insertStory(Story story);
    public int updateStory(Story story);
    public int deleteStoryById(Long id);
    public int deleteStoryByIds(Long[] ids);
    public GenerateStoryResponse generateStory(Long userId, GenerateStoryRequest request);
    public int favoriteStory(Long userId, Long storyId);
    public int unfavoriteStory(Long userId, Long storyId);
    public List<Story> searchStories(Long userId, String keyword, Double similarityThreshold, Integer maxResults);
    public Story getStoryByShareToken(String shareToken);
    public String generateShareToken(Long storyId);
}