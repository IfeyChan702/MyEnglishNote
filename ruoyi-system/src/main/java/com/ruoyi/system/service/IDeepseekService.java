package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

/**
 * Deepseek Service接口
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface IDeepseekService {
    
    /**
     * 生成文本的向量表示
     * 
     * @param text 输入文本
     * @return 向量列表
     */
    List<Double> embedding(String text);
    
    /**
     * 调用Chat API生成回答
     * 
     * @param messages 对话消息列表
     * @return AI生成的回答
     */
    String chat(List<Map<String, String>> messages);
    
    /**
     * 生成带上下文的回答
     * 
     * @param context 上下文文本
     * @param question 用户问题
     * @return AI生成的回答
     */
    String chatWithContext(String context, String question);
}
