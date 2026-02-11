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
    
    @Value("${rag.deepseek.multimodal-model:deepseek-chat}")
    private String multimodalModel;
    
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
     * 调用Vision API识别图片中的物品
     * 
     * @param imageBase64 图片Base64编码（不含data:image前缀）或URL
     * @param imageType 图片类型: "base64" 或 "url"
     * @return 识别出的物品列表
     */
    public List<String> analyzeImage(String imageBase64, String imageType) {
        // 构建消息
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        
        // 构建内容数组
        List<Map<String, Object>> content = new ArrayList<>();
        
        // 添加文本部分
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("type", "text");
        textPart.put("text", "Please identify all objects, items, and things visible in this image. List them as simple English nouns, separated by commas. Only include the object names, no descriptions or explanations.");
        content.add(textPart);
        
        // 添加图片部分
        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        if ("url".equalsIgnoreCase(imageType)) {
            imageUrl.put("url", imageBase64);
        } else {
            // Base64格式需要添加data URI前缀
            if (!imageBase64.startsWith("data:image")) {
                // 检测图片类型，默认使用jpeg
                String mimeType = "jpeg";
                if (imageBase64.startsWith("/9j/")) {
                    mimeType = "jpeg";
                } else if (imageBase64.startsWith("iVBORw0KGgo")) {
                    mimeType = "png";
                } else if (imageBase64.startsWith("R0lGOD")) {
                    mimeType = "gif";
                }
                imageUrl.put("url", "data:image/" + mimeType + ";base64," + imageBase64);
            } else {
                imageUrl.put("url", imageBase64);
            }
        }
        imagePart.put("image_url", imageUrl);
        content.add(imagePart);
        
        message.put("content", content);
        messages.add(message);
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", multimodalModel);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 500);
        
        String url = apiEndpoint + chatPath;
        String responseBody = executeWithRetry(url, requestBody);
        
        if (responseBody == null) {
            throw new RuntimeException("Failed to get vision analysis from Deepseek API");
        }
        
        // 解析响应
        JSONObject response = JSON.parseObject(responseBody);
        JSONArray choices = response.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("Empty choices in vision response");
        }
        
        JSONObject choice = choices.getJSONObject(0);
        JSONObject responseMessage = choice.getJSONObject("message");
        String responseContent = responseMessage.getString("content");
        
        // 解析物品列表（假设返回的是逗号分隔的列表）
        List<String> objects = new ArrayList<>();
        if (responseContent != null && !responseContent.trim().isEmpty()) {
            String[] items = responseContent.split(",");
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    objects.add(trimmed);
                }
            }
        }
        
        log.info("Identified {} objects from image", objects.size());
        return objects;
    }
    
    /**
     * 基于物品列表生成故事
     * 
     * @param objects 物品列表
     * @param characterName 主角名字
     * @param model 模型名称
     * @return 生成的故事
     */
    public String generateStory(List<String> objects, String characterName, String model) {
        String objectsStr = String.join(", ", objects);
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 系统消息
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a creative English storyteller who writes engaging fairy tales for children. Your stories should be educational, fun, and include all the objects mentioned.");
        messages.add(systemMessage);
        
        // 用户消息
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", String.format(
            "Write a short English fairy tale (about 200-300 words) featuring a character named '%s'. " +
            "The story MUST include ALL of these objects: %s. " +
            "Make the story creative, educational, and appropriate for children. " +
            "Use simple English vocabulary suitable for language learners.",
            characterName, objectsStr
        ));
        messages.add(userMessage);
        
        return createChatCompletion(messages, model);
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
