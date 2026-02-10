package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.EnglishNote;
import com.ruoyi.system.domain.dto.NoteDTO;
import com.ruoyi.system.domain.dto.RAGResponse;
import com.ruoyi.system.mapper.NoteMapper;
import com.ruoyi.system.service.IDeepseekService;
import com.ruoyi.system.service.IRAGService;
import com.ruoyi.system.util.EmbeddingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * RAG Service实现
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Service
public class RAGServiceImpl implements IRAGService {

    private static final Logger log = LoggerFactory.getLogger(RAGServiceImpl.class);
    
    @Autowired
    private IDeepseekService deepseekService;
    
    @Autowired
    private NoteMapper noteMapper;
    
    @Value("${rag.vector.similarity-threshold:0.7}")
    private Double defaultThreshold;
    
    @Value("${rag.vector.max-results:5}")
    private Integer defaultMaxResults;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 向量检索相关笔记
     * 
     * @param userId 用户ID
     * @param query 查询文本
     * @param threshold 相似度阈值
     * @param maxResults 最大返回结果数
     * @return 相关笔记列表
     */
    @Override
    public List<EnglishNote> searchNotes(Long userId, String query, Double threshold, Integer maxResults) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        // 使用默认值
        if (threshold == null) {
            threshold = defaultThreshold;
        }
        if (maxResults == null) {
            maxResults = defaultMaxResults;
        }
        
        log.debug("Searching notes for user {} with query: {}", userId, query);
        
        try {
            // 1. 生成查询向量
            List<Double> queryEmbedding = deepseekService.embedding(query);
            String queryEmbeddingJson = EmbeddingUtil.embeddingToJson(queryEmbedding);
            
            // 2. 使用向量检索相关笔记
            List<EnglishNote> notes = noteMapper.searchSimilarNotes(
                    userId, 
                    queryEmbeddingJson, 
                    threshold, 
                    maxResults
            );
            
            log.debug("Found {} similar notes", notes.size());
            
            return notes;
        } catch (Exception e) {
            log.error("Failed to search notes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search notes: " + e.getMessage(), e);
        }
    }
    
    /**
     * 使用检索结果生成回答
     * 
     * @param userId 用户ID
     * @param question 用户问题
     * @param threshold 相似度阈值
     * @param maxResults 最大返回结果数
     * @param includeContext 是否包含上下文
     * @return RAG响应
     */
    @Override
    public RAGResponse generateAnswer(Long userId, String question, Double threshold, Integer maxResults, Boolean includeContext) {
        long startTime = System.currentTimeMillis();
        RAGResponse response = new RAGResponse();
        
        try {
            // 1. 检索相关笔记
            List<EnglishNote> relatedNotes = searchNotes(userId, question, threshold, maxResults);
            
            // 2. 转换为DTO
            List<NoteDTO> noteDTOs = convertToNoteDTOs(relatedNotes);
            response.setRelatedNotes(noteDTOs);
            response.setNoteCount(noteDTOs.size());
            
            // 3. 生成回答
            String answer;
            if (includeContext != null && includeContext && !relatedNotes.isEmpty()) {
                // 构建上下文
                StringBuilder context = new StringBuilder();
                for (int i = 0; i < relatedNotes.size(); i++) {
                    EnglishNote note = relatedNotes.get(i);
                    context.append(String.format("[笔记%d] %s\n", i + 1, note.getContent()));
                }
                
                // 带上下文生成回答
                answer = deepseekService.chatWithContext(context.toString(), question);
            } else {
                // 不带上下文，直接生成回答
                answer = deepseekService.chatWithContext(null, question);
            }
            
            response.setAnswer(answer);
            response.setSuccess(true);
            
        } catch (Exception e) {
            log.error("Failed to generate answer: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
            response.setRelatedNotes(new ArrayList<>());
            response.setNoteCount(0);
        }
        
        long endTime = System.currentTimeMillis();
        response.setProcessingTime(endTime - startTime);
        
        return response;
    }
    
    /**
     * 将EnglishNote列表转换为NoteDTO列表
     * 
     * @param notes 笔记列表
     * @return DTO列表
     */
    private List<NoteDTO> convertToNoteDTOs(List<EnglishNote> notes) {
        List<NoteDTO> dtos = new ArrayList<>();
        
        for (EnglishNote note : notes) {
            NoteDTO dto = new NoteDTO();
            dto.setId(note.getId());
            dto.setContent(note.getContent());
            dto.setTags(note.getTags());
            dto.setSimilarityScore(note.getSimilarityScore());
            
            if (note.getCreateTime() != null) {
                dto.setCreateTime(dateFormat.format(note.getCreateTime()));
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }
}
