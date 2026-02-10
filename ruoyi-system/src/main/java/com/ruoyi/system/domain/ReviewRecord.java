package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 复习记录对象 review_record
 * 支持SRS(Spaced Repetition System)算法
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public class ReviewRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 笔记ID */
    @Excel(name = "笔记ID")
    private Long noteId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 复习质量(0-5分) */
    @Excel(name = "复习质量")
    private Integer quality;

    /** 难度系数 */
    @Excel(name = "难度系数")
    private BigDecimal easinessFactor;

    /** 复习间隔(天) */
    @Excel(name = "复习间隔")
    private Integer intervalDays;

    /** 复习次数 */
    @Excel(name = "复习次数")
    private Integer repetitions;

    /** 下次复习时间 */
    @Excel(name = "下次复习时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date nextReviewDate;

    /** 复习时间 */
    @Excel(name = "复习时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reviewedAt;

    /** 笔记内容 (关联查询使用，不存储) */
    private String noteContent;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setNoteId(Long noteId) 
    {
        this.noteId = noteId;
    }

    public Long getNoteId() 
    {
        return noteId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setQuality(Integer quality) 
    {
        this.quality = quality;
    }

    public Integer getQuality() 
    {
        return quality;
    }

    public void setEasinessFactor(BigDecimal easinessFactor) 
    {
        this.easinessFactor = easinessFactor;
    }

    public BigDecimal getEasinessFactor() 
    {
        return easinessFactor;
    }

    public void setIntervalDays(Integer intervalDays) 
    {
        this.intervalDays = intervalDays;
    }

    public Integer getIntervalDays() 
    {
        return intervalDays;
    }

    public void setRepetitions(Integer repetitions) 
    {
        this.repetitions = repetitions;
    }

    public Integer getRepetitions() 
    {
        return repetitions;
    }

    public void setNextReviewDate(Date nextReviewDate) 
    {
        this.nextReviewDate = nextReviewDate;
    }

    public Date getNextReviewDate() 
    {
        return nextReviewDate;
    }

    public void setReviewedAt(Date reviewedAt) 
    {
        this.reviewedAt = reviewedAt;
    }

    public Date getReviewedAt() 
    {
        return reviewedAt;
    }

    public String getNoteContent() 
    {
        return noteContent;
    }

    public void setNoteContent(String noteContent) 
    {
        this.noteContent = noteContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("noteId", getNoteId())
            .append("userId", getUserId())
            .append("quality", getQuality())
            .append("easinessFactor", getEasinessFactor())
            .append("intervalDays", getIntervalDays())
            .append("repetitions", getRepetitions())
            .append("nextReviewDate", getNextReviewDate())
            .append("reviewedAt", getReviewedAt())
            .toString();
    }
}
