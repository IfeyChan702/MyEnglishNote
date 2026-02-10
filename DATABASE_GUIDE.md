# 数据库选择指南 (Database Selection Guide)

## 概述

MyEnglishNote 支持两种数据库用于 RAG (检索增强生成) 功能：
- **MySQL 5.7+ / 8.0+**
- **PostgreSQL 12+ (带 pgvector 扩展)**

本文档将帮助您选择最适合您需求的数据库。

---

## 快速对比

| 特性 | MySQL | PostgreSQL + pgvector |
|------|-------|----------------------|
| **向量存储** | JSON 格式 | 原生 vector 类型 |
| **向量计算** | 应用层 (Java) | 应用层或数据库层 |
| **查询性能** | 良好 | 优秀 |
| **向量索引** | 不支持 | 支持 (IVFFlat, HNSW) |
| **设置难度** | 简单 | 中等 (需安装扩展) |
| **适合场景** | 中小型项目 | 大型项目、高性能需求 |
| **成熟度** | 非常成熟 | 成熟 |

---

## MySQL 方案

### 优势

✅ **简单易用**
- 无需安装额外扩展
- 配置简单，开箱即用
- 广泛的社区支持和文档

✅ **成熟稳定**
- MySQL 是最流行的开源数据库之一
- 大量的生产环境验证
- 丰富的工具生态系统

✅ **足够的性能**
- 对于中小型数据集（< 10万条笔记）性能良好
- 应用层并行计算提供 5-10 倍性能提升

### 劣势

❌ **向量存储效率**
- 使用 JSON 格式存储向量，空间占用较大
- JSON 序列化/反序列化有开销

❌ **无原生向量索引**
- 不支持向量专用索引
- 大数据集查询性能受限

❌ **数据库层计算慢**
- MySQL 存储函数处理大向量效率低（已移除）
- 必须在应用层计算相似度

### 适用场景

- 📝 笔记数量 < 10 万条
- 🚀 快速部署，不想安装额外组件
- 💼 已有 MySQL 基础设施
- 🔧 团队熟悉 MySQL
- 💰 预算有限，使用现有资源

### 性能数据

**场景：1000 个笔记，1536 维向量**

```
查询响应时间：100-200ms
- 生成查询向量：30-50ms
- 数据库查询：10-20ms
- 应用层相似度计算：50-100ms (并行)
- 过滤和排序：10-20ms

内存占用：
- JSON 向量存储：约 12KB/向量
- 1000 个向量：约 12MB
```

---

## PostgreSQL + pgvector 方案

### 优势

✅ **原生向量支持**
- 使用 `vector(1536)` 类型存储向量
- 空间效率高，查询速度快
- 原生向量操作符：`<->`, `<#>`, `<=>`

✅ **高性能向量索引**
- IVFFlat 索引：适合大型数据集
- HNSW 索引：更高的召回率和查询速度
- 显著加速相似度搜索

✅ **灵活的计算选择**
- 可在数据库层计算（性能最优）
- 也可在应用层计算（统一逻辑）

✅ **未来扩展性**
- pgvector 持续优化
- 支持更多向量操作
- 适合大规模部署

### 劣势

❌ **设置复杂**
- 需要安装 pgvector 扩展
- 可能需要编译源代码
- 需要学习 PostgreSQL 特性

❌ **运维成本**
- 如果团队不熟悉 PostgreSQL
- 需要学习新的管理工具
- 备份和迁移策略不同

### 适用场景

- 📊 笔记数量 > 10 万条
- ⚡ 高性能要求（< 50ms 响应时间）
- 🎯 大规模向量检索
- 🔮 未来可能迁移到专用向量数据库
- 💻 团队有 PostgreSQL 经验

### 性能数据

**场景：1000 个笔记，1536 维向量**

```
查询响应时间：50-100ms
- 生成查询向量：30-50ms
- 数据库向量查询（含索引）：20-50ms

查询响应时间（10万笔记）：100-200ms
- 得益于向量索引，性能几乎不受数据量影响

内存占用：
- vector 类型存储：约 6KB/向量
- 1000 个向量：约 6MB
- 空间占用减少 50%
```

---

## 如何选择

### 选择 MySQL 如果：

```
✓ 笔记数量 < 10 万
✓ 响应时间要求 < 500ms
✓ 快速部署优先
✓ 已有 MySQL 基础设施
✓ 团队熟悉 MySQL
```

### 选择 PostgreSQL 如果：

```
✓ 笔记数量 > 10 万
✓ 响应时间要求 < 100ms
✓ 性能是首要考虑
✓ 计划长期发展
✓ 团队有 PostgreSQL 经验或愿意学习
```

---

## 配置切换

### 1. 在 application-rag.yml 中配置数据库类型

```yaml
rag:
  database:
    type: mysql  # 或 postgresql
```

### 2. 配置数据源

**MySQL (application-druid.yml)**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/myenglishnote?useUnicode=true&characterEncoding=utf8
    username: your_username
    password: your_password
```

**PostgreSQL (application-druid.yml)**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myenglishnote
    username: your_username
    password: your_password
```

### 3. 运行初始化脚本

**MySQL**
```bash
mysql -u your_username -p myenglishnote < sql/mysql/rag_init.sql
```

**PostgreSQL**
```bash
# 1. 安装 pgvector
psql -U your_username -d myenglishnote -f sql/postgresql/pgvector_setup.sql

# 2. 初始化表结构
psql -U your_username -d myenglishnote -f sql/postgresql/rag_init.sql
```

---

## 迁移指南

### 从 MySQL 迁移到 PostgreSQL

1. **导出 MySQL 数据**
```bash
mysqldump -u username -p myenglishnote > backup.sql
```

2. **转换 SQL 语法**
- 修改数据类型
- 调整函数名称
- 转换 JSON 向量为 pgvector 格式

3. **导入 PostgreSQL**
```bash
psql -U username -d myenglishnote -f converted.sql
```

4. **构建向量索引**
```sql
CREATE INDEX idx_note_embedding_cosine 
ON english_note 
USING ivfflat (embedding vector_cosine_ops) 
WITH (lists = 100);
```

5. **验证数据**
```sql
SELECT COUNT(*) FROM english_note WHERE embedding IS NOT NULL;
```

### 从 PostgreSQL 迁移到 MySQL

类似流程，但需要将 vector 类型转换为 JSON 格式。

---

## 性能优化建议

### MySQL 优化

```sql
-- 1. 添加索引
CREATE INDEX idx_user_del ON english_note(user_id, del_flag);

-- 2. 定期优化表
OPTIMIZE TABLE english_note;

-- 3. 调整配置
SET GLOBAL innodb_buffer_pool_size = 2G;
```

### PostgreSQL 优化

```sql
-- 1. 调整 work_mem
SET work_mem = '512MB';

-- 2. 运行 VACUUM ANALYZE
VACUUM ANALYZE english_note;

-- 3. 调整 probes 参数
SET ivfflat.probes = 10;

-- 4. 使用 EXPLAIN 分析查询
EXPLAIN ANALYZE 
SELECT * FROM english_note 
ORDER BY embedding <=> '[...]'::vector 
LIMIT 10;
```

---

## 常见问题

### Q: 可以同时支持两种数据库吗？
A: 理论上可以，但需要维护两套配置。建议选择一种数据库使用。

### Q: 切换数据库需要修改代码吗？
A: 不需要！只需修改配置文件即可。应用层代码已经抽象化。

### Q: pgvector 支持哪些 PostgreSQL 版本？
A: PostgreSQL 11+，推荐 12 或更高版本。

### Q: MySQL 8.0 比 5.7 性能更好吗？
A: 是的，MySQL 8.0 的 JSON 处理性能有所提升，但差异不大。

### Q: 可以使用云数据库吗？
A: 可以！
- AWS RDS (MySQL/PostgreSQL)
- Azure Database (MySQL/PostgreSQL)
- Google Cloud SQL (MySQL/PostgreSQL)
- 阿里云 RDS (MySQL/PostgreSQL)

---

## 总结

- **快速开始**：选择 MySQL
- **高性能**：选择 PostgreSQL + pgvector
- **长期发展**：PostgreSQL 是更好的选择
- **性能足够**：MySQL 对于大多数场景已经足够

两种方案都经过优化和测试，根据您的实际需求选择即可！

---

## 相关文档

- [向量优化说明](VECTOR_OPTIMIZATION.md)
- [架构文档](docs/ARCHITECTURE.md)
- [迁移指南](docs/MIGRATION.md)
