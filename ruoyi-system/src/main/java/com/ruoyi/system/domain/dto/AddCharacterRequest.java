package com.ruoyi.system.domain.dto;

import javax.validation.constraints.NotBlank;

/**
 * 添加主角请求DTO
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public class AddCharacterRequest {
    
    /** 主角名字 */
    @NotBlank(message = "主角名字不能为空")
    private String name;
    
    /** 主角描述 */
    private String description;
    
    /** 主角头像URL */
    private String avatarUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
