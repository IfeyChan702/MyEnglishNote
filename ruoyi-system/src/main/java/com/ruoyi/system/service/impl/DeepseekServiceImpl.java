package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.IDeepseekService;
import com.ruoyi.system.util.DeepseekApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Deepseek Service实现
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Service
public class DeepseekServiceImpl implements IDeepseekService {

    private static final Logger log = LoggerFactory.getLogger(DeepseekServiceImpl.class);
    
    @Autowired
    private DeepseekApiClient apiClient;
    
    @Value("${rag.deepseek.embedding-model}")
    private String embeddingModel;
    
    @Value("${rag.deepseek.chat-model}")
    private String chatModel;
    
    @Value("${rag.prompt.system:你是一个专业的英语学习助手}")
    private String systemPrompt;
    
    @Value("${rag.prompt.context-template}")
    private String contextTemplate;
    
    /**
     * 生成文本的向量表示
     * 
     * @param text 输入文本
     * @return 向量列表
     */
    @Override
    public List<Double> embedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        
        try {
            log.debug("Generating embedding for text: {}", text.substring(0, Math.min(50, text.length())));
            List<Double> embedding = apiClient.createEmbedding(text, embeddingModel);
            log.debug("Successfully generated embedding with dimension: {}", embedding.size());
            return embedding;
        } catch (Exception e) {
            log.error("Failed to generate embedding: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }
    
    /**
     * 调用Chat API生成回答
     * 
     * @param messages 对话消息列表
     * @return AI生成的回答
     */
    @Override
    public String chat(List<Map<String, String>> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Messages cannot be null or empty");
        }
        
        try {
            log.debug("Calling chat API with {} messages", messages.size());
            String response = apiClient.createChatCompletion(messages, chatModel);
            log.debug("Successfully got chat response");
            return response;
        } catch (Exception e) {
            log.error("Failed to get chat completion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get chat completion: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成带上下文的回答
     * 
     * @param context 上下文文本
     * @param question 用户问题
     * @return AI生成的回答
     */
    @Override
    public String chatWithContext(String context, String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 添加系统提示词
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);
        
        // 构建用户消息
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        
        String userContent;
        if (context != null && !context.trim().isEmpty()) {
            // 使用模板组合上下文和问题
            userContent = contextTemplate
                    .replace("{context}", context)
                    .replace("{question}", question);
        } else {
            userContent = question;
        }
        
        userMessage.put("content", userContent);
        messages.add(userMessage);
        
        return chat(messages);
    }
}
