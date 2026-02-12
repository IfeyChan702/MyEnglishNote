package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Story;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 英语故事Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public interface StoryMapper
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
     * 删除英语故事
     * 
     * @param id 故事ID
     * @return 结果
     */
    public int deleteStoryById(Long id);

    /**
     * 批量删除英语故事
     * 
     * @param ids 需要删除的数据ID集合
     * @return 结果
     */
    public int deleteStoryByIds(Long[] ids);

    /**
     * 向量相似度搜索故事
     * 
     * @param userId 用户ID
     * @param embedding 查询向量(JSON字符串)
     * @param threshold 相似度阈值
     * @param limit 返回结果数量
     * @return 相似故事集合
     */
    public List<Story> searchSimilarStories(
        @Param("userId") Long userId,
        @Param("embedding") String embedding,
        @Param("threshold") Double threshold,
        @Param("limit") Integer limit
    );

    /**
     * 统计用户故事数量
     * 
     * @param userId 用户ID
     * @return 故事数量
     */
    public int countStoriesByUserId(Long userId);

    /**
     * 增加浏览次数
     * 
     * @param id 故事ID
     * @return 结果
     */
    public int incrementViewCount(Long id);

    /**
     * 增加分享次数
     * 
     * @param id 故事ID
     * @return 结果
     */
    public int incrementShareCount(Long id);

    /**
     * 根据分享token查询故事
     * 
     * @param shareToken 分享token
     * @return 故事
     */
    public Story selectStoryByShareToken(String shareToken);

    /**
     * 全文搜索故事
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 故事列表
     */
    public List<Story> searchStories(
        @Param("userId") Long userId,
        @Param("keyword") String keyword
    );
}
