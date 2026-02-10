package com.ruoyi.system.service;

import com.ruoyi.system.domain.ReviewRecord;

import java.util.List;
import java.util.Map;

/**
 * Review Service接口
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface IReviewService {
    
    /**
     * 获取下一个待复习项目
     * 
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 待复习记录列表
     */
    List<ReviewRecord> getNextReviewItems(Long userId, Integer limit);
    
    /**
     * 记录复习结果
     * 
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @param quality 复习质量(0-5分)
     * @return 更新后的复习记录
     */
    ReviewRecord recordReview(Long noteId, Long userId, Integer quality);
    
    /**
     * 获取复习统计
     * 
     * @param userId 用户ID
     * @return 复习统计信息
     */
    Map<String, Object> getReviewStats(Long userId);
    
    /**
     * 初始化笔记的复习记录
     * 
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return 初始化的复习记录
     */
    ReviewRecord initializeReview(Long noteId, Long userId);
}
