package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 英语学习笔记对象 english_note
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public class EnglishNote extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 笔记ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 笔记内容 */
    @Excel(name = "笔记内容")
    private String content;

    /** 向量嵌入(JSON格式存储) */
    private String embedding;

    /** 嵌入模型名称 */
    @Excel(name = "嵌入模型")
    private String embeddingModel;

    /** 标签(逗号分隔) */
    @Excel(name = "标签")
    private String tags;

    /** 删除标志(0正常 1删除) */
    private String delFlag;

    /** 相似度得分 (用于查询结果，不存储在数据库) */
    private Double similarityScore;

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

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
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

    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("content", getContent())
            .append("embedding", getEmbedding())
            .append("embeddingModel", getEmbeddingModel())
            .append("tags", getTags())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
