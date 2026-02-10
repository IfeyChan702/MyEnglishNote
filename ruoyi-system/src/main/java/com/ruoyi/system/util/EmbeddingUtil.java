package com.ruoyi.system.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.system.domain.EnglishNote;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 向量处理工具类
 * 提供向量存储、检索和相似度搜索功能
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Component
public class EmbeddingUtil {

    /**
     * 将向量列表转换为JSON字符串
     * 
     * @param embedding 向量列表
     * @return JSON字符串
     */
    public static String embeddingToJson(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return null;
        }
        return JSON.toJSONString(embedding);
    }
    
    /**
     * 将JSON字符串转换为向量列表
     * 
     * @param json JSON字符串
     * @return 向量列表
     */
    public static List<Double> jsonToEmbedding(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            JSONArray jsonArray = JSON.parseArray(json);
            List<Double> embedding = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                embedding.add(jsonArray.getDouble(i));
            }
            return embedding;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse embedding JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * 计算查询向量与笔记列表的相似度，并设置到笔记对象中
     * 
     * @param queryEmbedding 查询向量
     * @param notes 笔记列表
     */
    public static void calculateAndSetSimilarityScores(List<Double> queryEmbedding, List<EnglishNote> notes) {
        if (queryEmbedding == null || notes == null || notes.isEmpty()) {
            return;
        }
        
        for (EnglishNote note : notes) {
            if (note.getEmbedding() != null && !note.getEmbedding().trim().isEmpty()) {
                try {
                    List<Double> noteEmbedding = jsonToEmbedding(note.getEmbedding());
                    if (noteEmbedding != null && noteEmbedding.size() == queryEmbedding.size()) {
                        double similarity = VectorUtil.cosineSimilarity(queryEmbedding, noteEmbedding);
                        note.setSimilarityScore(similarity);
                    }
                } catch (Exception e) {
                    // 忽略解析失败的向量，继续处理其他笔记
                    note.setSimilarityScore(0.0);
                }
            } else {
                note.setSimilarityScore(0.0);
            }
        }
    }
    
    /**
     * 根据相似度阈值过滤笔记
     * 
     * @param notes 笔记列表
     * @param threshold 相似度阈值
     * @return 过滤后的笔记列表
     */
    public static List<EnglishNote> filterByThreshold(List<EnglishNote> notes, double threshold) {
        if (notes == null || notes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<EnglishNote> filtered = new ArrayList<>();
        for (EnglishNote note : notes) {
            if (note.getSimilarityScore() != null && note.getSimilarityScore() >= threshold) {
                filtered.add(note);
            }
        }
        
        return filtered;
    }
    
    /**
     * 按相似度降序排序笔记列表
     * 
     * @param notes 笔记列表
     * @return 排序后的笔记列表
     */
    public static List<EnglishNote> sortBySimilarity(List<EnglishNote> notes) {
        if (notes == null || notes.isEmpty()) {
            return new ArrayList<>();
        }
        
        notes.sort((n1, n2) -> {
            Double score1 = n1.getSimilarityScore() != null ? n1.getSimilarityScore() : 0.0;
            Double score2 = n2.getSimilarityScore() != null ? n2.getSimilarityScore() : 0.0;
            return Double.compare(score2, score1); // 降序
        });
        
        return notes;
    }
    
    /**
     * 限制返回结果数量
     * 
     * @param notes 笔记列表
     * @param limit 最大数量
     * @return 限制后的笔记列表
     */
    public static List<EnglishNote> limitResults(List<EnglishNote> notes, int limit) {
        if (notes == null || notes.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (notes.size() <= limit) {
            return notes;
        }
        
        return notes.subList(0, limit);
    }
    
    /**
     * 验证向量维度是否匹配
     * 
     * @param embedding 向量
     * @param expectedDimension 期望的维度
     * @return 是否匹配
     */
    public static boolean validateDimension(List<Double> embedding, int expectedDimension) {
        return embedding != null && embedding.size() == expectedDimension;
    }
}
