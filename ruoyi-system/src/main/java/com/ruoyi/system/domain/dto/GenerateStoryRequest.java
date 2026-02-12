package com.ruoyi.system.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 生成故事请求DTO
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public class GenerateStoryRequest {
    
    /** 图片Base64编码或URL */
    @NotBlank(message = "图片不能为空")
    private String image;
    
    /** 图片类型: base64 或 url */
    private String imageType = "base64";
    
    /** 主角ID */
    @NotNull(message = "主角ID不能为空")
    private Long characterId;
    
    /** 故事标题（可选，不提供则自动生成） */
    private String title;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
