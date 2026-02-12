package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 英语故事对象 story
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public class Story extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 故事ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 主角ID */
    @Excel(name = "主角ID")
    private Long characterId;

    /** 故事标题 */
    @Excel(name = "故事标题")
    private String title;

    /** 故事内容 */
    @Excel(name = "故事内容")
    private String content;

    /** 识别出的物品列表(JSON) */
    private String objects;

    /** 原始图片URL */
    @Excel(name = "图片URL")
    private String imageUrl;

    /** 故事向量表示(JSON) */
    private String embedding;

    /** 嵌入模型名称 */
    @Excel(name = "嵌入模型")
    private String embeddingModel;

    /** 是否收藏 */
    @Excel(name = "是否收藏")
    private Integer isFavorite;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Integer viewCount;

    /** 分享次数 */
    @Excel(name = "分享次数")
    private Integer shareCount;

    /** 分享token */
    private String shareToken;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /** 删除标志(0正常 1删除) */
    private String delFlag;

    /** 相似度得分 (用于查询结果，不存储在数据库) */
    private Double similarityScore;

    /** 主角名称 (用于查询结果，不存储在数据库) */
    private String characterName;

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

    public void setCharacterId(Long characterId) 
    {
        this.characterId = characterId;
    }

    public Long getCharacterId() 
    {
        return characterId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setObjects(String objects) 
    {
        this.objects = objects;
    }

    public String getObjects() 
    {
        return objects;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    public void setEmbedding(String embedding) 
    {
        this.embedding = embedding;
    }

    public String getEmbedding() 
    {
        return embedding;
    }

    public void setEmbeddingModel(String embeddingModel) 
    {
        this.embeddingModel = embeddingModel;
    }

    public String getEmbeddingModel() 
    {
        return embeddingModel;
    }

    public void setIsFavorite(Integer isFavorite) 
    {
        this.isFavorite = isFavorite;
    }

    public Integer getIsFavorite() 
    {
        return isFavorite;
    }

    public void setViewCount(Integer viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Integer getViewCount() 
    {
        return viewCount;
    }

    public void setShareCount(Integer shareCount) 
    {
        this.shareCount = shareCount;
    }

    public Integer getShareCount() 
    {
        return shareCount;
    }

    public void setShareToken(String shareToken) 
    {
        this.shareToken = shareToken;
    }

    public String getShareToken() 
    {
        return shareToken;
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

    public Double getSimilarityScore() 
    {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) 
    {
        this.similarityScore = similarityScore;
    }

    public String getCharacterName() 
    {
        return characterName;
    }

    public void setCharacterName(String characterName) 
    {
        this.characterName = characterName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("characterId", getCharacterId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("objects", getObjects())
            .append("imageUrl", getImageUrl())
            .append("embedding", getEmbedding())
            .append("embeddingModel", getEmbeddingModel())
            .append("isFavorite", getIsFavorite())
            .append("viewCount", getViewCount())
            .append("shareCount", getShareCount())
            .append("shareToken", getShareToken())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
