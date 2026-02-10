package com.ruoyi.system.service;

import com.ruoyi.system.domain.EnglishNote;
import com.ruoyi.system.domain.dto.RAGResponse;

import java.util.List;

/**
 * RAG Service接口
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface IRAGService {
    
    /**
     * 向量检索相关笔记
     * 
     * @param userId 用户ID
     * @param query 查询文本
     * @param threshold 相似度阈值
     * @param maxResults 最大返回结果数
     * @return 相关笔记列表
     */
    List<EnglishNote> searchNotes(Long userId, String query, Double threshold, Integer maxResults);
    
    /**
     * 使用检索结果生成回答
     * 
     * @param userId 用户ID
     * @param question 用户问题
     * @param threshold 相似度阈值
     * @param maxResults 最大返回结果数
     * @param includeContext 是否包含上下文
     * @return RAG响应
     */
    RAGResponse generateAnswer(Long userId, String question, Double threshold, Integer maxResults, Boolean includeContext);
}
