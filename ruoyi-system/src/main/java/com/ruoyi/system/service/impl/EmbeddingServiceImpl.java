package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.system.service.IEmbeddingService;
import com.ruoyi.system.util.VectorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 向量处理服务实现
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Service
public class EmbeddingServiceImpl implements IEmbeddingService {
    
    private static final Logger log = LoggerFactory.getLogger(EmbeddingServiceImpl.class);
    
    @Value("${rag.vector.dimension:1536}")
    private int expectedDimension;
    
    @Value("${rag.cache.enabled:false}")
    private boolean cacheEnabled;
    
    @Value("${rag.cache.max-size:10000}")
    private int maxCacheSize;
    
    // 向量缓存
    private final Map<String, List<Double>> embeddingCache = new ConcurrentHashMap<>();
    
    /**
     * 将向量列表转换为JSON字符串
     */
    @Override
    public String embeddingToJson(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return null;
        }
        
        long startTime = System.currentTimeMillis();
        String json = JSON.toJSONString(embedding);
        
        if (log.isDebugEnabled()) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.debug("Converted vector to JSON in {}ms (dimension: {})", elapsed, embedding.size());
        }
        
        return json;
    }
    
    /**
     * 将JSON字符串转换为向量列表
     */
    @Override
    public List<Double> jsonToEmbedding(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            JSONArray jsonArray = JSON.parseArray(json);
            List<Double> embedding = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                embedding.add(jsonArray.getDouble(i));
            }
            
            if (log.isDebugEnabled()) {
                long elapsed = System.currentTimeMillis() - startTime;
                log.debug("Converted JSON to vector in {}ms (dimension: {})", elapsed, embedding.size());
            }
            
            return embedding;
        } catch (Exception e) {
            log.error("Failed to parse embedding JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to parse embedding JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证向量维度
     */
    @Override
    public boolean validateEmbedding(List<Double> embedding, int expectedDimension) {
        if (embedding == null) {
            return false;
        }
        
        boolean valid = embedding.size() == expectedDimension;
        
        if (!valid) {
            log.warn("Invalid embedding dimension: expected {}, got {}", expectedDimension, embedding.size());
        }
        
        return valid;
    }
    
    /**
     * 归一化向量
     */
    @Override
    public List<Double> normalizeEmbedding(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("Embedding cannot be null or empty");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            List<Double> normalized = VectorUtil.normalizeVector(embedding);
            
            if (log.isDebugEnabled()) {
                long elapsed = System.currentTimeMillis() - startTime;
                log.debug("Normalized vector in {}ms", elapsed);
            }
            
            return normalized;
        } catch (Exception e) {
            log.error("Failed to normalize embedding: {}", e.getMessage());
            throw new RuntimeException("Failed to normalize embedding: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从缓存中获取向量
     */
    @Override
    public List<Double> getCachedEmbedding(String key) {
        if (!cacheEnabled || key == null) {
            return null;
        }
        
        List<Double> cached = embeddingCache.get(key);
        
        if (cached != null) {
            log.debug("Cache hit for key: {}", key);
        }
        
        return cached;
    }
    
    /**
     * 将向量存入缓存
     */
    @Override
    public void cacheEmbedding(String key, List<Double> embedding) {
        if (!cacheEnabled || key == null || embedding == null) {
            return;
        }
        
        // 检查缓存大小
        if (embeddingCache.size() >= maxCacheSize) {
            log.warn("Cache size limit reached ({}), clearing cache", maxCacheSize);
            clearCache();
        }
        
        embeddingCache.put(key, new ArrayList<>(embedding));
        log.debug("Cached embedding for key: {}", key);
    }
    
    /**
     * 清除向量缓存
     */
    @Override
    public void clearCache() {
        int size = embeddingCache.size();
        embeddingCache.clear();
        log.info("Cleared embedding cache ({} entries)", size);
    }
    
    /**
     * 获取缓存统计信息
     */
    @Override
    public int getCacheSize() {
        return embeddingCache.size();
    }
}
