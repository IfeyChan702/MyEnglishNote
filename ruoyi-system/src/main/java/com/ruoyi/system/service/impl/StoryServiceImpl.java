package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.system.domain.Story;
import com.ruoyi.system.domain.StoryCharacter;
import com.ruoyi.system.domain.dto.GenerateStoryRequest;
import com.ruoyi.system.domain.dto.GenerateStoryResponse;
import com.ruoyi.system.mapper.StoryCharacterMapper;
import com.ruoyi.system.mapper.StoryFavoriteMapper;
import com.ruoyi.system.mapper.StoryMapper;
import com.ruoyi.system.service.IDeepseekService;
import com.ruoyi.system.service.IStoryService;
import com.ruoyi.system.util.DeepseekApiClient;
import com.ruoyi.system.util.EmbeddingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StoryServiceImpl implements IStoryService {
    private static final Logger log = LoggerFactory.getLogger(StoryServiceImpl.class);

    @Autowired
    private StoryMapper storyMapper;
    @Autowired
    private StoryCharacterMapper storyCharacterMapper;
    @Autowired
    private StoryFavoriteMapper storyFavoriteMapper;
    @Autowired
    private DeepseekApiClient deepseekApiClient;
    @Autowired
    private IDeepseekService deepseekService;
    @Value("${rag.deepseek.embedding-model:}")
    private String embeddingModel;
    @Value("${rag.deepseek.chat-model:}")
    private String chatModel;

    @Override
    public Story selectStoryById(Long id) {
        return storyMapper.selectStoryById(id);
    }

    @Override
    public List<Story> selectStoryList(Story story) {
        return storyMapper.selectStoryList(story);
    }

    @Override
    public List<Story> selectStoryListByUserId(Long userId) {
        return storyMapper.selectStoryListByUserId(userId);
    }

    @Override
    public List<Story> selectStoryListByCharacterId(Long characterId) {
        return storyMapper.selectStoryListByCharacterId(characterId);
    }

    @Override
    @Transactional
    public int insertStory(Story story) {
        story.setDelFlag(false);  // ✅ Boolean false
        story.setIsFavorite(false);  // ✅ Boolean false
        story.setViewCount(0);
        story.setShareCount(0);
        story.setCreatedAt(LocalDateTime.now());  // ✅ LocalDateTime
        story.setUpdatedAt(LocalDateTime.now());  // ✅ LocalDateTime
        int result = storyMapper.insertStory(story);
        if (story.getCharacterId() != null) {
            storyCharacterMapper.incrementStoryCount(story.getCharacterId());
        }
        return result;
    }

    @Override
    @Transactional
    public int updateStory(Story story) {
        story.setUpdatedAt(LocalDateTime.now());  // ✅ LocalDateTime
        return storyMapper.updateStory(story);
    }

    @Override
    @Transactional
    public int deleteStoryById(Long id) {
        Story story = storyMapper.selectStoryById(id);
        if (story != null && story.getCharacterId() != null) {
            storyCharacterMapper.decrementStoryCount(story.getCharacterId());
        }
        return storyMapper.deleteStoryById(id);
    }

    @Override
    @Transactional
    public int deleteStoryByIds(Long[] ids) {
        for (Long id : ids) {
            deleteStoryById(id);
        }
        return ids.length;
    }

    @Override
    @Transactional
    public GenerateStoryResponse generateStory(Long userId, GenerateStoryRequest request) {
        long startTime = System.currentTimeMillis();
        GenerateStoryResponse response = new GenerateStoryResponse();
        try {
            StoryCharacter storyCharacter = storyCharacterMapper.selectStoryCharacterById(request.getCharacterId());
            if (storyCharacter == null) {
                throw new RuntimeException("Character not found");
            }
            if (!storyCharacter.getUserId().equals(userId)) {
                throw new RuntimeException("Character does not belong to the user");
            }
            List<String> objects = deepseekApiClient.analyzeImage(request.getImage(), request.getImageType());
            if (objects == null || objects.isEmpty()) {
                throw new RuntimeException("No objects identified");
            }
            String storyContent = generateStoryFromObjects(objects, storyCharacter.getName());
            List<Double> embedding = deepseekService.embedding(storyContent);
            String embeddingJson = EmbeddingUtil.embeddingToJson(embedding);
            Story story = new Story();
            story.setUserId(userId);
            story.setCharacterId(request.getCharacterId());
            story.setTitle(request.getTitle() != null ? request.getTitle() : "Story");
            story.setContent(storyContent);
            story.setObjects(JSON.toJSONString(objects));
            story.setImageUrl(request.getImage());
            story.setEmbedding(embeddingJson);
            story.setEmbeddingModel(embeddingModel);
            story.setDelFlag(false);  // ✅ Boolean
            story.setIsFavorite(false);  // ✅ Boolean
            story.setViewCount(0);
            story.setShareCount(0);
            story.setCreatedAt(LocalDateTime.now());  // ✅ LocalDateTime
            story.setUpdatedAt(LocalDateTime.now());  // ✅ LocalDateTime
            storyMapper.insertStory(story);
            storyCharacterMapper.incrementStoryCount(request.getCharacterId());
            response.setStoryId(story.getId());
            response.setTitle(story.getTitle());
            response.setContent(storyContent);
            response.setObjects(objects);
            response.setCharacterName(storyCharacter.getName());
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("Failed to generate story: {}", e.getMessage());
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        }
        response.setProcessingTime(System.currentTimeMillis() - startTime);
        return response;
    }

    @Override
    @Transactional
    public int favoriteStory(Long userId, Long storyId) {
        Story story = storyMapper.selectStoryById(storyId);
        if (story == null) return 0;
        story.setIsFavorite(true);  // ✅ Boolean true
        return storyMapper.updateStory(story);
    }

    @Override
    @Transactional
    public int unfavoriteStory(Long userId, Long storyId) {
        Story story = storyMapper.selectStoryById(storyId);
        if (story == null) return 0;
        story.setIsFavorite(false);  // ✅ Boolean false
        return storyMapper.updateStory(story);
    }

    @Override
    public List<Story> searchStories(Long userId, String keyword, Double similarityThreshold, Integer maxResults) {
        List<Story> allStories = storyMapper.selectStoryListByUserId(userId);
        if (allStories == null) return new ArrayList<>();
        return allStories;
    }

    @Override
    public Story getStoryByShareToken(String shareToken) {
        return storyMapper.selectStoryByShareToken(shareToken);
    }

    @Override
    public String generateShareToken(Long storyId) {
        String token = UUID.randomUUID().toString();
        Story story = storyMapper.selectStoryById(storyId);
        if (story != null) {
            story.setShareToken(token);
            storyMapper.updateStory(story);
        }
        return token;
    }

    private String generateStoryFromObjects(List<String> objects, String characterName) {
        String objectsText = String.join(", ", objects);
        String prompt = String.format(
                "Please write a creative fairy tale in English featuring a character named %s. " +
                        "The story should incorporate the following objects: %s.",
                characterName, objectsText
        );
        return deepseekService.chatWithContext(null, prompt);
    }
}