-- ========================================
-- Story Generation Feature Database Schema
-- ========================================

-- 主角表
CREATE TABLE IF NOT EXISTS `story_character` (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主角ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  name VARCHAR(100) NOT NULL COMMENT '主角名字',
  description VARCHAR(500) COMMENT '主角描述',
  avatar_url VARCHAR(500) COMMENT '主角头像URL',
  story_count INT DEFAULT 0 COMMENT '相关故事数量',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 1删除)',
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at),
  INDEX idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故事主角表';

-- 故事表
CREATE TABLE IF NOT EXISTS story (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '故事ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  character_id BIGINT NOT NULL COMMENT '主角ID',
  title VARCHAR(255) NOT NULL COMMENT '故事标题',
  content LONGTEXT NOT NULL COMMENT '故事内容',
  objects JSON COMMENT '识别出的物品列表',
  image_url VARCHAR(500) COMMENT '原始图片URL',
  embedding JSON COMMENT '故事向量表示(RAG)',
  embedding_model VARCHAR(100) COMMENT '嵌入模型名称',
  is_favorite TINYINT DEFAULT 0 COMMENT '是否收藏',
  view_count INT DEFAULT 0 COMMENT '浏览次数',
  share_count INT DEFAULT 0 COMMENT '分享次数',
  share_token VARCHAR(100) UNIQUE COMMENT '分享token',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 1删除)',
  INDEX idx_user_id (user_id),
  INDEX idx_character_id (character_id),
  INDEX idx_created_at (created_at),
  INDEX idx_is_favorite (is_favorite),
  INDEX idx_share_token (share_token),
  INDEX idx_del_flag (del_flag),
  FOREIGN KEY (character_id) REFERENCES `character`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='英语故事表';

-- 故事收藏表
CREATE TABLE IF NOT EXISTS story_favorite (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  story_id BIGINT NOT NULL COMMENT '故事ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_user_story (user_id, story_id),
  INDEX idx_user_id (user_id),
  INDEX idx_story_id (story_id),
  FOREIGN KEY (story_id) REFERENCES story(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故事收藏表';

-- ========================================
-- 示例数据插入（可选）
-- ========================================

-- 插入默认主角示例
-- INSERT INTO `character` (user_id, name, description) VALUES 
-- (1, 'Tom', 'A curious little boy who loves adventures'),
-- (1, 'Lily', 'A smart girl who enjoys learning new things');

