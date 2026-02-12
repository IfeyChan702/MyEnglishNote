package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.system.domain.Character;
import com.ruoyi.system.domain.Story;
import com.ruoyi.system.domain.StoryFavorite;
import com.ruoyi.system.domain.dto.GenerateStoryRequest;
import com.ruoyi.system.domain.dto.GenerateStoryResponse;
import com.ruoyi.system.mapper.CharacterMapper;
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

import java.util.*;

/**
 * 英语故事Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
@Service
public class StoryServiceImpl implements IStoryService
{
    private static final Logger log = LoggerFactory.getLogger(StoryServiceImpl.class);

    @Autowired
    private StoryMapper storyMapper;

    @Autowired
    private CharacterMapper characterMapper;

    @Autowired
    private StoryFavoriteMapper storyFavoriteMapper;

    @Autowired
    private DeepseekApiClient deepseekApiClient;

    @Autowired
    private IDeepseekService deepseekService;

    @Value("${rag.deepseek.embedding-model}")
    private String embeddingModel;

    @Value("${rag.deepseek.chat-model}")
    private String chatModel;

    /**
     * 查询英语故事
     * 
     * @param id 故事ID
     * @return 英语故事
     */
    @Override
    public Story selectStoryById(Long id)
    {
        return storyMapper.selectStoryById(id);
    }

    /**
     * 查询英语故事列表
     * 
     * @param story 英语故事
     * @return 英语故事
     */
    @Override
    public List<Story> selectStoryList(Story story)
    {
        return storyMapper.selectStoryList(story);
    }

    /**
     * 根据用户ID查询故事列表
     * 
     * @param userId 用户ID
     * @return 英语故事集合
     */
    @Override
    public List<Story> selectStoryListByUserId(Long userId)
    {
        return storyMapper.selectStoryListByUserId(userId);
    }

    /**
     * 根据主角ID查询故事列表
     * 
     * @param characterId 主角ID
     * @return 英语故事集合
     */
    @Override
    public List<Story> selectStoryListByCharacterId(Long characterId)
    {
        return storyMapper.selectStoryListByCharacterId(characterId);
    }

    /**
     * 新增英语故事
     * 
     * @param story 英语故事
     * @return 结果
     */
    @Override
    @Transactional
    public int insertStory(Story story)
    {
        story.setDelFlag("0");
        story.setIsFavorite(0);
        story.setViewCount(0);
        story.setShareCount(0);
        story.setCreatedAt(new Date());
        story.setUpdatedAt(new Date());
        
        int result = storyMapper.insertStory(story);
        
        // 增加主角故事计数
        if (story.getCharacterId() != null) {
            characterMapper.incrementStoryCount(story.getCharacterId());
        }
        
        return result;
    }

    /**
     * 修改英语故事
     * 
     * @param story 英语故事
     * @return 结果
     */
    @Override
    @Transactional
    public int updateStory(Story story)
    {
        story.setUpdatedAt(new Date());
        return storyMapper.updateStory(story);
    }

    /**
     * 删除英语故事对象
     * 
     * @param id 故事ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStoryById(Long id)
    {
        Story story = storyMapper.selectStoryById(id);
        if (story != null && story.getCharacterId() != null) {
            // 减少主角故事计数
            characterMapper.decrementStoryCount(story.getCharacterId());
        }
        return storyMapper.deleteStoryById(id);
    }

    /**
     * 批量删除英语故事
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStoryByIds(Long[] ids)
    {
        for (Long id : ids) {
            deleteStoryById(id);
        }
        return ids.length;
    }

    /**
     * 生成故事（核心功能）
     * 
     * @param userId 用户ID
     * @param request 生成故事请求
     * @return 生成故事响应
     */
    @Override
    @Transactional
    public GenerateStoryResponse generateStory(Long userId, GenerateStoryRequest request)
    {
        long startTime = System.currentTimeMillis();
        GenerateStoryResponse response = new GenerateStoryResponse();
        
        try {
            log.info("Starting story generation for user {} with character {}", userId, request.getCharacterId());
            
            // 1. 验证主角存在
            Character character = characterMapper.selectCharacterById(request.getCharacterId());
            if (character == null) {
                throw new RuntimeException("Character not found with ID: " + request.getCharacterId());
            }
            
            if (!character.getUserId().equals(userId)) {
                throw new RuntimeException("Character does not belong to the user");
            }
            
            // 2. 调用Vision API识别图片中的物品
            log.info("Analyzing image with Vision API");
            List<String> objects = deepseekApiClient.analyzeImage(request.getImage(), request.getImageType());
            
            if (objects.isEmpty()) {
                throw new RuntimeException("No objects identified in the image");
            }
            
            log.info("Identified {} objects: {}", objects.size(), objects);
            
            // 3. 调用Chat API生成故事
            log.info("Generating story with Chat API");
            String storyContent = deepseekApiClient.generateStory(objects, character.getName(), chatModel);
            
            if (storyContent == null || storyContent.trim().isEmpty()) {
                throw new RuntimeException("Failed to generate story content");
            }
            
            // 4. 生成故事标题（如果未提供）
            String title = request.getTitle();
            if (title == null || title.trim().isEmpty()) {
                title = generateStoryTitle(character.getName(), objects);
            }
            
            // 5. 生成向量嵌入（用于RAG搜索）
            log.info("Generating embedding for story");
            List<Double> embedding = deepseekService.embedding(storyContent);
            String embeddingJson = EmbeddingUtil.embeddingToJson(embedding);
            
            // 6. 保存故事到数据库
            Story story = new Story();
            story.setUserId(userId);
            story.setCharacterId(character.getId());
            story.setTitle(title);
            story.setContent(storyContent);
            story.setObjects(JSON.toJSONString(objects));
            story.setImageUrl(request.getImageType().equals("url") ? request.getImage() : null);
            story.setEmbedding(embeddingJson);
            story.setEmbeddingModel(embeddingModel);
            
            insertStory(story);
            
            // 7. 构建响应
            response.setSuccess(true);
            response.setStoryId(story.getId());
            response.setTitle(title);
            response.setContent(storyContent);
            response.setObjects(objects);
            response.setCharacterName(character.getName());
            response.setImageUrl(story.getImageUrl());
            response.setProcessingTime(System.currentTimeMillis() - startTime);
            
            log.info("Story generation completed successfully in {}ms", response.getProcessingTime());
            
        } catch (Exception e) {
            log.error("Failed to generate story: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
            response.setProcessingTime(System.currentTimeMillis() - startTime);
        }
        
        return response;
    }

    /**
     * 生成故事标题
     * 
     * @param characterName 主角名字
     * @param objects 物品列表
     * @return 标题
     */
    private String generateStoryTitle(String characterName, List<String> objects) {
        if (objects.isEmpty()) {
            return characterName + "'s Adventure";
        }
        
        // 取前3个物品生成标题
        List<String> topObjects = objects.subList(0, Math.min(3, objects.size()));
        String objectsStr = String.join(" and ", topObjects);
        return characterName + " and the " + objectsStr;
    }

    /**
     * 收藏故事
     * 
     * @param userId 用户ID
     * @param storyId 故事ID
     * @return 结果
     */
    @Override
    @Transactional
    public int favoriteStory(Long userId, Long storyId)
    {
        // 检查是否已收藏
        StoryFavorite existing = storyFavoriteMapper.selectByUserIdAndStoryId(userId, storyId);
        if (existing != null) {
            return 0; // 已收藏
        }
        
        StoryFavorite favorite = new StoryFavorite();
        favorite.setUserId(userId);
        favorite.setStoryId(storyId);
        favorite.setCreatedAt(new Date());
        
        int result = storyFavoriteMapper.insertStoryFavorite(favorite);
        
        // 更新故事的收藏标志
        if (result > 0) {
            Story story = new Story();
            story.setId(storyId);
            story.setIsFavorite(1);
            storyMapper.updateStory(story);
        }
        
        return result;
    }

    /**
     * 取消收藏故事
     * 
     * @param userId 用户ID
     * @param storyId 故事ID
     * @return 结果
     */
    @Override
    @Transactional
    public int unfavoriteStory(Long userId, Long storyId)
    {
        int result = storyFavoriteMapper.deleteByUserIdAndStoryId(userId, storyId);
        
        // 更新故事的收藏标志
        if (result > 0) {
            Story story = new Story();
            story.setId(storyId);
            story.setIsFavorite(0);
            storyMapper.updateStory(story);
        }
        
        return result;
    }

    /**
     * 搜索故事
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param similarityThreshold 相似度阈值
     * @param maxResults 最大结果数
     * @return 故事列表
     */
    @Override
    public List<Story> searchStories(Long userId, String keyword, Double similarityThreshold, Integer maxResults)
    {
        // 如果提供了关键词，使用全文搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            return storyMapper.searchStories(userId, keyword);
        }
        
        // 否则返回用户的所有故事
        return storyMapper.selectStoryListByUserId(userId);
    }

    /**
     * 根据分享token获取故事
     * 
     * @param shareToken 分享token
     * @return 故事
     */
    @Override
    public Story getStoryByShareToken(String shareToken)
    {
        Story story = storyMapper.selectStoryByShareToken(shareToken);
        if (story != null) {
            // 增加浏览次数
            storyMapper.incrementViewCount(story.getId());
        }
        return story;
    }

    /**
     * 生成分享token
     * 
     * @param storyId 故事ID
     * @return 分享token
     */
    @Override
    @Transactional
    public String generateShareToken(Long storyId)
    {
        Story story = storyMapper.selectStoryById(storyId);
        if (story == null) {
            throw new RuntimeException("Story not found");
        }
        
        // 如果已有分享token，直接返回
        if (story.getShareToken() != null && !story.getShareToken().isEmpty()) {
            return story.getShareToken();
        }
        
        // 生成新的分享token
        String shareToken = UUID.randomUUID().toString().replace("-", "");
        
        Story updateStory = new Story();
        updateStory.setId(storyId);
        updateStory.setShareToken(shareToken);
        storyMapper.updateStory(updateStory);
        
        // 增加分享次数
        storyMapper.incrementShareCount(storyId);
        
        return shareToken;
    }
}
