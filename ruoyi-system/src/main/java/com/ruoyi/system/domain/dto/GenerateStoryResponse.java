package com.ruoyi.system.domain.dto;

import java.util.List;

/**
 * 生成故事响应DTO
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public class GenerateStoryResponse {
    
    /** 故事ID */
    private Long storyId;
    
    /** 故事标题 */
    private String title;
    
    /** 故事内容 */
    private String content;
    
    /** 识别出的物品列表 */
    private List<String> objects;
    
    /** 主角名字 */
    private String characterName;
    
    /** 图片URL */
    private String imageUrl;
    
    /** 是否成功 */
    private boolean success;
    
    /** 错误信息 */
    private String errorMessage;
    
    /** 处理时间(毫秒) */
    private Long processingTime;

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getObjects() {
        return objects;
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }
}
