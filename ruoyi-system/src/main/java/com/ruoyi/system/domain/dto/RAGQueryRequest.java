package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * RAG查询请求DTO
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@ApiModel("RAG查询请求")
public class RAGQueryRequest
{
    @ApiModelProperty(value = "用户问题", required = true)
    @NotBlank(message = "问题不能为空")
    private String question;

    @ApiModelProperty(value = "相似度阈值(0-1)", example = "0.7")
    private Double similarityThreshold;

    @ApiModelProperty(value = "最大返回结果数", example = "5")
    private Integer maxResults;

    @ApiModelProperty(value = "是否包含上下文笔记", example = "true")
    private Boolean includeContext;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(Double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Boolean getIncludeContext() {
        return includeContext;
    }

    public void setIncludeContext(Boolean includeContext) {
        this.includeContext = includeContext;
    }
}
