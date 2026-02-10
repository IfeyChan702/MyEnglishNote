package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 向量检索记录对象 embedding_record
 * 用于分析和优化向量检索效果
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public class EmbeddingRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 查询文本 */
    @Excel(name = "查询文本")
    private String queryText;

    /** 查询向量 */
    private String queryEmbedding;

    /** 检索结果(JSON格式) */
    private String results;

    /** 结果数量 */
    @Excel(name = "结果数量")
    private Integer resultCount;

    /** 相似度阈值 */
    @Excel(name = "相似度阈值")
    private BigDecimal similarityThreshold;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setQueryText(String queryText) 
    {
        this.queryText = queryText;
    }

    public String getQueryText() 
    {
        return queryText;
    }

    public void setQueryEmbedding(String queryEmbedding) 
    {
        this.queryEmbedding = queryEmbedding;
    }

    public String getQueryEmbedding() 
    {
        return queryEmbedding;
    }

    public void setResults(String results) 
    {
        this.results = results;
    }

    public String getResults() 
    {
        return results;
    }

    public void setResultCount(Integer resultCount) 
    {
        this.resultCount = resultCount;
    }

    public Integer getResultCount() 
    {
        return resultCount;
    }

    public void setSimilarityThreshold(BigDecimal similarityThreshold) 
    {
        this.similarityThreshold = similarityThreshold;
    }

    public BigDecimal getSimilarityThreshold() 
    {
        return similarityThreshold;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("queryText", getQueryText())
            .append("queryEmbedding", getQueryEmbedding())
            .append("results", getResults())
            .append("resultCount", getResultCount())
            .append("similarityThreshold", getSimilarityThreshold())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
