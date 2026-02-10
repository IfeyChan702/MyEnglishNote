package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.EnglishNote;
import com.ruoyi.system.mapper.NoteMapper;
import com.ruoyi.system.mapper.ReviewMapper;
import com.ruoyi.system.service.IDeepseekService;
import com.ruoyi.system.service.INoteService;
import com.ruoyi.system.util.EmbeddingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Note Service实现
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Service
public class NoteServiceImpl implements INoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    
    @Autowired
    private NoteMapper noteMapper;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private IDeepseekService deepseekService;
    
    @Value("${rag.deepseek.embedding-model}")
    private String embeddingModel;
    
    /**
     * 创建笔记（包含向量生成）
     * 
     * @param note 笔记对象
     * @return 创建的笔记
     */
    @Override
    @Transactional
    public EnglishNote createNote(EnglishNote note) {
        if (note == null || note.getContent() == null || note.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Note content cannot be null or empty");
        }
        
        if (note.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            // 生成向量
            log.debug("Generating embedding for note content");
            List<Double> embedding = deepseekService.embedding(note.getContent());
            String embeddingJson = EmbeddingUtil.embeddingToJson(embedding);
            
            note.setEmbedding(embeddingJson);
            note.setEmbeddingModel(embeddingModel);
            note.setDelFlag("0");
            
            // 保存笔记
            int result = noteMapper.insertNote(note);
            if (result > 0) {
                log.info("Successfully created note with ID: {}", note.getId());
                return note;
            } else {
                throw new RuntimeException("Failed to insert note");
            }
        } catch (Exception e) {
            log.error("Failed to create note: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create note: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新笔记
     * 
     * @param note 笔记对象
     * @return 更新的笔记
     */
    @Override
    @Transactional
    public EnglishNote updateNote(EnglishNote note) {
        if (note == null || note.getId() == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }
        
        // 检查笔记是否存在
        EnglishNote existingNote = noteMapper.selectNoteById(note.getId());
        if (existingNote == null) {
            throw new RuntimeException("Note not found with ID: " + note.getId());
        }
        
        try {
            // 如果内容有变化，重新生成向量
            if (note.getContent() != null && !note.getContent().equals(existingNote.getContent())) {
                log.debug("Content changed, regenerating embedding");
                List<Double> embedding = deepseekService.embedding(note.getContent());
                String embeddingJson = EmbeddingUtil.embeddingToJson(embedding);
                note.setEmbedding(embeddingJson);
                note.setEmbeddingModel(embeddingModel);
            }
            
            // 更新笔记
            int result = noteMapper.updateNote(note);
            if (result > 0) {
                log.info("Successfully updated note with ID: {}", note.getId());
                return noteMapper.selectNoteById(note.getId());
            } else {
                throw new RuntimeException("Failed to update note");
            }
        } catch (Exception e) {
            log.error("Failed to update note: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update note: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除笔记
     * 
     * @param id 笔记ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deleteNote(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }
        
        try {
            // 删除相关的复习记录
            reviewMapper.deleteReviewRecordByNoteId(id);
            
            // 软删除笔记
            int result = noteMapper.deleteNoteById(id);
            if (result > 0) {
                log.info("Successfully deleted note with ID: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to delete note: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete note: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据ID获取笔记
     * 
     * @param id 笔记ID
     * @return 笔记对象
     */
    @Override
    public EnglishNote getNoteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }
        
        return noteMapper.selectNoteById(id);
    }
    
    /**
     * 列出用户的所有笔记
     * 
     * @param userId 用户ID
     * @return 笔记列表
     */
    @Override
    public List<EnglishNote> listNotes(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        return noteMapper.selectNoteListByUserId(userId);
    }
    
    /**
     * 批量删除笔记
     * 
     * @param ids 笔记ID数组
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deleteNotes(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new IllegalArgumentException("Note IDs cannot be null or empty");
        }
        
        try {
            // 删除相关的复习记录
            for (Long id : ids) {
                reviewMapper.deleteReviewRecordByNoteId(id);
            }
            
            // 批量软删除笔记
            int result = noteMapper.deleteNoteByIds(ids);
            if (result > 0) {
                log.info("Successfully deleted {} notes", result);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to delete notes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete notes: " + e.getMessage(), e);
        }
    }
    
    /**
     * 统计用户笔记数量
     * 
     * @param userId 用户ID
     * @return 笔记数量
     */
    @Override
    public int countNotes(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        return noteMapper.countNotesByUserId(userId);
    }
}
