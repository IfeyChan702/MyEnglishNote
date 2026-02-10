-- ========================================
-- MyEnglishNote RAG Pipeline Database Schema (PostgreSQL)
-- 使用 pgvector 扩展提供原生向量支持
-- ========================================
-- 
-- 要求：
-- - PostgreSQL 12+
-- - pgvector 扩展 (请先运行 pgvector_setup.sql)
-- 
-- ========================================

-- 确保 pgvector 扩展已安装
CREATE EXTENSION IF NOT EXISTS vector;

-- 1. 英语笔记表
CREATE TABLE IF NOT EXISTS english_note (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536),  -- 使用 pgvector 的原生向量类型
    embedding_model VARCHAR(100) DEFAULT 'deepseek-embedding',
    tags VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0'
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_note_user_id ON english_note(user_id);
CREATE INDEX IF NOT EXISTS idx_note_user_del ON english_note(user_id, del_flag);
CREATE INDEX IF NOT EXISTS idx_note_created_at ON english_note(created_at);
CREATE INDEX IF NOT EXISTS idx_note_del_flag ON english_note(del_flag);

-- 向量相似度索引 (使用 IVFFlat，适合中大型数据集)
-- lists 参数根据数据规模调整：建议为 rows / 1000 (最少 10, 最多 1000)
CREATE INDEX IF NOT EXISTS idx_note_embedding_cosine 
ON english_note 
USING ivfflat (embedding vector_cosine_ops) 
WITH (lists = 100);

-- 可选：使用 HNSW 索引获得更好的性能 (需要 pgvector 0.5.0+)
-- CREATE INDEX IF NOT EXISTS idx_note_embedding_hnsw 
-- ON english_note 
-- USING hnsw (embedding vector_cosine_ops);

-- 表注释
COMMENT ON TABLE english_note IS '英语学习笔记表';
COMMENT ON COLUMN english_note.id IS '笔记ID';
COMMENT ON COLUMN english_note.user_id IS '用户ID';
COMMENT ON COLUMN english_note.content IS '笔记内容';
COMMENT ON COLUMN english_note.embedding IS '向量嵌入(1536维)';
COMMENT ON COLUMN english_note.embedding_model IS '嵌入模型名称';
COMMENT ON COLUMN english_note.tags IS '标签(逗号分隔)';
COMMENT ON COLUMN english_note.created_at IS '创建时间';
COMMENT ON COLUMN english_note.updated_at IS '更新时间';
COMMENT ON COLUMN english_note.del_flag IS '删除标志(0正常 1删除)';

-- 2. 复习记录表 (支持SRS算法)
CREATE TABLE IF NOT EXISTS review_record (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    quality INT NOT NULL,
    easiness_factor DECIMAL(5,2) DEFAULT 2.50,
    interval_days INT DEFAULT 1,
    repetitions INT DEFAULT 0,
    next_review_date TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES english_note(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_review_note_id ON review_record(note_id);
CREATE INDEX IF NOT EXISTS idx_review_user_id ON review_record(user_id);
CREATE INDEX IF NOT EXISTS idx_review_next_review ON review_record(next_review_date);
CREATE INDEX IF NOT EXISTS idx_review_user_next ON review_record(user_id, next_review_date);

-- 表注释
COMMENT ON TABLE review_record IS '复习记录表';
COMMENT ON COLUMN review_record.id IS '记录ID';
COMMENT ON COLUMN review_record.note_id IS '笔记ID';
COMMENT ON COLUMN review_record.user_id IS '用户ID';
COMMENT ON COLUMN review_record.quality IS '复习质量(0-5分)';
COMMENT ON COLUMN review_record.easiness_factor IS '难度系数';
COMMENT ON COLUMN review_record.interval_days IS '复习间隔(天)';
COMMENT ON COLUMN review_record.repetitions IS '复习次数';
COMMENT ON COLUMN review_record.next_review_date IS '下次复习时间';
COMMENT ON COLUMN review_record.reviewed_at IS '复习时间';

-- 3. 向量检索记录表 (可选，用于分析和优化)
CREATE TABLE IF NOT EXISTS embedding_record (
    id BIGSERIAL PRIMARY KEY,
    query_text TEXT NOT NULL,
    query_embedding vector(1536),
    results JSONB,
    result_count INT DEFAULT 0,
    similarity_threshold DECIMAL(5,4) DEFAULT 0.7000,
    user_id BIGINT,
    processing_time_ms BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_embedding_user_id ON embedding_record(user_id);
CREATE INDEX IF NOT EXISTS idx_embedding_created_at ON embedding_record(created_at);

-- 表注释
COMMENT ON TABLE embedding_record IS '向量检索记录表';
COMMENT ON COLUMN embedding_record.id IS '记录ID';
COMMENT ON COLUMN embedding_record.query_text IS '查询文本';
COMMENT ON COLUMN embedding_record.query_embedding IS '查询向量';
COMMENT ON COLUMN embedding_record.results IS '检索结果(JSONB格式)';
COMMENT ON COLUMN embedding_record.result_count IS '结果数量';
COMMENT ON COLUMN embedding_record.similarity_threshold IS '相似度阈值';
COMMENT ON COLUMN embedding_record.user_id IS '用户ID';
COMMENT ON COLUMN embedding_record.processing_time_ms IS '处理时间(毫秒)';
COMMENT ON COLUMN embedding_record.created_at IS '创建时间';

-- ========================================
-- 自动更新 updated_at 触发器
-- ========================================

-- 创建触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为 english_note 表创建触发器
DROP TRIGGER IF EXISTS update_note_updated_at ON english_note;
CREATE TRIGGER update_note_updated_at
    BEFORE UPDATE ON english_note
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ========================================
-- PostgreSQL 向量查询示例
-- ========================================

-- 1. 余弦相似度搜索（使用 <=> 操作符，距离越小越相似）
-- SELECT 
--     id, 
--     content, 
--     tags,
--     1 - (embedding <=> '[0.1, 0.2, ...]'::vector) AS similarity_score
-- FROM english_note
-- WHERE user_id = 1 
--     AND del_flag = '0'
--     AND embedding IS NOT NULL
-- ORDER BY embedding <=> '[0.1, 0.2, ...]'::vector
-- LIMIT 5;

-- 2. 欧氏距离搜索（使用 <-> 操作符）
-- SELECT 
--     id, 
--     content,
--     embedding <-> '[0.1, 0.2, ...]'::vector AS l2_distance
-- FROM english_note
-- WHERE user_id = 1 AND del_flag = '0'
-- ORDER BY embedding <-> '[0.1, 0.2, ...]'::vector
-- LIMIT 5;

-- 3. 内积搜索（使用 <#> 操作符）
-- SELECT 
--     id, 
--     content,
--     (embedding <#> '[0.1, 0.2, ...]'::vector) * -1 AS inner_product
-- FROM english_note
-- WHERE user_id = 1 AND del_flag = '0'
-- ORDER BY embedding <#> '[0.1, 0.2, ...]'::vector
-- LIMIT 5;

-- 4. 结合相似度阈值过滤
-- SELECT 
--     id, 
--     content,
--     1 - (embedding <=> '[0.1, 0.2, ...]'::vector) AS similarity_score
-- FROM english_note
-- WHERE user_id = 1 
--     AND del_flag = '0'
--     AND embedding IS NOT NULL
--     AND (1 - (embedding <=> '[0.1, 0.2, ...]'::vector)) >= 0.7
-- ORDER BY embedding <=> '[0.1, 0.2, ...]'::vector
-- LIMIT 5;

-- ========================================
-- 性能优化建议
-- ========================================
-- 
-- 1. 定期运行 VACUUM ANALYZE
--    VACUUM ANALYZE english_note;
-- 
-- 2. 监控查询性能
--    EXPLAIN ANALYZE 
--    SELECT * FROM english_note 
--    WHERE user_id = 1 
--    ORDER BY embedding <=> '[...]'::vector 
--    LIMIT 10;
-- 
-- 3. 调整 work_mem 以提高索引构建和查询速度
--    SET work_mem = '512MB';
-- 
-- 4. 对于大型数据集，考虑分区表
--    CREATE TABLE english_note_p1 PARTITION OF english_note
--    FOR VALUES FROM (1) TO (1000000);
-- 
-- 5. 使用 probes 参数调整查询准确性和速度
--    SET ivfflat.probes = 10;  -- 默认值为 1
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
-- 统计查询
-- ========================================

-- 1. 统计用户笔记数量
-- SELECT COUNT(*) FROM english_note 
-- WHERE user_id = 1 AND del_flag = '0';

-- 2. 统计有向量的笔记数量
-- SELECT COUNT(*) FROM english_note 
-- WHERE user_id = 1 AND del_flag = '0' AND embedding IS NOT NULL;

-- 3. 查看平均处理时间
-- SELECT 
--     AVG(processing_time_ms) as avg_time_ms, 
--     MAX(processing_time_ms) as max_time_ms,
--     MIN(processing_time_ms) as min_time_ms
-- FROM embedding_record
-- WHERE created_at >= CURRENT_TIMESTAMP - INTERVAL '1 day';

-- 4. 查看慢查询
-- SELECT query_text, processing_time_ms, result_count, created_at
-- FROM embedding_record
-- WHERE processing_time_ms > 1000
-- ORDER BY processing_time_ms DESC
-- LIMIT 10;

-- ========================================
-- 权限设置 (根据实际情况调整)
-- ========================================

-- GRANT SELECT, INSERT, UPDATE, DELETE ON english_note TO your_app_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON review_record TO your_app_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON embedding_record TO your_app_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO your_app_user;
