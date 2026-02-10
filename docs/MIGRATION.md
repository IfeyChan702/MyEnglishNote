# 向量计算架构迁移指南 (Migration Guide)

## 概述

本文档指导您如何从旧的 MySQL 存储函数架构迁移到新的 Java 应用层向量计算架构。

---

## 变更摘要

### 移除的功能
- ❌ MySQL `cosine_similarity` 存储函数
- ❌ SQL 查询中的相似度计算和过滤

### 新增的功能
- ✅ `VectorUtil` 增强（支持多种算法）
- ✅ `IEmbeddingService` 向量处理服务
- ✅ `RAGServiceImpl` 应用层计算逻辑
- ✅ 性能监控和日志
- ✅ PostgreSQL + pgvector 支持

### 保持不变的功能
- ✅ 数据库表结构（english_note, review_record）
- ✅ API 接口（对外接口保持兼容）
- ✅ 向量维度（1536）
- ✅ JSON 格式存储（MySQL）

---

## 迁移步骤

### 步骤 1: 备份数据库

**MySQL:**
```bash
mysqldump -u username -p myenglishnote > backup_$(date +%Y%m%d).sql
```

**PostgreSQL:**
```bash
pg_dump -U username myenglishnote > backup_$(date +%Y%m%d).sql
```

### 步骤 2: 更新代码

```bash
# 拉取最新代码
git pull origin main

# 或者切换到新分支
git checkout feature/vector-optimization
```

### 步骤 3: 更新配置文件

编辑 `ruoyi-admin/src/main/resources/application-rag.yml`：

```yaml
rag:
  # 新增：数据库类型配置
  database:
    type: mysql  # 或 postgresql
  
  # 新增：相似度算法配置
  similarity:
    algorithm: cosine  # 默认使用余弦相似度
  
  # 新增：缓存配置
  cache:
    enabled: false  # 根据需要启用
    ttl-minutes: 60
    max-size: 10000
  
  # 新增：性能监控配置
  performance:
    monitor-enabled: true
    slow-query-threshold-ms: 1000
  
  # 保持现有配置
  deepseek:
    api-key: ${DEEPSEEK_API_KEY}
    # ...
  
  vector:
    dimension: 1536
    similarity-threshold: 0.7
    max-results: 5
```

### 步骤 4: 更新数据库

#### 选项 A: 继续使用 MySQL

```bash
# 1. 删除旧的存储函数
mysql -u username -p myenglishnote << 'EOF'
DROP FUNCTION IF EXISTS cosine_similarity;
EOF

# 2. 更新表结构（可选，添加索引优化）
mysql -u username -p myenglishnote << 'EOF'
CREATE INDEX IF NOT EXISTS idx_user_del ON english_note(user_id, del_flag);
CREATE INDEX IF NOT EXISTS idx_user_next_review ON review_record(user_id, next_review_date);
EOF
```

**注意：** 不需要运行完整的 `sql/mysql/rag_init.sql`，因为表结构已存在。

#### 选项 B: 迁移到 PostgreSQL

```bash
# 1. 安装 pgvector 扩展
psql -U username -d myenglishnote -f sql/postgresql/pgvector_setup.sql

# 2. 导出 MySQL 数据
mysqldump -u username -p --no-create-info --complete-insert myenglishnote > data_export.sql

# 3. 转换数据格式（需要手动编写脚本或使用工具）
# 主要转换：
#   - JSON 向量 → vector(1536)
#   - DATETIME → TIMESTAMP
#   - AUTO_INCREMENT → SERIAL

# 4. 创建 PostgreSQL 表
psql -U username -d myenglishnote -f sql/postgresql/rag_init.sql

# 5. 导入数据
psql -U username -d myenglishnote -f converted_data.sql

# 6. 构建向量索引
psql -U username -d myenglishnote << 'EOF'
CREATE INDEX idx_note_embedding_cosine 
ON english_note 
USING ivfflat (embedding vector_cosine_ops) 
WITH (lists = 100);
EOF
```

### 步骤 5: 构建项目

```bash
# 清理并构建
mvn clean package -DskipTests

# 或者
./ry.sh package
```

### 步骤 6: 运行测试

```bash
# 运行单元测试
mvn test

# 或者运行特定测试
mvn test -Dtest=VectorUtilTest
mvn test -Dtest=RAGServiceTest
```

### 步骤 7: 启动应用

```bash
# 启动应用
java -jar ruoyi-admin/target/ruoyi-admin.jar

# 或者使用脚本
./ry.sh start
```

### 步骤 8: 验证功能

```bash
# 1. 检查应用启动日志
tail -f logs/sys-info.log

# 2. 测试 RAG 查询
curl -X POST http://localhost:8080/rag/search \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "query": "apple fruit",
    "threshold": 0.7,
    "maxResults": 5
  }'

# 3. 检查性能日志
grep "Search completed" logs/sys-info.log
```

### 步骤 9: 性能对比

**测试查询性能：**

```bash
# 使用 Apache Benchmark 测试
ab -n 100 -c 10 -p query.json -T application/json \
   http://localhost:8080/rag/search

# 或使用 JMeter 进行详细测试
```

**期望性能提升：**
- 响应时间减少 5-10 倍
- 从 800-1200ms 降至 100-200ms

---

## 回滚方案

如果迁移遇到问题，可以回滚到旧版本：

### 步骤 1: 恢复代码

```bash
# 切换回旧分支
git checkout main

# 或者恢复到特定提交
git reset --hard <commit-hash>
```

### 步骤 2: 恢复数据库

```bash
# MySQL
mysql -u username -p myenglishnote < backup_YYYYMMDD.sql

# PostgreSQL
psql -U username -d myenglishnote -f backup_YYYYMMDD.sql
```

### 步骤 3: 重建存储函数

```bash
# 运行旧的 SQL 脚本
mysql -u username -p myenglishnote -f sql/rag_init.sql
```

### 步骤 4: 重启应用

```bash
./ry.sh restart
```

---

## 常见问题

### Q1: 迁移后性能没有提升？

**可能原因：**
- JVM 未充分预热
- 数据库索引未正确创建
- 并行度设置不当

**解决方案：**
```bash
# 1. 检查并行度
System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism")

# 2. 验证索引
SHOW INDEX FROM english_note;

# 3. 运行性能测试多次
```

### Q2: 迁移后内存占用增加？

**原因：** 应用层计算需要将向量加载到内存。

**解决方案：**
```bash
# 调整 JVM 堆大小
java -Xms2G -Xmx4G -jar ruoyi-admin.jar

# 或启用缓存淘汰机制
```

### Q3: PostgreSQL 迁移后 JSON 向量如何转换？

**转换脚本示例 (Python):**

```python
import json
import psycopg2

conn = psycopg2.connect("dbname=myenglishnote user=username")
cur = conn.cursor()

# 读取 MySQL 数据
cur.execute("SELECT id, embedding FROM english_note WHERE embedding IS NOT NULL")

for row in cur.fetchall():
    note_id, embedding_json = row
    embedding_list = json.loads(embedding_json)
    embedding_str = '[' + ','.join(map(str, embedding_list)) + ']'
    
    # 更新 PostgreSQL
    cur.execute(
        "UPDATE english_note SET embedding = %s::vector WHERE id = %s",
        (embedding_str, note_id)
    )

conn.commit()
conn.close()
```

### Q4: 如何验证相似度计算结果一致性？

**测试脚本：**

```java
@Test
public void testConsistency() {
    // 1. 准备测试向量
    List<Double> v1 = Arrays.asList(1.0, 2.0, 3.0);
    List<Double> v2 = Arrays.asList(4.0, 5.0, 6.0);
    
    // 2. 计算相似度
    double similarity = VectorUtil.cosineSimilarity(v1, v2);
    
    // 3. 验证结果（与旧版本对比）
    assertEquals(0.9746, similarity, 0.0001);
}
```

### Q5: 大数据量迁移建议？

**对于 > 10 万笔记：**

```bash
# 1. 分批迁移
SELECT * FROM english_note WHERE id BETWEEN 0 AND 10000;
# ... 逐批处理

# 2. 使用 PostgreSQL + pgvector
# 性能更好，支持向量索引

# 3. 考虑停机维护
# 避免迁移过程中数据不一致
```

---

## 性能基准

### 迁移前 (MySQL 存储函数)

| 笔记数量 | 响应时间 | CPU 使用 |
|---------|---------|---------|
| 100     | 80-120ms | 高 |
| 1,000   | 800-1200ms | 高 |
| 10,000  | 8-12s | 很高 |

### 迁移后 (Java 应用层)

| 笔记数量 | 响应时间 | CPU 使用 |
|---------|---------|---------|
| 100     | 20-30ms | 中 |
| 1,000   | 100-200ms | 中 |
| 10,000  | 800-1200ms | 中高 |

### PostgreSQL + pgvector

| 笔记数量 | 响应时间 | CPU 使用 |
|---------|---------|---------|
| 100     | 10-15ms | 低 |
| 1,000   | 50-100ms | 低 |
| 10,000  | 200-400ms | 中 |
| 100,000 | 1-2s | 中 |

---

## 最佳实践

### 1. 灰度发布

```bash
# 第一天：10% 流量
# 第三天：50% 流量
# 第五天：100% 流量
```

### 2. 监控告警

```yaml
# Prometheus 告警规则
- alert: RAGResponseTimeSlow
  expr: rag_search_duration_ms > 1000
  for: 5m
  labels:
    severity: warning
```

### 3. 回滚演练

定期进行回滚演练，确保可以快速恢复。

### 4. 文档更新

更新相关文档和运维手册。

---

## 迁移检查清单

- [ ] 数据库备份完成
- [ ] 代码更新完成
- [ ] 配置文件更新
- [ ] 数据库结构更新
- [ ] 应用构建成功
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 性能测试完成
- [ ] 日志监控配置
- [ ] 回滚方案准备
- [ ] 团队培训完成
- [ ] 文档更新完成

---

## 获取帮助

如果遇到问题：

1. 查看日志：`logs/sys-info.log`
2. 检查配置：`application-rag.yml`
3. 查阅文档：
   - [数据库选择指南](../DATABASE_GUIDE.md)
   - [向量优化说明](../VECTOR_OPTIMIZATION.md)
   - [架构文档](ARCHITECTURE.md)
4. 提交 Issue：https://github.com/IfeyChan702/MyEnglishNote/issues

---

## 总结

迁移到新架构将显著提升系统性能和代码质量。按照本指南逐步操作，可以安全、平稳地完成迁移。
