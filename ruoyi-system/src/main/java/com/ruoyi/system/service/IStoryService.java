package com.ruoyi.system.service;

import com.ruoyi.system.domain.Story;
import com.ruoyi.system.domain.dto.GenerateStoryRequest;
import com.ruoyi.system.domain.dto.GenerateStoryResponse;

import java.util.List;

/**
 * 英语故事Service接口
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public interface IStoryService
{
    /**
     * 查询英语故事
     * 
     * @param id 故事ID
     * @return 英语故事
     */
    public Story selectStoryById(Long id);

    /**
     * 查询英语故事列表
     * 
     * @param story 英语故事
     * @return 英语故事集合
     */
    public List<Story> selectStoryList(Story story);

    /**
     * 根据用户ID查询故事列表
     * 
     * @param userId 用户ID
     * @return 英语故事集合
     */
    public List<Story> selectStoryListByUserId(Long userId);

    /**
     * 根据主角ID查询故事列表
     * 
     * @param characterId 主角ID
     * @return 英语故事集合
     */
    public List<Story> selectStoryListByCharacterId(Long characterId);

    /**
     * 新增英语故事
     * 
     * @param story 英语故事
     * @return 结果
     */
    public int insertStory(Story story);

    /**
     * 修改英语故事
     * 
     * @param story 英语故事
     * @return 结果
     */
    public int updateStory(Story story);

    /**
     * 删除英语故事信息
     * 
     * @param id 故事ID
     * @return 结果
     */
    public int deleteStoryById(Long id);

    /**
     * 批量删除英语故事
     * 
     * @param ids 需要删除的故事ID
     * @return 结果
     */
    public int deleteStoryByIds(Long[] ids);

    /**
     * 生成故事（核心功能）
     * 
     * @param userId 用户ID
     * @param request 生成故事请求
     * @return 生成故事响应
     */
    public GenerateStoryResponse generateStory(Long userId, GenerateStoryRequest request);

    /**
     * 收藏故事
     * 
     * @param userId 用户ID
     * @param storyId 故事ID
     * @return 结果
     */
    public int favoriteStory(Long userId, Long storyId);

    /**
     * 取消收藏故事
     * 
     * @param userId 用户ID
     * @param storyId 故事ID
     * @return 结果
     */
    public int unfavoriteStory(Long userId, Long storyId);

    /**
     * 搜索故事
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param similarityThreshold 相似度阈值
     * @param maxResults 最大结果数
     * @return 故事列表
     */
    public List<Story> searchStories(Long userId, String keyword, Double similarityThreshold, Integer maxResults);

    /**
     * 根据分享token获取故事
     * 
     * @param shareToken 分享token
     * @return 故事
     */
    public Story getStoryByShareToken(String shareToken);

    /**
     * 生成分享token
     * 
     * @param storyId 故事ID
     * @return 分享token
     */
    public String generateShareToken(Long storyId);
}
