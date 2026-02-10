package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.EnglishNote;
import com.ruoyi.system.domain.dto.NoteDTO;
import com.ruoyi.system.domain.dto.RAGResponse;
import com.ruoyi.system.mapper.NoteMapper;
import com.ruoyi.system.service.IDeepseekService;
import com.ruoyi.system.service.IEmbeddingService;
import com.ruoyi.system.service.IRAGService;
import com.ruoyi.system.util.EmbeddingUtil;
import com.ruoyi.system.util.VectorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG Service实现
 * 使用应用层向量计算替代数据库存储函数
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
    
    @Autowired
    private IEmbeddingService embeddingService;
    
    @Value("${rag.vector.similarity-threshold:0.7}")
    private Double defaultThreshold;
    
    @Value("${rag.vector.max-results:5}")
    private Integer defaultMaxResults;
    
    @Value("${rag.similarity.algorithm:cosine}")
    private String similarityAlgorithm;
    
    @Value("${rag.performance.monitor-enabled:true}")
    private boolean performanceMonitorEnabled;
    
    @Value("${rag.performance.slow-query-threshold-ms:1000}")
    private long slowQueryThresholdMs;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 向量检索相关笔记
     * 使用应用层并行计算相似度
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
        final double finalThreshold = (threshold == null) ? defaultThreshold : threshold;
        final int finalMaxResults = (maxResults == null) ? defaultMaxResults : maxResults;
        
        long startTime = System.currentTimeMillis();
        
        log.debug("Searching notes for user {} with query: {}", userId, query);
        
        try {
            // 1. 生成查询向量
            long embeddingStartTime = System.currentTimeMillis();
            List<Double> queryEmbedding = deepseekService.embedding(query);
            long embeddingTime = System.currentTimeMillis() - embeddingStartTime;
            
            if (performanceMonitorEnabled) {
                log.info("Generated query embedding in {}ms", embeddingTime);
            }
            
            // 2. 从数据库获取用户所有笔记（带embedding）
            // 注意：对于大量笔记（>10万），考虑实现分页或流式处理
            // 当前实现适用于中小型数据集（<10万笔记）
            long dbStartTime = System.currentTimeMillis();
            List<EnglishNote> allNotes = noteMapper.selectNoteListByUserId(userId);
            long dbTime = System.currentTimeMillis() - dbStartTime;
            
            if (performanceMonitorEnabled) {
                log.info("Retrieved {} notes from database in {}ms", allNotes.size(), dbTime);
            }
            
            // 3. 过滤有embedding的笔记
            List<EnglishNote> notesWithEmbedding = allNotes.stream()
                .filter(note -> note.getEmbedding() != null && !note.getEmbedding().trim().isEmpty())
                .collect(Collectors.toList());
            
            if (notesWithEmbedding.isEmpty()) {
                log.warn("No notes with embeddings found for user {}", userId);
                return new ArrayList<>();
            }
            
            // 4. 在应用层并行计算相似度
            long similarityStartTime = System.currentTimeMillis();
            computeSimilarityScores(queryEmbedding, notesWithEmbedding);
            long similarityTime = System.currentTimeMillis() - similarityStartTime;
            
            if (performanceMonitorEnabled) {
                log.info("Calculated similarity for {} notes in {}ms using {} algorithm", 
                         notesWithEmbedding.size(), similarityTime, similarityAlgorithm);
            }
            
            // 5. 过滤相似度阈值
            List<EnglishNote> filteredNotes = notesWithEmbedding.stream()
                .filter(note -> note.getSimilarityScore() != null && note.getSimilarityScore() >= finalThreshold)
                .collect(Collectors.toList());
            
            // 6. 按相似度降序排序
            filteredNotes.sort(Comparator.comparing(EnglishNote::getSimilarityScore).reversed());
            
            // 7. 限制返回结果数量
            List<EnglishNote> results = filteredNotes.stream()
                .limit(finalMaxResults)
                .collect(Collectors.toList());
            
            long totalTime = System.currentTimeMillis() - startTime;
            
            if (performanceMonitorEnabled) {
                log.info("Search completed: found {} similar notes (threshold: {}) in {}ms [embedding: {}ms, db: {}ms, similarity: {}ms]",
                         results.size(), finalThreshold, totalTime, embeddingTime, dbTime, similarityTime);
                
                if (totalTime > slowQueryThresholdMs) {
                    log.warn("SLOW QUERY: Search took {}ms (threshold: {}ms)", totalTime, slowQueryThresholdMs);
                }
            }
            
            log.debug("Found {} similar notes", results.size());
            
            return results;
            
        } catch (Exception e) {
            log.error("Failed to search notes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search notes: " + e.getMessage(), e);
        }
    }
    
    /**
     * 在应用层计算相似度（并行处理）
     * 
     * @param queryEmbedding 查询向量
     * @param notes 笔记列表
     */
    private void computeSimilarityScores(List<Double> queryEmbedding, List<EnglishNote> notes) {
        if (queryEmbedding == null || notes == null || notes.isEmpty()) {
            return;
        }
        
        // 使用并行流处理
        notes.parallelStream().forEach(note -> {
            try {
                List<Double> noteEmbedding = embeddingService.jsonToEmbedding(note.getEmbedding());
                
                if (noteEmbedding != null && noteEmbedding.size() == queryEmbedding.size()) {
                    double similarity = calculateSimilarity(queryEmbedding, noteEmbedding);
                    note.setSimilarityScore(similarity);
                } else {
                    log.warn("Invalid embedding for note {}: dimension mismatch", note.getId());
                    note.setSimilarityScore(0.0);
                }
            } catch (Exception e) {
                log.warn("Failed to calculate similarity for note {}: {}", note.getId(), e.getMessage());
                note.setSimilarityScore(0.0);
            }
        });
    }
    
    /**
     * 根据配置的算法计算相似度
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度/距离
     */
    private double calculateSimilarity(List<Double> vector1, List<Double> vector2) {
        switch (similarityAlgorithm.toLowerCase()) {
            case "cosine":
                return VectorUtil.cosineSimilarity(vector1, vector2);
            case "euclidean":
                // 欧氏距离需要转换为相似度（距离越小越相似）
                double distance = VectorUtil.euclideanDistance(vector1, vector2);
                return 1.0 / (1.0 + distance);
            case "manhattan":
                // 曼哈顿距离需要转换为相似度
                double manhattanDist = VectorUtil.manhattanDistance(vector1, vector2);
                return 1.0 / (1.0 + manhattanDist);
            default:
                log.warn("Unknown similarity algorithm: {}, using cosine", similarityAlgorithm);
                return VectorUtil.cosineSimilarity(vector1, vector2);
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
