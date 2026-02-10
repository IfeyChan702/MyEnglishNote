package com.ruoyi.system.service;

import java.util.List;

/**
 * 向量处理服务接口
 * 提供向量的序列化、反序列化、缓存管理等功能
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface IEmbeddingService {
    
    /**
     * 将向量列表转换为JSON字符串
     * 
     * @param embedding 向量列表
     * @return JSON字符串
     */
    String embeddingToJson(List<Double> embedding);
    
    /**
     * 将JSON字符串转换为向量列表
     * 
     * @param json JSON字符串
     * @return 向量列表
     */
    List<Double> jsonToEmbedding(String json);
    
    /**
     * 验证向量维度
     * 
     * @param embedding 向量
     * @param expectedDimension 期望的维度
     * @return 是否有效
     */
    boolean validateEmbedding(List<Double> embedding, int expectedDimension);
    
    /**
     * 归一化向量
     * 
     * @param embedding 原始向量
     * @return 归一化后的向量
     */
    List<Double> normalizeEmbedding(List<Double> embedding);
    
    /**
     * 从缓存中获取向量
     * 
     * @param key 缓存键
     * @return 向量列表
     */
    List<Double> getCachedEmbedding(String key);
    
    /**
     * 将向量存入缓存
     * 
     * @param key 缓存键
     * @param embedding 向量列表
     */
    void cacheEmbedding(String key, List<Double> embedding);
    
    /**
     * 清除向量缓存
     */
    void clearCache();
    
    /**
     * 获取缓存统计信息
     * 
     * @return 缓存大小
     */
    int getCacheSize();
}
