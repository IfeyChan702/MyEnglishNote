package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 复习记录DTO
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@ApiModel("复习记录数据传输对象")
public class ReviewRecordDTO
{
    @ApiModelProperty("笔记ID")
    @NotNull(message = "笔记ID不能为空")
    private Long noteId;

    @ApiModelProperty(value = "复习质量(0-5分)", required = true)
    @NotNull(message = "复习质量不能为空")
    @Min(value = 0, message = "复习质量最小为0")
    @Max(value = 5, message = "复习质量最大为5")
    private Integer quality;

    @ApiModelProperty("笔记内容")
    private String noteContent;

    @ApiModelProperty("下次复习时间")
    private String nextReviewDate;

    @ApiModelProperty("复习间隔(天)")
    private Integer intervalDays;

    @ApiModelProperty("复习次数")
    private Integer repetitions;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(String nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }
}
