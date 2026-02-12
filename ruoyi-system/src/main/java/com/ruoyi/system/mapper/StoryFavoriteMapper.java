package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StoryFavorite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 故事收藏Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public interface StoryFavoriteMapper
{
    /**
     * 查询故事收藏
     * 
     * @param id 收藏ID
     * @return 故事收藏
     */
    public StoryFavorite selectStoryFavoriteById(Long id);

    /**
     * 查询用户收藏的故事ID列表
     * 
     * @param userId 用户ID
     * @return 故事ID集合
     */
    public List<Long> selectFavoriteStoryIdsByUserId(Long userId);

    /**
     * 检查用户是否收藏了故事
     * 
     * @param userId 用户ID
     * @param storyId 故事ID
     * @return 收藏记录
     */
    public StoryFavorite selectByUserIdAndStoryId(
        @Param("userId") Long userId,
        @Param("storyId") Long storyId
    );

    /**
     * 新增故事收藏
     * 
     * @param storyFavorite 故事收藏
     * @return 结果
     */
    public int insertStoryFavorite(StoryFavorite storyFavorite);

    /**
     * 删除故事收藏
     * 
     * @param id 收藏ID
     * @return 结果
     */
    public int deleteStoryFavoriteById(Long id);

    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param storyId 故事ID
     * @return 结果
     */
    public int deleteByUserIdAndStoryId(
        @Param("userId") Long userId,
        @Param("storyId") Long storyId
    );

    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    public int countFavoritesByUserId(Long userId);
}
