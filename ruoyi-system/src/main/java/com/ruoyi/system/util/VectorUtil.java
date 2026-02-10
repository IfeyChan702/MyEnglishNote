package com.ruoyi.system.util;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 向量计算工具类
 * 提供余弦相似度、欧氏距离、曼哈顿距离、向量归一化等功能
 * 支持批量并行计算和缓存机制
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public class VectorUtil {
    
    private static final Logger log = LoggerFactory.getLogger(VectorUtil.class);
    
    // 缓存机制（可选）
    private static final Map<String, Double> similarityCache = new ConcurrentHashMap<>();
    private static boolean cacheEnabled = false;
    private static final int MAX_CACHE_SIZE = 10000;

    /**
     * 计算两个向量的余弦相似度
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度 (范围: -1 到 1)
     */
    public static double cosineSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double[] array1 = vector1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = vector2.stream().mapToDouble(Double::doubleValue).toArray();
        
        RealVector v1 = new ArrayRealVector(array1);
        RealVector v2 = new ArrayRealVector(array2);
        
        double dotProduct = v1.dotProduct(v2);
        double norm1 = v1.getNorm();
        double norm2 = v2.getNorm();
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * 计算两个向量的余弦相似度（double数组版本）
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度
     */
    public static double cosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1 == null || vector2 == null || vector1.length == 0 || vector2.length == 0) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        RealVector v1 = new ArrayRealVector(vector1);
        RealVector v2 = new ArrayRealVector(vector2);
        
        double dotProduct = v1.dotProduct(v2);
        double norm1 = v1.getNorm();
        double norm2 = v2.getNorm();
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * 归一化向量（使向量的模为1）
     * 
     * @param vector 输入向量
     * @return 归一化后的向量
     */
    public static List<Double> normalizeVector(List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            throw new IllegalArgumentException("Vector cannot be null or empty");
        }
        
        double[] array = vector.stream().mapToDouble(Double::doubleValue).toArray();
        RealVector v = new ArrayRealVector(array);
        
        double norm = v.getNorm();
        if (norm == 0) {
            throw new IllegalArgumentException("Cannot normalize a zero vector");
        }
        
        RealVector normalized = v.mapDivide(norm);
        
        double[] normalizedArray = normalized.toArray();
        List<Double> result = new java.util.ArrayList<>();
        for (double value : normalizedArray) {
            result.add(value);
        }
        
        return result;
    }
    
    /**
     * 计算向量的欧几里得距离
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 欧几里得距离
     */
    public static double euclideanDistance(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double[] array1 = vector1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = vector2.stream().mapToDouble(Double::doubleValue).toArray();
        
        RealVector v1 = new ArrayRealVector(array1);
        RealVector v2 = new ArrayRealVector(array2);
        
        return v1.getDistance(v2);
    }
    
    /**
     * 计算向量的点积
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 点积
     */
    public static double dotProduct(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double[] array1 = vector1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = vector2.stream().mapToDouble(Double::doubleValue).toArray();
        
        RealVector v1 = new ArrayRealVector(array1);
        RealVector v2 = new ArrayRealVector(array2);
        
        return v1.dotProduct(v2);
    }
    
    /**
     * 计算向量的模（L2范数）
     * 
     * @param vector 输入向量
     * @return 向量的模
     */
    public static double norm(List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            throw new IllegalArgumentException("Vector cannot be null or empty");
        }
        
        double[] array = vector.stream().mapToDouble(Double::doubleValue).toArray();
        RealVector v = new ArrayRealVector(array);
        
        return v.getNorm();
    }
    
    /**
     * 计算曼哈顿距离（L1距离）
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 曼哈顿距离
     */
    public static double manhattanDistance(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double distance = 0.0;
        for (int i = 0; i < vector1.size(); i++) {
            distance += Math.abs(vector1.get(i) - vector2.get(i));
        }
        
        return distance;
    }
    
    /**
     * 批量计算余弦相似度（并行处理）
     * 
     * @param queryVector 查询向量
     * @param targetVectors 目标向量列表
     * @return 相似度列表（与targetVectors顺序一致）
     */
    public static List<Double> batchCosineSimilarity(List<Double> queryVector, List<List<Double>> targetVectors) {
        if (queryVector == null || targetVectors == null || queryVector.isEmpty() || targetVectors.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        long startTime = System.currentTimeMillis();
        
        // 使用并行流处理
        List<Double> similarities = targetVectors.parallelStream()
            .map(targetVector -> {
                try {
                    return cosineSimilarity(queryVector, targetVector);
                } catch (Exception e) {
                    log.warn("Failed to calculate similarity: {}", e.getMessage());
                    return 0.0;
                }
            })
            .collect(Collectors.toList());
        
        long endTime = System.currentTimeMillis();
        log.debug("Batch similarity calculation completed in {}ms for {} vectors", 
                  endTime - startTime, targetVectors.size());
        
        return similarities;
    }
    
    /**
     * 批量计算余弦相似度（带缓存和并行处理）
     * 
     * @param queryVector 查询向量
     * @param targetVectors 目标向量列表
     * @param targetIds 目标向量ID列表（用于缓存键）
     * @return 相似度列表（与targetVectors顺序一致）
     */
    public static List<Double> batchCosineSimilarityWithCache(
            List<Double> queryVector, 
            List<List<Double>> targetVectors,
            List<String> targetIds) {
        
        if (queryVector == null || targetVectors == null || queryVector.isEmpty() || targetVectors.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (targetIds != null && targetIds.size() != targetVectors.size()) {
            throw new IllegalArgumentException("Target IDs size must match target vectors size");
        }
        
        long startTime = System.currentTimeMillis();
        String queryKey = generateVectorKey(queryVector);
        
        List<Double> similarities = new ArrayList<>(targetVectors.size());
        for (int i = 0; i < targetVectors.size(); i++) {
            List<Double> targetVector = targetVectors.get(i);
            String targetId = targetIds != null ? targetIds.get(i) : null;
            
            Double similarity;
            if (cacheEnabled && targetId != null) {
                String cacheKey = queryKey + "_" + targetId;
                similarity = similarityCache.computeIfAbsent(cacheKey, k -> {
                    // 检查缓存大小
                    if (similarityCache.size() > MAX_CACHE_SIZE) {
                        clearCache();
                    }
                    return cosineSimilarity(queryVector, targetVector);
                });
            } else {
                similarity = cosineSimilarity(queryVector, targetVector);
            }
            
            similarities.add(similarity);
        }
        
        long endTime = System.currentTimeMillis();
        log.debug("Batch similarity calculation with cache completed in {}ms for {} vectors", 
                  endTime - startTime, targetVectors.size());
        
        return similarities;
    }
    
    /**
     * 计算向量的点积（优化版本，不使用Apache Commons Math）
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 点积
     */
    public static double vectorDot(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double dotProduct = 0.0;
        for (int i = 0; i < vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
        }
        
        return dotProduct;
    }
    
    /**
     * 计算向量的模长（优化版本，不使用Apache Commons Math）
     * 
     * @param vector 输入向量
     * @return 向量的模长
     */
    public static double vectorMagnitude(List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            throw new IllegalArgumentException("Vector cannot be null or empty");
        }
        
        double sumOfSquares = 0.0;
        for (Double value : vector) {
            sumOfSquares += value * value;
        }
        
        return Math.sqrt(sumOfSquares);
    }
    
    /**
     * 启用缓存机制
     */
    public static void enableCache() {
        cacheEnabled = true;
        log.info("Vector similarity cache enabled");
    }
    
    /**
     * 禁用缓存机制
     */
    public static void disableCache() {
        cacheEnabled = false;
        clearCache();
        log.info("Vector similarity cache disabled");
    }
    
    /**
     * 清除缓存
     */
    public static void clearCache() {
        similarityCache.clear();
        log.debug("Vector similarity cache cleared");
    }
    
    /**
     * 获取缓存统计信息
     * 
     * @return 缓存大小
     */
    public static int getCacheSize() {
        return similarityCache.size();
    }
    
    /**
     * 生成向量的缓存键
     * 
     * @param vector 向量
     * @return 缓存键
     */
    private static String generateVectorKey(List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            return "";
        }
        
        // 使用向量的前几个值和长度生成简单的键
        // 注意：这不是完美的哈希，但对于缓存足够了
        StringBuilder sb = new StringBuilder();
        sb.append(vector.size()).append("_");
        int sampleSize = Math.min(5, vector.size());
        for (int i = 0; i < sampleSize; i++) {
            sb.append(String.format("%.4f", vector.get(i))).append("_");
        }
        
        return sb.toString();
    }
}
