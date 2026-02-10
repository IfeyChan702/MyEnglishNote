package com.ruoyi.system.service;

import com.ruoyi.system.service.impl.EmbeddingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * EmbeddingService 单元测试
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@RunWith(SpringRunner.class)
public class EmbeddingServiceTest {

    private IEmbeddingService embeddingService;
    
    @Before
    public void setUp() {
        embeddingService = new EmbeddingServiceImpl();
        
        // 设置测试配置
        ReflectionTestUtils.setField(embeddingService, "expectedDimension", 1536);
        ReflectionTestUtils.setField(embeddingService, "cacheEnabled", true);
        ReflectionTestUtils.setField(embeddingService, "maxCacheSize", 100);
    }
    
    /**
     * 测试向量到JSON的转换
     */
    @Test
    public void testEmbeddingToJson() {
        List<Double> embedding = Arrays.asList(0.1, 0.2, 0.3);
        String json = embeddingService.embeddingToJson(embedding);
        
        assertNotNull(json);
        assertTrue(json.contains("0.1"));
        assertTrue(json.contains("0.2"));
        assertTrue(json.contains("0.3"));
    }
    
    /**
     * 测试JSON到向量的转换
     */
    @Test
    public void testJsonToEmbedding() {
        String json = "[0.1,0.2,0.3]";
        List<Double> embedding = embeddingService.jsonToEmbedding(json);
        
        assertNotNull(embedding);
        assertEquals(3, embedding.size());
        assertEquals(0.1, embedding.get(0), 0.0001);
        assertEquals(0.2, embedding.get(1), 0.0001);
        assertEquals(0.3, embedding.get(2), 0.0001);
    }
    
    /**
     * 测试向量JSON往返转换
     */
    @Test
    public void testRoundTripConversion() {
        List<Double> original = new ArrayList<>();
        for (int i = 0; i < 1536; i++) {
            original.add(Math.random());
        }
        
        // 转换为JSON
        String json = embeddingService.embeddingToJson(original);
        assertNotNull(json);
        
        // 转换回向量
        List<Double> recovered = embeddingService.jsonToEmbedding(json);
        assertNotNull(recovered);
        
        // 验证维度
        assertEquals(original.size(), recovered.size());
        
        // 验证值
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i), recovered.get(i), 0.0001);
        }
    }
    
    /**
     * 测试向量维度验证
     */
    @Test
    public void testValidateEmbedding() {
        // 正确维度
        List<Double> validEmbedding = generateRandomVector(1536);
        boolean valid = embeddingService.validateEmbedding(validEmbedding, 1536);
        assertTrue(valid);
        
        // 错误维度
        List<Double> invalidEmbedding = generateRandomVector(1024);
        valid = embeddingService.validateEmbedding(invalidEmbedding, 1536);
        assertFalse(valid);
        
        // null向量
        valid = embeddingService.validateEmbedding(null, 1536);
        assertFalse(valid);
    }
    
    /**
     * 测试向量归一化
     */
    @Test
    public void testNormalizeEmbedding() {
        List<Double> embedding = Arrays.asList(3.0, 4.0);
        List<Double> normalized = embeddingService.normalizeEmbedding(embedding);
        
        assertNotNull(normalized);
        assertEquals(2, normalized.size());
        
        // 归一化后模长应为1
        double sumOfSquares = 0.0;
        for (Double value : normalized) {
            sumOfSquares += value * value;
        }
        assertEquals(1.0, Math.sqrt(sumOfSquares), 0.0001);
    }
    
    /**
     * 测试缓存功能
     */
    @Test
    public void testCacheFunctionality() {
        String key = "test_key_1";
        List<Double> embedding = Arrays.asList(0.1, 0.2, 0.3);
        
        // 存入缓存
        embeddingService.cacheEmbedding(key, embedding);
        
        // 从缓存读取
        List<Double> cached = embeddingService.getCachedEmbedding(key);
        assertNotNull(cached);
        assertEquals(3, cached.size());
        assertEquals(0.1, cached.get(0), 0.0001);
        
        // 检查缓存大小
        assertTrue(embeddingService.getCacheSize() > 0);
        
        // 清除缓存
        embeddingService.clearCache();
        assertEquals(0, embeddingService.getCacheSize());
        
        // 缓存应该为空
        cached = embeddingService.getCachedEmbedding(key);
        assertNull(cached);
    }
    
    /**
     * 测试缓存LRU淘汰
     */
    @Test
    public void testCacheLRUEviction() {
        // 填满缓存（maxCacheSize = 100）
        for (int i = 0; i < 101; i++) {
            String key = "key_" + i;
            List<Double> embedding = generateRandomVector(10);
            embeddingService.cacheEmbedding(key, embedding);
        }
        
        // 缓存应该被清空一次
        assertTrue(embeddingService.getCacheSize() <= 100);
    }
    
    /**
     * 测试空JSON处理
     */
    @Test
    public void testEmptyJsonHandling() {
        String emptyJson = "";
        List<Double> embedding = embeddingService.jsonToEmbedding(emptyJson);
        assertNull(embedding);
        
        String nullJson = null;
        embedding = embeddingService.jsonToEmbedding(nullJson);
        assertNull(embedding);
    }
    
    /**
     * 测试空向量处理
     */
    @Test
    public void testEmptyEmbeddingHandling() {
        List<Double> emptyEmbedding = new ArrayList<>();
        String json = embeddingService.embeddingToJson(emptyEmbedding);
        assertNull(json);
        
        List<Double> nullEmbedding = null;
        json = embeddingService.embeddingToJson(nullEmbedding);
        assertNull(json);
    }
    
    /**
     * 测试无效JSON处理
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidJsonHandling() {
        String invalidJson = "{invalid json}";
        embeddingService.jsonToEmbedding(invalidJson);
    }
    
    /**
     * 测试性能：大批量JSON转换
     */
    @Test
    public void testPerformanceBatchJsonConversion() {
        List<List<Double>> embeddings = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            embeddings.add(generateRandomVector(1536));
        }
        
        // 转换为JSON
        long startTime = System.currentTimeMillis();
        List<String> jsonList = new ArrayList<>();
        for (List<Double> embedding : embeddings) {
            jsonList.add(embeddingService.embeddingToJson(embedding));
        }
        long jsonConversionTime = System.currentTimeMillis() - startTime;
        
        // 转换回向量
        startTime = System.currentTimeMillis();
        List<List<Double>> recovered = new ArrayList<>();
        for (String json : jsonList) {
            recovered.add(embeddingService.jsonToEmbedding(json));
        }
        long embeddingConversionTime = System.currentTimeMillis() - startTime;
        
        System.out.println("JSON conversion time (1000 vectors): " + jsonConversionTime + "ms");
        System.out.println("Embedding conversion time (1000 vectors): " + embeddingConversionTime + "ms");
        
        assertEquals(1000, recovered.size());
        
        // 性能要求：1000个向量的JSON转换应在1秒内完成
        assertTrue("JSON conversion too slow: " + jsonConversionTime + "ms", 
                   jsonConversionTime < 1000);
        assertTrue("Embedding conversion too slow: " + embeddingConversionTime + "ms", 
                   embeddingConversionTime < 1000);
    }
    
    /**
     * 测试性能：缓存命中率
     */
    @Test
    public void testCacheHitRate() {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            keys.add("key_" + i);
        }
        
        // 写入缓存
        for (String key : keys) {
            embeddingService.cacheEmbedding(key, generateRandomVector(1536));
        }
        
        // 测试缓存命中
        int hits = 0;
        int queries = 1000;
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < queries; i++) {
            String key = keys.get(i % 100);
            List<Double> cached = embeddingService.getCachedEmbedding(key);
            if (cached != null) {
                hits++;
            }
        }
        long elapsed = System.currentTimeMillis() - startTime;
        
        double hitRate = (double) hits / queries * 100;
        
        System.out.println("Cache hit rate: " + hitRate + "%");
        System.out.println("Cache queries time: " + elapsed + "ms");
        
        // 缓存命中率应该100%（因为都是重复查询）
        assertEquals(100.0, hitRate, 0.1);
    }
    
    /**
     * 测试归一化后的向量维度保持不变
     */
    @Test
    public void testNormalizationPreservesDimension() {
        List<Double> original = generateRandomVector(1536);
        List<Double> normalized = embeddingService.normalizeEmbedding(original);
        
        assertEquals(original.size(), normalized.size());
    }
    
    /**
     * 测试零向量归一化异常
     */
    @Test(expected = RuntimeException.class)
    public void testNormalizeZeroVector() {
        List<Double> zeroVector = new ArrayList<>();
        for (int i = 0; i < 1536; i++) {
            zeroVector.add(0.0);
        }
        embeddingService.normalizeEmbedding(zeroVector);
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
