package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 故事主角对象 character
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public class Character extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主角ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 主角名字 */
    @Excel(name = "主角名字")
    private String name;

    /** 主角描述 */
    @Excel(name = "主角描述")
    private String description;

    /** 主角头像URL */
    @Excel(name = "主角头像")
    private String avatarUrl;

    /** 相关故事数量 */
    @Excel(name = "故事数量")
    private Integer storyCount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /** 删除标志(0正常 1删除) */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setAvatarUrl(String avatarUrl) 
    {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() 
    {
        return avatarUrl;
    }

    public void setStoryCount(Integer storyCount) 
    {
        this.storyCount = storyCount;
    }

    public Integer getStoryCount() 
    {
        return storyCount;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("name", getName())
            .append("description", getDescription())
            .append("avatarUrl", getAvatarUrl())
            .append("storyCount", getStoryCount())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
