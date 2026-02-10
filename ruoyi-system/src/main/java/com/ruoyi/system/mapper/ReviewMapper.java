package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ReviewRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 复习记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface ReviewMapper
{
    /**
     * 查询复习记录
     * 
     * @param id 记录ID
     * @return 复习记录
     */
    public ReviewRecord selectReviewRecordById(Long id);

    /**
     * 查询复习记录列表
     * 
     * @param reviewRecord 复习记录
     * @return 复习记录集合
     */
    public List<ReviewRecord> selectReviewRecordList(ReviewRecord reviewRecord);

    /**
     * 根据笔记ID查询最新的复习记录
     * 
     * @param noteId 笔记ID
     * @return 复习记录
     */
    public ReviewRecord selectLatestRecordByNoteId(Long noteId);

    /**
     * 查询需要复习的笔记
     * 
     * @param userId 用户ID
     * @param currentDate 当前日期
     * @param limit 返回结果数量
     * @return 复习记录集合(包含笔记内容)
     */
    public List<ReviewRecord> selectDueReviews(
        @Param("userId") Long userId,
        @Param("currentDate") Date currentDate,
        @Param("limit") Integer limit
    );

    /**
     * 新增复习记录
     * 
     * @param reviewRecord 复习记录
     * @return 结果
     */
    public int insertReviewRecord(ReviewRecord reviewRecord);

    /**
     * 修改复习记录
     * 
     * @param reviewRecord 复习记录
     * @return 结果
     */
    public int updateReviewRecord(ReviewRecord reviewRecord);

    /**
     * 删除复习记录
     * 
     * @param id 记录ID
     * @return 结果
     */
    public int deleteReviewRecordById(Long id);

    /**
     * 根据笔记ID删除复习记录
     * 
     * @param noteId 笔记ID
     * @return 结果
     */
    public int deleteReviewRecordByNoteId(Long noteId);

    /**
     * 统计待复习笔记数量
     * 
     * @param userId 用户ID
     * @param currentDate 当前日期
     * @return 待复习数量
     */
    public int countDueReviews(
        @Param("userId") Long userId,
        @Param("currentDate") Date currentDate
    );
}
