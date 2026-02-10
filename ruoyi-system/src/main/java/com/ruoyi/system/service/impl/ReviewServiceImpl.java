package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ReviewRecord;
import com.ruoyi.system.mapper.NoteMapper;
import com.ruoyi.system.mapper.ReviewMapper;
import com.ruoyi.system.service.IReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Review Service实现
 * 基于SuperMemo SM-2算法的SRS实现
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
@Service
public class ReviewServiceImpl implements IReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private NoteMapper noteMapper;
    
    @Value("${rag.srs.initial-interval:1}")
    private Integer initialInterval;
    
    @Value("${rag.srs.min-easiness:1.3}")
    private Double minEasiness;
    
    @Value("${rag.srs.max-easiness:2.5}")
    private Double maxEasiness;
    
    @Value("${rag.srs.default-easiness:2.5}")
    private Double defaultEasiness;
    
    /**
     * 获取下一个待复习项目
     * 
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 待复习记录列表
     */
    @Override
    public List<ReviewRecord> getNextReviewItems(Long userId, Integer limit) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        Date currentDate = new Date();
        return reviewMapper.selectDueReviews(userId, currentDate, limit);
    }
    
    /**
     * 记录复习结果
     * 
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @param quality 复习质量(0-5分)
     * @return 更新后的复习记录
     */
    @Override
    @Transactional
    public ReviewRecord recordReview(Long noteId, Long userId, Integer quality) {
        if (noteId == null || userId == null) {
            throw new IllegalArgumentException("Note ID and User ID cannot be null");
        }
        
        if (quality == null || quality < 0 || quality > 5) {
            throw new IllegalArgumentException("Quality must be between 0 and 5");
        }
        
        // 获取最新的复习记录
        ReviewRecord latestRecord = reviewMapper.selectLatestRecordByNoteId(noteId);
        
        ReviewRecord newRecord = new ReviewRecord();
        newRecord.setNoteId(noteId);
        newRecord.setUserId(userId);
        newRecord.setQuality(quality);
        newRecord.setReviewedAt(new Date());
        
        if (latestRecord == null) {
            // 首次复习
            newRecord.setRepetitions(1);
            newRecord.setEasinessFactor(BigDecimal.valueOf(defaultEasiness));
            newRecord.setIntervalDays(initialInterval);
        } else {
            // 更新复习数据
            updateReviewStats(newRecord, latestRecord, quality);
        }
        
        // 计算下次复习时间
        Date nextReviewDate = calculateNextReviewDate(newRecord.getIntervalDays());
        newRecord.setNextReviewDate(nextReviewDate);
        
        // 保存新记录
        reviewMapper.insertReviewRecord(newRecord);
        
        log.info("Recorded review for note {} with quality {}, next review in {} days", 
                noteId, quality, newRecord.getIntervalDays());
        
        return newRecord;
    }
    
    /**
     * 获取复习统计
     * 
     * @param userId 用户ID
     * @return 复习统计信息
     */
    @Override
    public Map<String, Object> getReviewStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // 待复习数量
        int dueCount = reviewMapper.countDueReviews(userId, new Date());
        stats.put("dueCount", dueCount);
        
        // 总笔记数
        int totalNotes = noteMapper.countNotesByUserId(userId);
        stats.put("totalNotes", totalNotes);
        
        // 今日复习记录
        ReviewRecord record = new ReviewRecord();
        record.setUserId(userId);
        List<ReviewRecord> todayReviews = reviewMapper.selectReviewRecordList(record);
        
        // 过滤今日的记录
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date todayStart = cal.getTime();
        
        long todayCount = todayReviews.stream()
                .filter(r -> r.getReviewedAt() != null && r.getReviewedAt().after(todayStart))
                .count();
        stats.put("todayReviewCount", todayCount);
        
        // 总复习次数
        stats.put("totalReviewCount", todayReviews.size());
        
        return stats;
    }
    
    /**
     * 初始化笔记的复习记录
     * 
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return 初始化的复习记录
     */
    @Override
    @Transactional
    public ReviewRecord initializeReview(Long noteId, Long userId) {
        if (noteId == null || userId == null) {
            throw new IllegalArgumentException("Note ID and User ID cannot be null");
        }
        
        // 检查是否已有复习记录
        ReviewRecord existing = reviewMapper.selectLatestRecordByNoteId(noteId);
        if (existing != null) {
            return existing;
        }
        
        // 创建初始复习记录
        ReviewRecord record = new ReviewRecord();
        record.setNoteId(noteId);
        record.setUserId(userId);
        record.setQuality(0);
        record.setRepetitions(0);
        record.setEasinessFactor(BigDecimal.valueOf(defaultEasiness));
        record.setIntervalDays(initialInterval);
        record.setReviewedAt(new Date());
        record.setNextReviewDate(calculateNextReviewDate(initialInterval));
        
        reviewMapper.insertReviewRecord(record);
        
        log.info("Initialized review record for note {}", noteId);
        
        return record;
    }
    
    /**
     * 更新复习统计数据（基于SM-2算法）
     * 
     * @param newRecord 新记录
     * @param latestRecord 最新记录
     * @param quality 复习质量
     */
    private void updateReviewStats(ReviewRecord newRecord, ReviewRecord latestRecord, Integer quality) {
        int repetitions = latestRecord.getRepetitions() != null ? latestRecord.getRepetitions() : 0;
        double easiness = latestRecord.getEasinessFactor() != null ? 
                latestRecord.getEasinessFactor().doubleValue() : defaultEasiness;
        int interval = latestRecord.getIntervalDays() != null ? latestRecord.getIntervalDays() : initialInterval;
        
        // SM-2算法：更新难度系数
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        double newEasiness = easiness + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        
        // 限制难度系数范围
        if (newEasiness < minEasiness) {
            newEasiness = minEasiness;
        } else if (newEasiness > maxEasiness) {
            newEasiness = maxEasiness;
        }
        
        // 更新复习次数和间隔
        if (quality >= 3) {
            // 复习成功
            repetitions++;
            
            if (repetitions == 1) {
                interval = 1;
            } else if (repetitions == 2) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * newEasiness);
            }
        } else {
            // 复习失败，重置
            repetitions = 0;
            interval = initialInterval;
        }
        
        newRecord.setRepetitions(repetitions);
        newRecord.setEasinessFactor(BigDecimal.valueOf(newEasiness));
        newRecord.setIntervalDays(interval);
    }
    
    /**
     * 计算下次复习日期
     * 
     * @param intervalDays 间隔天数
     * @return 下次复习日期
     */
    private Date calculateNextReviewDate(Integer intervalDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, intervalDays);
        return cal.getTime();
    }
}
