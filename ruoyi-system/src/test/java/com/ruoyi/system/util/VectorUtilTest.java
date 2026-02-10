package com.ruoyi.system.util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * VectorUtil 单元测试
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public class VectorUtilTest {

    /**
     * 测试余弦相似度计算
     */
    @Test
    public void testCosineSimilarity() {
        // 测试相同向量
        List<Double> v1 = Arrays.asList(1.0, 0.0, 0.0);
        List<Double> v2 = Arrays.asList(1.0, 0.0, 0.0);
        double similarity = VectorUtil.cosineSimilarity(v1, v2);
        assertEquals(1.0, similarity, 0.001);
        
        // 测试正交向量
        List<Double> v3 = Arrays.asList(1.0, 0.0, 0.0);
        List<Double> v4 = Arrays.asList(0.0, 1.0, 0.0);
        similarity = VectorUtil.cosineSimilarity(v3, v4);
        assertEquals(0.0, similarity, 0.001);
        
        // 测试相反向量
        List<Double> v5 = Arrays.asList(1.0, 0.0, 0.0);
        List<Double> v6 = Arrays.asList(-1.0, 0.0, 0.0);
        similarity = VectorUtil.cosineSimilarity(v5, v6);
        assertEquals(-1.0, similarity, 0.001);
    }
    
    /**
     * 测试欧氏距离计算
     */
    @Test
    public void testEuclideanDistance() {
        List<Double> v1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> v2 = Arrays.asList(4.0, 5.0, 6.0);
        double distance = VectorUtil.euclideanDistance(v1, v2);
        assertEquals(5.196, distance, 0.01);
        
        // 测试零距离
        List<Double> v3 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> v4 = Arrays.asList(1.0, 2.0, 3.0);
        distance = VectorUtil.euclideanDistance(v3, v4);
        assertEquals(0.0, distance, 0.001);
    }
    
    /**
     * 测试曼哈顿距离计算
     */
    @Test
    public void testManhattanDistance() {
        List<Double> v1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> v2 = Arrays.asList(4.0, 5.0, 6.0);
        double distance = VectorUtil.manhattanDistance(v1, v2);
        assertEquals(9.0, distance, 0.001);
        
        // 测试零距离
        List<Double> v3 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> v4 = Arrays.asList(1.0, 2.0, 3.0);
        distance = VectorUtil.manhattanDistance(v3, v4);
        assertEquals(0.0, distance, 0.001);
    }
    
    /**
     * 测试向量归一化
     */
    @Test
    public void testNormalizeVector() {
        List<Double> v1 = Arrays.asList(3.0, 4.0);
        List<Double> normalized = VectorUtil.normalizeVector(v1);
        
        // 归一化后模长应为1
        double norm = VectorUtil.norm(normalized);
        assertEquals(1.0, norm, 0.001);
        
        // 检查归一化值
        assertEquals(0.6, normalized.get(0), 0.001);
        assertEquals(0.8, normalized.get(1), 0.001);
    }
    
    /**
     * 测试向量点积
     */
    @Test
    public void testVectorDot() {
        List<Double> v1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> v2 = Arrays.asList(4.0, 5.0, 6.0);
        double dotProduct = VectorUtil.vectorDot(v1, v2);
        assertEquals(32.0, dotProduct, 0.001);
    }
    
    /**
     * 测试向量模长
     */
    @Test
    public void testVectorMagnitude() {
        List<Double> v1 = Arrays.asList(3.0, 4.0);
        double magnitude = VectorUtil.vectorMagnitude(v1);
        assertEquals(5.0, magnitude, 0.001);
        
        List<Double> v2 = Arrays.asList(1.0, 0.0, 0.0);
        magnitude = VectorUtil.vectorMagnitude(v2);
        assertEquals(1.0, magnitude, 0.001);
    }
    
    /**
     * 测试批量余弦相似度计算
     */
    @Test
    public void testBatchCosineSimilarity() {
        List<Double> queryVector = Arrays.asList(1.0, 0.0, 0.0);
        
        List<List<Double>> targetVectors = new ArrayList<>();
        targetVectors.add(Arrays.asList(1.0, 0.0, 0.0));  // 相似度 = 1.0
        targetVectors.add(Arrays.asList(0.0, 1.0, 0.0));  // 相似度 = 0.0
        targetVectors.add(Arrays.asList(-1.0, 0.0, 0.0)); // 相似度 = -1.0
        
        List<Double> similarities = VectorUtil.batchCosineSimilarity(queryVector, targetVectors);
        
        assertEquals(3, similarities.size());
        assertEquals(1.0, similarities.get(0), 0.001);
        assertEquals(0.0, similarities.get(1), 0.001);
        assertEquals(-1.0, similarities.get(2), 0.001);
    }
    
    /**
     * 测试异常情况：空向量
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCosineSimilarityWithNullVector() {
        VectorUtil.cosineSimilarity(null, Arrays.asList(1.0, 2.0));
    }
    
    /**
     * 测试异常情况：维度不匹配
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCosineSimilarityWithDimensionMismatch() {
        List<Double> v1 = Arrays.asList(1.0, 2.0);
        List<Double> v2 = Arrays.asList(1.0, 2.0, 3.0);
        VectorUtil.cosineSimilarity(v1, v2);
    }
    
    /**
     * 测试异常情况：零向量归一化
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeZeroVector() {
        List<Double> zeroVector = Arrays.asList(0.0, 0.0, 0.0);
        VectorUtil.normalizeVector(zeroVector);
    }
    
    /**
     * 测试缓存机制
     */
    @Test
    public void testCacheMechanism() {
        // 启用缓存
        VectorUtil.enableCache();
        
        List<Double> queryVector = Arrays.asList(1.0, 2.0, 3.0);
        List<List<Double>> targetVectors = new ArrayList<>();
        targetVectors.add(Arrays.asList(4.0, 5.0, 6.0));
        
        List<String> targetIds = new ArrayList<>();
        targetIds.add("note_1");
        
        // 第一次计算（写入缓存）
        List<Double> similarities1 = VectorUtil.batchCosineSimilarityWithCache(
            queryVector, targetVectors, targetIds
        );
        
        // 第二次计算（从缓存读取）
        List<Double> similarities2 = VectorUtil.batchCosineSimilarityWithCache(
            queryVector, targetVectors, targetIds
        );
        
        // 结果应该一致
        assertEquals(similarities1.get(0), similarities2.get(0), 0.0001);
        
        // 清除缓存
        VectorUtil.clearCache();
        assertEquals(0, VectorUtil.getCacheSize());
        
        // 禁用缓存
        VectorUtil.disableCache();
    }
    
    /**
     * 性能测试：1536维向量计算
     */
    @Test
    public void testPerformanceWith1536DimVectors() {
        // 生成1536维随机向量
        List<Double> queryVector = generateRandomVector(1536);
        List<List<Double>> targetVectors = new ArrayList<>();
        
        for (int i = 0; i < 1000; i++) {
            targetVectors.add(generateRandomVector(1536));
        }
        
        long startTime = System.currentTimeMillis();
        List<Double> similarities = VectorUtil.batchCosineSimilarity(queryVector, targetVectors);
        long elapsed = System.currentTimeMillis() - startTime;
        
        System.out.println("Calculated similarity for 1000 vectors (1536-dim) in " + elapsed + "ms");
        
        // 验证所有相似度都已计算
        assertEquals(1000, similarities.size());
        
        // 性能要求：1000个向量应在500ms内完成
        assertTrue("Performance test failed: " + elapsed + "ms > 500ms", elapsed < 500);
    }
    
    /**
     * 性能测试：批量计算 vs 单个计算
     */
    @Test
    public void testBatchVsSingleCalculation() {
        List<Double> queryVector = generateRandomVector(1536);
        List<List<Double>> targetVectors = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            targetVectors.add(generateRandomVector(1536));
        }
        
        // 批量计算
        long batchStartTime = System.currentTimeMillis();
        List<Double> batchSimilarities = VectorUtil.batchCosineSimilarity(queryVector, targetVectors);
        long batchElapsed = System.currentTimeMillis() - batchStartTime;
        
        // 单个计算
        long singleStartTime = System.currentTimeMillis();
        List<Double> singleSimilarities = new ArrayList<>();
        for (List<Double> targetVector : targetVectors) {
            singleSimilarities.add(VectorUtil.cosineSimilarity(queryVector, targetVector));
        }
        long singleElapsed = System.currentTimeMillis() - singleStartTime;
        
        System.out.println("Batch calculation: " + batchElapsed + "ms");
        System.out.println("Single calculation: " + singleElapsed + "ms");
        System.out.println("Speedup: " + (double)singleElapsed / batchElapsed + "x");
        
        // 结果应该一致
        for (int i = 0; i < batchSimilarities.size(); i++) {
            assertEquals(batchSimilarities.get(i), singleSimilarities.get(i), 0.0001);
        }
    }
    
    /**
     * 生成随机向量
     */
    private List<Double> generateRandomVector(int dimension) {
        List<Double> vector = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            vector.add(Math.random());
        }
        return vector;
    }
}
