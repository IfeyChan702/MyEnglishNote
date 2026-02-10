package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 笔记DTO
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@ApiModel("笔记数据传输对象")
public class NoteDTO
{
    @ApiModelProperty("笔记ID")
    private Long id;

    @ApiModelProperty("笔记内容")
    private String content;

    @ApiModelProperty("标签(逗号分隔)")
    private String tags;

    @ApiModelProperty("相似度得分")
    private Double similarityScore;

    @ApiModelProperty("创建时间")
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
