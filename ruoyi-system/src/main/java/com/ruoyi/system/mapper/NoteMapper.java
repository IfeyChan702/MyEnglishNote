package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.EnglishNote;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 英语笔记Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface NoteMapper
{
    /**
     * 查询英语笔记
     * 
     * @param id 笔记ID
     * @return 英语笔记
     */
    public EnglishNote selectNoteById(Long id);

    /**
     * 查询英语笔记列表
     * 
     * @param note 英语笔记
     * @return 英语笔记集合
     */
    public List<EnglishNote> selectNoteList(EnglishNote note);

    /**
     * 根据用户ID查询笔记列表
     * 
     * @param userId 用户ID
     * @return 英语笔记集合
     */
    public List<EnglishNote> selectNoteListByUserId(Long userId);

    /**
     * 新增英语笔记
     * 
     * @param note 英语笔记
     * @return 结果
     */
    public int insertNote(EnglishNote note);

    /**
     * 修改英语笔记
     * 
     * @param note 英语笔记
     * @return 结果
     */
    public int updateNote(EnglishNote note);

    /**
     * 删除英语笔记
     * 
     * @param id 笔记ID
     * @return 结果
     */
    public int deleteNoteById(Long id);

    /**
     * 批量删除英语笔记
     * 
     * @param ids 需要删除的数据ID集合
     * @return 结果
     */
    public int deleteNoteByIds(Long[] ids);

    /**
     * 向量相似度搜索
     * 
     * @param userId 用户ID
     * @param embedding 查询向量(JSON字符串)
     * @param threshold 相似度阈值
     * @param limit 返回结果数量
     * @return 相似笔记集合
     */
    public List<EnglishNote> searchSimilarNotes(
        @Param("userId") Long userId,
        @Param("embedding") String embedding,
        @Param("threshold") Double threshold,
        @Param("limit") Integer limit
    );

    /**
     * 统计用户笔记数量
     * 
     * @param userId 用户ID
     * @return 笔记数量
     */
    public int countNotesByUserId(Long userId);
}
