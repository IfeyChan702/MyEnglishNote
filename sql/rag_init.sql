-- ========================================
-- MyEnglishNote RAG Pipeline Database Schema
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
-- MySQL 向量相似度计算函数
-- ========================================

-- 余弦相似度计算函数
DELIMITER $$

DROP FUNCTION IF EXISTS cosine_similarity$$

CREATE FUNCTION cosine_similarity(
    vec1 JSON,
    vec2 JSON
) RETURNS DECIMAL(10,6)
DETERMINISTIC
BEGIN
    DECLARE dot_product DECIMAL(20,10) DEFAULT 0;
    DECLARE magnitude1 DECIMAL(20,10) DEFAULT 0;
    DECLARE magnitude2 DECIMAL(20,10) DEFAULT 0;
    DECLARE length INT DEFAULT 0;
    DECLARE i INT DEFAULT 0;
    DECLARE val1 DECIMAL(20,10);
    DECLARE val2 DECIMAL(20,10);
    
    -- 获取向量长度
    SET length = JSON_LENGTH(vec1);
    
    -- 如果长度不匹配或为0，返回0
    IF length != JSON_LENGTH(vec2) OR length = 0 THEN
        RETURN 0;
    END IF;
    
    -- 计算点积和模长
    WHILE i < length DO
        SET val1 = JSON_EXTRACT(vec1, CONCAT('$[', i, ']'));
        SET val2 = JSON_EXTRACT(vec2, CONCAT('$[', i, ']'));
        
        SET dot_product = dot_product + (val1 * val2);
        SET magnitude1 = magnitude1 + (val1 * val1);
        SET magnitude2 = magnitude2 + (val2 * val2);
        
        SET i = i + 1;
    END WHILE;
    
    -- 计算余弦相似度
    IF magnitude1 = 0 OR magnitude2 = 0 THEN
        RETURN 0;
    END IF;
    
    RETURN dot_product / (SQRT(magnitude1) * SQRT(magnitude2));
END$$

DELIMITER ;

-- ========================================
-- 初始化数据 (示例)
-- ========================================

-- 插入示例笔记 (实际使用时需要通过API生成embedding)
-- INSERT INTO english_note (user_id, content, tags) VALUES 
-- (1, 'apple - 苹果, a red fruit', 'fruit,vocabulary'),
-- (1, 'banana - 香蕉, a yellow fruit', 'fruit,vocabulary'),
-- (1, 'computer - 计算机, electronic device for processing data', 'technology,vocabulary');

-- ========================================
-- 常用查询示例
-- ========================================

-- 1. 根据用户ID获取笔记列表
-- SELECT id, content, tags, created_at FROM english_note WHERE user_id = 1 AND del_flag = '0' ORDER BY created_at DESC;

-- 2. 向量相似度检索 (需要先将查询文本转换为向量)
-- SELECT 
--     id, 
--     content, 
--     cosine_similarity(embedding, '[0.1, 0.2, ...]') as similarity
-- FROM english_note 
-- WHERE user_id = 1 AND del_flag = '0' AND embedding IS NOT NULL
-- HAVING similarity > 0.7
-- ORDER BY similarity DESC
-- LIMIT 5;

-- 3. 获取需要复习的笔记
-- SELECT n.id, n.content, r.next_review_date, r.repetitions
-- FROM english_note n
-- JOIN review_record r ON n.id = r.note_id
-- WHERE r.user_id = 1 AND r.next_review_date <= NOW()
-- ORDER BY r.next_review_date ASC;

-- ========================================
-- 权限设置 (根据实际情况调整)
-- ========================================

-- GRANT SELECT, INSERT, UPDATE, DELETE ON english_note TO 'your_app_user'@'localhost';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON review_record TO 'your_app_user'@'localhost';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON embedding_record TO 'your_app_user'@'localhost';
-- GRANT EXECUTE ON FUNCTION cosine_similarity TO 'your_app_user'@'localhost';
