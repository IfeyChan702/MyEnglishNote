CREATE TABLE gift_card (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                           sender VARCHAR(100) COMMENT '发件人',
                           subject VARCHAR(200) COMMENT '主题',
                           gift_type VARCHAR(200) COMMENT '类型',
                           dt_str VARCHAR(50) COMMENT '时间',
                           code VARCHAR(100) UNIQUE COMMENT '礼品卡代码',
                           order_number VARCHAR(200) COMMENT '订单号',
                           amount DECIMAL(10,2) COMMENT '金额',
                           extra_number VARCHAR(200) COMMENT '编号',
                           usage_type VARCHAR(200) COMMENT '使用类型',
                           status TINYINT DEFAULT 0 COMMENT '状态(0正常,1禁用)',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='礼品卡表';

ALTER TABLE gift_card ADD UNIQUE (code);
