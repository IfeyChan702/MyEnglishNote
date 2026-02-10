-- ========================================
-- MyEnglishNote RAG Pipeline Database Schema (MySQL)
-- 优化版本：移除存储函数，改用应用层计算
-- ========================================

-- 1. 英语笔记表
CREATE TABLE IF NOT EXISTS english_note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '笔记ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '笔记内容',
    embedding JSON DEFAULT NULL COMMENT '向量嵌入(JSON格式存储，1536维)',
    embedding_model VARCHAR(100) DEFAULT 'deepseek-embedding' COMMENT '嵌入模型名称',
    tags VARCHAR(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 1删除)',
    INDEX idx_user_id (user_id),
    INDEX idx_user_del (user_id, del_flag),
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
    INDEX idx_user_next_review (user_id, next_review_date),
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
    processing_time_ms BIGINT DEFAULT 0 COMMENT '处理时间(毫秒)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量检索记录表';

-- ========================================
-- 优化说明
-- ========================================
-- 
-- 1. 移除了 cosine_similarity 存储函数
--    原因：MySQL JSON 处理大向量效率低下，改用 Java 应用层计算性能提升 5-10 倍
-- 
-- 2. 优化了索引
--    - 添加复合索引 idx_user_del 用于加速笔记查询
--    - 添加复合索引 idx_user_next_review 用于加速复习查询
-- 
-- 3. 向量计算现在在应用层进行
--    - 使用并行流处理提高性能
--    - 支持多种相似度算法（余弦、欧氏距离、曼哈顿距离）
--    - 支持缓存机制
-- 
-- 4. 添加了性能监控字段
--    - processing_time_ms: 记录查询处理时间
-- 
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
-- SELECT id, content, tags, created_at 
-- FROM english_note 
-- WHERE user_id = 1 AND del_flag = '0' 
-- ORDER BY created_at DESC;

-- 2. 获取需要复习的笔记
-- SELECT n.id, n.content, r.next_review_date, r.repetitions
-- FROM english_note n
-- JOIN review_record r ON n.id = r.note_id
-- WHERE r.user_id = 1 AND r.next_review_date <= NOW()
-- ORDER BY r.next_review_date ASC;

-- 3. 统计用户笔记数量
-- SELECT COUNT(*) FROM english_note 
-- WHERE user_id = 1 AND del_flag = '0';

-- 4. 查询有向量的笔记数量
-- SELECT COUNT(*) FROM english_note 
-- WHERE user_id = 1 AND del_flag = '0' AND embedding IS NOT NULL;

-- ========================================
-- 性能监控查询
-- ========================================

-- 查看平均处理时间
-- SELECT AVG(processing_time_ms) as avg_time_ms, 
--        MAX(processing_time_ms) as max_time_ms,
--        MIN(processing_time_ms) as min_time_ms
-- FROM embedding_record
-- WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 DAY);

-- 查看慢查询（处理时间超过1秒）
-- SELECT query_text, processing_time_ms, result_count, created_at
-- FROM embedding_record
-- WHERE processing_time_ms > 1000
-- ORDER BY processing_time_ms DESC
-- LIMIT 10;

-- ========================================
-- 权限设置 (根据实际情况调整)
-- ========================================

-- GRANT SELECT, INSERT, UPDATE, DELETE ON english_note TO 'your_app_user'@'localhost';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON review_record TO 'your_app_user'@'localhost';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON embedding_record TO 'your_app_user'@'localhost';
