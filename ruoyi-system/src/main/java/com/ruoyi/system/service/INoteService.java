package com.ruoyi.system.service;

import com.ruoyi.system.domain.EnglishNote;

import java.util.List;

/**
 * Note Service接口
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface INoteService {
    
    /**
     * 创建笔记（包含向量生成）
     * 
     * @param note 笔记对象
     * @return 创建的笔记
     */
    EnglishNote createNote(EnglishNote note);
    
    /**
     * 更新笔记
     * 
     * @param note 笔记对象
     * @return 更新的笔记
     */
    EnglishNote updateNote(EnglishNote note);
    
    /**
     * 删除笔记
     * 
     * @param id 笔记ID
     * @return 是否成功
     */
    boolean deleteNote(Long id);
    
    /**
     * 根据ID获取笔记
     * 
     * @param id 笔记ID
     * @return 笔记对象
     */
    EnglishNote getNoteById(Long id);
    
    /**
     * 列出用户的所有笔记
     * 
     * @param userId 用户ID
     * @return 笔记列表
     */
    List<EnglishNote> listNotes(Long userId);
    
    /**
     * 批量删除笔记
     * 
     * @param ids 笔记ID数组
     * @return 是否成功
     */
    boolean deleteNotes(Long[] ids);
    
    /**
     * 统计用户笔记数量
     * 
     * @param userId 用户ID
     * @return 笔记数量
     */
    int countNotes(Long userId);

//    public List<EnglishNote> searchNotes(Long userId, String keyword);
}
