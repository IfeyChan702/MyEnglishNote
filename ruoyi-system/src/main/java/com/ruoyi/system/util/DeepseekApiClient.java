package com.ruoyi.system.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Deepseek API HTTP客户端
 * 提供embedding和chat接口调用
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Component
public class DeepseekApiClient {

    private static final Logger log = LoggerFactory.getLogger(DeepseekApiClient.class);
    
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    
    @Value("${rag.deepseek.api-key}")
    private String apiKey;
    
    @Value("${rag.deepseek.api-endpoint}")
    private String apiEndpoint;
    
    @Value("${rag.deepseek.embedding-path}")
    private String embeddingPath;
    
    @Value("${rag.deepseek.chat-path}")
    private String chatPath;
    
    @Value("${rag.deepseek.timeout:30}")
    private int timeout;
    
    @Value("${rag.deepseek.max-retries:3}")
    private int maxRetries;
    
    private OkHttpClient client;
    
    /**
     * 初始化HTTP客户端
     */
    private OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }
    
    /**
     * 调用Embedding API生成向量
     * 
     * @param text 输入文本
     * @param model 模型名称
     * @return 向量数组
     */
    public List<Double> createEmbedding(String text, String model) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("input", text);
        
        String url = apiEndpoint + embeddingPath;
        String responseBody = executeWithRetry(url, requestBody);
        
        if (responseBody == null) {
            throw new RuntimeException("Failed to get embedding from Deepseek API");
        }
        
        // 解析响应
        JSONObject response = JSON.parseObject(responseBody);
        JSONArray data = response.getJSONArray("data");
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("Empty embedding data in response");
        }
        
        JSONObject embeddingObj = data.getJSONObject(0);
        JSONArray embeddingArray = embeddingObj.getJSONArray("embedding");
        
        List<Double> embedding = new ArrayList<>();
        for (int i = 0; i < embeddingArray.size(); i++) {
            embedding.add(embeddingArray.getDouble(i));
        }
        
        return embedding;
    }
    
    /**
     * 调用Chat API生成回答
     * 
     * @param messages 对话消息列表
     * @param model 模型名称
     * @return 生成的回答
     */
    public String createChatCompletion(List<Map<String, String>> messages, String model) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        
        String url = apiEndpoint + chatPath;
        String responseBody = executeWithRetry(url, requestBody);
        
        if (responseBody == null) {
            throw new RuntimeException("Failed to get chat completion from Deepseek API");
        }
        
        // 解析响应
        JSONObject response = JSON.parseObject(responseBody);
        JSONArray choices = response.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("Empty choices in chat completion response");
        }
        
        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");
        
        return message.getString("content");
    }
    
    /**
     * 带重试机制的HTTP请求执行
     * 
     * @param url 请求URL
     * @param requestBody 请求体
     * @return 响应内容
     */
    private String executeWithRetry(String url, Map<String, Object> requestBody) {
        log.info("Requesting URL: {}", url);  // ← 添加这行
        int retries = 0;
        Exception lastException = null;
        
        while (retries < maxRetries) {
            try {
                return execute(url, requestBody);
            } catch (Exception e) {
                lastException = e;
                retries++;
                log.warn("Request failed (attempt {}/{}): {}", retries, maxRetries, e.getMessage());
                
                if (retries < maxRetries) {
                    try {
                        // 指数退避
                        Thread.sleep((long) Math.pow(2, retries) * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Request interrupted", ie);
                    }
                }
            }
        }
        
        log.error("Request failed after {} retries", maxRetries, lastException);
        throw new RuntimeException("Request failed after " + maxRetries + " retries", lastException);
    }
    
    /**
     * 执行HTTP POST请求
     * 
     * @param url 请求URL
     * @param requestBody 请求体
     * @return 响应内容
     */
    private String execute(String url, Map<String, Object> requestBody) throws IOException {
        String jsonBody = JSON.toJSONString(requestBody);

        log.info("🔍 Deepseek API Request:");
        log.info("   URL: {}", url);
        log.info("   Method: POST");
        log.info("   Body: {}", jsonBody);
        log.info("   API Key: {}", apiKey != null ? apiKey.substring(0, 10) + "..." : "null");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(JSON_MEDIA_TYPE, jsonBody))
                .build();

        try (Response response = getClient().newCall(request).execute()) {
            log.info("📡 Response Status: {}", response.code());

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                log.error("❌ API Error ({}): {}", response.code(), errorBody);
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + errorBody);
            }

            String result = response.body() != null ? response.body().string() : null;
            log.info("✅ Success Response: {}", result);
            return result;
        }
    }
}
