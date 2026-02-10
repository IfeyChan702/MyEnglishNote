-- ========================================
-- MyEnglishNote RAG Pipeline Database Schema
-- 优化版（不包含 MySQL 函数）
-- ========================================

-- 1. 英语笔记表
CREATE TABLE IF NOT EXISTS english_note (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '笔记ID',
                                            user_id BIGINT NOT NULL COMMENT '用户ID',
                                            content TEXT NOT NULL COMMENT '笔记内容',
                                            embedding JSON DEFAULT NULL COMMENT '向量嵌入(JSON格式存储)',
                                            embedding_model VARCHAR(100) DEFAULT 'deepseek-embedding' COMMENT '嵌入模型名称',
    tags VARCHAR(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 1删除)',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_del_flag (del_flag)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='英语学习笔记表';

-- 2. 复习记录表 (支持SRS算法)
CREATE TABLE IF NOT EXISTS review_record (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                             note_id BIGINT NOT NULL COMMENT '笔记ID',
                                             user_id BIGINT NOT NULL COMMENT '用户ID',
                                             quality INT NOT NULL COMMENT '复习质量(0-5分)',
                                             easiness_factor DECIMAL(5,2) DEFAULT 2.50 COMMENT '难度系数',
    interval_days INT DEFAULT 1 COMMENT '复习间隔(天)',
    repetitions INT DEFAULT 0 COMMENT '复习次数',
    next_review_date DATETIME NOT NULL COMMENT '下次复习时间',
    reviewed_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '复习时间',
    INDEX idx_note_id (note_id),
    INDEX idx_user_id (user_id),
    INDEX idx_next_review (next_review_date),
    FOREIGN KEY (note_id) REFERENCES english_note(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复习记录表';

-- 3. 向量检索记录表 (可选，用于分析和优化)
CREATE TABLE IF NOT EXISTS embedding_record (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                                query_text TEXT NOT NULL COMMENT '查询文本',
                                                query_embedding JSON NOT NULL COMMENT '查询向量',
                                                results JSON DEFAULT NULL COMMENT '检索结果(JSON格式)',
                                                result_count INT DEFAULT 0 COMMENT '结果数量',
                                                similarity_threshold DECIMAL(5,4) DEFAULT 0.7000 COMMENT '相似度阈值',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量检索记录表';

-- ========================================
-- 注意：向量相似度计算已移到 Java 应用层
-- ========================================
-- 不再使用 MySQL 函数进行向量计算
-- 原因：性能更优（Java 层 5-10 倍性能提升）
-- 实现：使用 VectorUtil 工具类在应用层计算
-- ========================================

-- ========================================
-- 常用查询示例
-- ========================================

-- 1. 根据用户ID获取笔记列表
-- SELECT id, content, tags, created_at
-- FROM english_note
-- WHERE user_id = ? AND del_flag = '0'
-- ORDER BY created_at DESC;

-- 2. 向量相似度检索（在 Java 应用层进行）
-- 获取用户的所有笔记（含向量）
-- SELECT id, user_id, content, embedding, created_at
-- FROM english_note
-- WHERE user_id = ? AND del_flag = '0' AND embedding IS NOT NULL
-- ORDER BY created_at DESC;

-- 3. 获取需要复习的笔记
-- SELECT n.id, n.content, r.next_review_date, r.repetitions
-- FROM english_note n
-- JOIN review_record r ON n.id = r.note_id
-- WHERE r.user_id = ? AND r.next_review_date <= NOW()
-- ORDER BY r.next_review_date ASC;