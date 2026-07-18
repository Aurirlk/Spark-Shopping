-- 大数据课程要求：生成1000+条电商订单模拟数据
-- 执行方式：mysql -u root -p < SQL/init_order_data.sql

USE shopping;

-- =====================================================
-- 1. 创建新表（如果不存在）
-- =====================================================

-- 订单明细表（与order_item类似，但用于大数据分析）
CREATE TABLE IF NOT EXISTS `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) DEFAULT '' COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '小计金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 用户行为日志表
CREATE TABLE IF NOT EXISTS `user_behavior_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `product_id` bigint DEFAULT NULL COMMENT '商品ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `behavior_type` tinyint NOT NULL COMMENT '行为类型：1-浏览 2-搜索 3-收藏 4-加购 5-下单',
  `device_type` varchar(20) DEFAULT 'pc' COMMENT '设备类型：pc/mobile/miniapp',
  `ip_address` varchar(50) DEFAULT '' COMMENT 'IP地址',
  `session_id` varchar(100) DEFAULT '' COMMENT '会话ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_product_time` (`product_id`, `create_time`),
  KEY `idx_behavior_type` (`behavior_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为日志表';

-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` text COMMENT '评价内容',
  `is_anonymous` tinyint DEFAULT 0 COMMENT '是否匿名：0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- =====================================================
-- 2. 生成更多用户数据（100个用户）
-- =====================================================

-- 使用存储过程生成用户数据
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS generate_users(IN num_users INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE username_val VARCHAR(50);
    DECLARE phone_val VARCHAR(20);
    
    WHILE i <= num_users DO
        SET username_val = CONCAT('user_', LPAD(i, 4, '0'));
        SET phone_val = CONCAT('138', LPAD(FLOOR(RAND() * 100000000), 8, '0'));
        
        INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `phone`, `gender`, `status`)
        VALUES (
            username_val,
            '$2a$10$lS4nrUR11lmWCmT8Mk62Deywkg4WfcrTzTJJk2.ca8sm1BqUugZAS', -- 密码：123123
            CONCAT('用户', i),
            phone_val,
            FLOOR(RAND() * 3), -- 随机性别
            1  -- 正常状态
        );
        
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 执行用户生成
CALL generate_users(100);

-- =====================================================
-- 3. 生成更多商品数据（50个商品）
-- =====================================================

-- 使用存储过程生成商品数据
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS generate_products(IN num_products INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE category_id_val BIGINT;
    DECLARE price_val DECIMAL(10,2);
    DECLARE name_val VARCHAR(200);
    
    WHILE i <= num_products DO
        -- 随机选择分类（6-11，对应现有二级分类）
        SET category_id_val = FLOOR(6 + RAND() * 6);
        
        -- 随机价格（100-15000）
        SET price_val = ROUND(100 + RAND() * 14900, 2);
        
        -- 生成商品名称
        SET name_val = CONCAT(
            ELT(FLOOR(1 + RAND() * 10), 'iPhone', '华为', '小米', '三星', '联想', '戴尔', '海尔', '美的', '格力', '耐克'),
            ' ',
            ELT(FLOOR(1 + RAND() * 5), 'Pro', 'Max', 'Ultra', 'Plus', '标准版'),
            ' ',
            ELT(FLOOR(1 + RAND() * 4), '2024款', '旗舰版', '经典版', '新品')
        );
        
        INSERT IGNORE INTO `product` (`name`, `category_id`, `price`, `original_price`, `stock`, `sales`, `main_image`, `description`)
        VALUES (
            name_val,
            category_id_val,
            price_val,
            ROUND(price_val * (1 + RAND() * 0.3), 2), -- 原价比现价高0-30%
            FLOOR(50 + RAND() * 500), -- 库存50-550
            FLOOR(10 + RAND() * 200), -- 销量10-210
            '/images/products/default.png',
            CONCAT('这是', name_val, '的详细描述')
        );
        
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 执行商品生成
CALL generate_products(50);

-- =====================================================
-- 4. 生成订单数据（2000个订单）
-- =====================================================

-- 使用存储过程生成订单数据
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS generate_orders(IN num_orders INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_id_val BIGINT;
    DECLARE order_no_val VARCHAR(50);
    DECLARE total_amount_val DECIMAL(10,2);
    DECLARE status_val TINYINT;
    DECLARE receiver_name_val VARCHAR(50);
    DECLARE receiver_phone_val VARCHAR(20);
    DECLARE receiver_address_val VARCHAR(500);
    DECLARE pay_time_val DATETIME;
    DECLARE create_time_val DATETIME;
    
    -- 获取用户ID范围
    DECLARE min_user_id BIGINT;
    DECLARE max_user_id BIGINT;
    DECLARE user_count INT;
    
    SELECT MIN(id), MAX(id), COUNT(*) INTO min_user_id, max_user_id, user_count FROM `user`;
    
    WHILE i <= num_orders DO
        -- 随机选择用户
        SET user_id_val = min_user_id + FLOOR(RAND() * (max_user_id - min_user_id + 1));
        
        -- 生成订单号
        SET order_no_val = CONCAT(
            'ORD',
            DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'),
            LPAD(i, 6, '0'),
            LPAD(FLOOR(RAND() * 10000), 4, '0')
        );
        
        -- 随机订单金额（100-20000）
        SET total_amount_val = ROUND(100 + RAND() * 19900, 2);
        
        -- 随机订单状态（0-4）
        SET status_val = FLOOR(RAND() * 5);
        
        -- 收货人信息
        SET receiver_name_val = ELT(FLOOR(1 + RAND() * 5), '张三', '李四', '王五', '赵六', '钱七');
        SET receiver_phone_val = CONCAT('138', LPAD(FLOOR(RAND() * 100000000), 8, '0'));
        SET receiver_address_val = CONCAT(
            ELT(FLOOR(1 + RAND() * 5), '北京市', '上海市', '广州市', '深圳市', '杭州市'),
            ELT(FLOOR(1 + RAND() * 5), '朝阳区', '浦东新区', '天河区', '南山区', '西湖区'),
            ELT(FLOOR(1 + RAND() * 5), '建国门外大街', '陆家嘴', '珠江新城', '科技园', '文三路'),
            FLOOR(1 + RAND() * 200), '号'
        );
        
        -- 时间（最近一年内）
        SET create_time_val = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY);
        SET create_time_val = DATE_ADD(create_time_val, INTERVAL FLOOR(RAND() * 24) HOUR);
        SET create_time_val = DATE_ADD(create_time_val, INTERVAL FLOOR(RAND() * 60) MINUTE);
        
        -- 支付时间（如果已支付）
        IF status_val >= 1 THEN
            SET pay_time_val = DATE_ADD(create_time_val, INTERVAL FLOOR(RAND() * 60) MINUTE);
        ELSE
            SET pay_time_val = NULL;
        END IF;
        
        INSERT INTO `order` (`order_no`, `user_id`, `total_amount`, `pay_amount`, `freight_amount`, `pay_type`, `status`, 
                            `receiver_name`, `receiver_phone`, `receiver_address`, `pay_time`, `create_time`)
        VALUES (
            order_no_val,
            user_id_val,
            total_amount_val,
            total_amount_val, -- 实付金额等于总金额
            IF(RAND() > 0.7, ROUND(RAND() * 20, 2), 0), -- 30%概率有运费
            IF(RAND() > 0.3, 1, 2), -- 70%在线支付，30%货到付款
            status_val,
            receiver_name_val,
            receiver_phone_val,
            receiver_address_val,
            pay_time_val,
            create_time_val
        );
        
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 执行订单生成
CALL generate_orders(2000);

-- =====================================================
-- 5. 生成订单明细数据（每个订单1-3个商品，约4000-6000条）
-- =====================================================

-- 使用存储过程生成订单明细
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS generate_order_details()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE order_id_val BIGINT;
    DECLARE product_id_val BIGINT;
    DECLARE product_name_val VARCHAR(200);
    DECLARE product_image_val VARCHAR(500);
    DECLARE price_val DECIMAL(10,2);
    DECLARE quantity_val INT;
    DECLARE num_items INT;
    DECLARE j INT;
    DECLARE min_product_id BIGINT;
    DECLARE max_product_id BIGINT;
    DECLARE order_cursor CURSOR FOR SELECT id FROM `order`;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    SELECT MIN(id), MAX(id) INTO min_product_id, max_product_id FROM `product`;
    
    OPEN order_cursor;
    
    order_loop: LOOP
        FETCH order_cursor INTO order_id_val;
        IF done THEN
            LEAVE order_loop;
        END IF;
        
        -- 每个订单1-3个商品
        SET num_items = FLOOR(1 + RAND() * 3);
        SET j = 1;
        
        WHILE j <= num_items DO
            -- 随机选择商品
            SET product_id_val = min_product_id + FLOOR(RAND() * (max_product_id - min_product_id + 1));
            
            -- 获取商品信息
            SELECT name, main_image, price INTO product_name_val, product_image_val, price_val
            FROM `product` WHERE id = product_id_val;
            
            -- 随机数量1-5
            SET quantity_val = FLOOR(1 + RAND() * 5);
            
            INSERT INTO `order_detail` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `total_amount`)
            VALUES (
                order_id_val,
                product_id_val,
                IFNULL(product_name_val, '未知商品'),
                IFNULL(product_image_val, '/images/products/default.png'),
                price_val,
                quantity_val,
                ROUND(price_val * quantity_val, 2)
            );
            
            SET j = j + 1;
        END WHILE;
    END LOOP;
    
    CLOSE order_cursor;
END //

DELIMITER ;

-- 执行订单明细生成
CALL generate_order_details();

-- =====================================================
-- 6. 生成用户行为日志数据（5000条）
-- =====================================================

-- 使用存储过程生成用户行为日志
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS generate_user_behavior_logs(IN num_logs INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_id_val BIGINT;
    DECLARE product_id_val BIGINT;
    DECLARE category_id_val BIGINT;
    DECLARE behavior_type_val TINYINT;
    DECLARE device_type_val VARCHAR(20);
    DECLARE ip_address_val VARCHAR(50);
    DECLARE session_id_val VARCHAR(100);
    DECLARE create_time_val DATETIME;
    
    -- 获取用户和商品ID范围
    DECLARE min_user_id BIGINT;
    DECLARE max_user_id BIGINT;
    DECLARE min_product_id BIGINT;
    DECLARE max_product_id BIGINT;
    
    SELECT MIN(id), MAX(id) INTO min_user_id, max_user_id FROM `user`;
    SELECT MIN(id), MAX(id) INTO min_product_id, max_product_id FROM `product`;
    
    WHILE i <= num_logs DO
        -- 随机用户（允许为空，表示未登录用户）
        IF RAND() > 0.2 THEN
            SET user_id_val = min_user_id + FLOOR(RAND() * (max_user_id - min_user_id + 1));
        ELSE
            SET user_id_val = NULL;
        END IF;
        
        -- 随机商品
        SET product_id_val = min_product_id + FLOOR(RAND() * (max_product_id - min_product_id + 1));
        
        -- 获取商品分类
        SELECT category_id INTO category_id_val FROM `product` WHERE id = product_id_val;
        
        -- 随机行为类型（1-5，浏览最多，下单最少）
        SET behavior_type_val = CASE 
            WHEN RAND() < 0.4 THEN 1  -- 浏览 40%
            WHEN RAND() < 0.6 THEN 2  -- 搜索 20%
            WHEN RAND() < 0.75 THEN 3 -- 收藏 15%
            WHEN RAND() < 0.9 THEN 4  -- 加购 15%
            ELSE 5                     -- 下单 10%
        END;
        
        -- 设备类型
        SET device_type_val = ELT(FLOOR(1 + RAND() * 3), 'pc', 'mobile', 'miniapp');
        
        -- IP地址
        SET ip_address_val = CONCAT(
            FLOOR(1 + RAND() * 255), '.',
            FLOOR(RAND() * 256), '.',
            FLOOR(RAND() * 256), '.',
            FLOOR(RAND() * 256)
        );
        
        -- 会话ID
        SET session_id_val = CONCAT('sess_', REPLACE(UUID(), '-', ''), '_', i);
        
        -- 时间（最近90天内）
        SET create_time_val = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY);
        SET create_time_val = DATE_ADD(create_time_val, INTERVAL FLOOR(RAND() * 24) HOUR);
        SET create_time_val = DATE_ADD(create_time_val, INTERVAL FLOOR(RAND() * 60) MINUTE);
        
        INSERT INTO `user_behavior_log` (`user_id`, `product_id`, `category_id`, `behavior_type`, 
                                        `device_type`, `ip_address`, `session_id`, `create_time`)
        VALUES (
            user_id_val,
            product_id_val,
            category_id_val,
            behavior_type_val,
            device_type_val,
            ip_address_val,
            session_id_val,
            create_time_val
        );
        
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 执行用户行为日志生成
CALL generate_user_behavior_logs(5000);

-- =====================================================
-- 7. 生成商品评价数据（1500条）
-- =====================================================

-- 使用存储过程生成商品评价
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS generate_product_reviews(IN num_reviews INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE product_id_val BIGINT;
    DECLARE user_id_val BIGINT;
    DECLARE order_id_val BIGINT;
    DECLARE rating_val TINYINT;
    DECLARE content_val TEXT;
    DECLARE is_anonymous_val TINYINT;
    DECLARE create_time_val DATETIME;
    DECLARE min_product_id BIGINT;
    DECLARE max_product_id BIGINT;
    DECLARE min_user_id BIGINT;
    DECLARE max_user_id BIGINT;
    DECLARE min_order_id BIGINT;
    DECLARE max_order_id BIGINT;
    
    SELECT MIN(id), MAX(id) INTO min_product_id, max_product_id FROM `product`;
    SELECT MIN(id), MAX(id) INTO min_user_id, max_user_id FROM `user`;
    SELECT MIN(id), MAX(id) INTO min_order_id, max_order_id FROM `order`;
    
    WHILE i <= num_reviews DO
        -- 随机商品
        SET product_id_val = min_product_id + FLOOR(RAND() * (max_product_id - min_product_id + 1));
        
        -- 随机用户
        SET user_id_val = min_user_id + FLOOR(RAND() * (max_user_id - min_user_id + 1));
        
        -- 随机关联订单（可能为空）
        IF RAND() > 0.3 THEN
            SET order_id_val = min_order_id + FLOOR(RAND() * (max_order_id - min_order_id + 1));
        ELSE
            SET order_id_val = NULL;
        END IF;
        
        -- 随机评分（1-5，偏向高分）
        SET rating_val = CASE 
            WHEN RAND() < 0.1 THEN 1
            WHEN RAND() < 0.2 THEN 2
            WHEN RAND() < 0.4 THEN 3
            WHEN RAND() < 0.7 THEN 4
            ELSE 5
        END;
        
        -- 评价内容
        SET content_val = ELT(FLOOR(1 + RAND() * 15), 
            '商品质量很好，物流也快',
            '性价比很高，推荐购买',
            '和描述的一样，满意',
            '包装精美，做工细致',
            '价格实惠，物超所值',
            '客服态度好，解答耐心',
            '发货速度快，第二天就到了',
            '商品有瑕疵，但不影响使用',
            '一般般，没有想象中好',
            '非常满意，会回购',
            '质量不错，值得推荐',
            '外观漂亮，功能齐全',
            '使用方便，操作简单',
            '品牌正品，质量有保证',
            '价格偏高，但质量好'
        );
        
        -- 是否匿名
        SET is_anonymous_val = IF(RAND() > 0.7, 1, 0);
        
        -- 时间（最近180天内）
        SET create_time_val = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 180) DAY);
        SET create_time_val = DATE_ADD(create_time_val, INTERVAL FLOOR(RAND() * 24) HOUR);
        SET create_time_val = DATE_ADD(create_time_val, INTERVAL FLOOR(RAND() * 60) MINUTE);
        
        INSERT INTO `product_review` (`product_id`, `user_id`, `order_id`, `rating`, `content`, `is_anonymous`, `create_time`)
        VALUES (
            product_id_val,
            user_id_val,
            order_id_val,
            rating_val,
            content_val,
            is_anonymous_val,
            create_time_val
        );
        
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 执行商品评价生成
CALL generate_product_reviews(1500);

-- =====================================================
-- 8. 验证数据量
-- =====================================================

SELECT '=== 数据生成完成 ===' AS info;
SELECT '用户表' AS table_name, COUNT(*) AS count FROM `user`
UNION ALL
SELECT '商品表', COUNT(*) FROM `product`
UNION ALL
SELECT '订单表', COUNT(*) FROM `order`
UNION ALL
SELECT '订单明细表', COUNT(*) FROM `order_detail`
UNION ALL
SELECT '用户行为日志表', COUNT(*) FROM `user_behavior_log`
UNION ALL
SELECT '商品评价表', COUNT(*) FROM `product_review`;

-- 检查数据量是否达标
SELECT 
    CASE 
        WHEN (SELECT COUNT(*) FROM `order_detail`) >= 4000 THEN '✅ 订单明细数据量达标'
        ELSE '❌ 订单明细数据量不足'
    END AS order_detail_check,
    CASE 
        WHEN (SELECT COUNT(*) FROM `user_behavior_log`) >= 5000 THEN '✅ 用户行为日志数据量达标'
        ELSE '❌ 用户行为日志数据量不足'
    END AS behavior_log_check,
    CASE 
        WHEN (SELECT COUNT(*) FROM `product_review`) >= 1500 THEN '✅ 商品评价数据量达标'
        ELSE '❌ 商品评价数据量不足'
    END AS review_check;

-- 清理存储过程
DROP PROCEDURE IF EXISTS generate_users;
DROP PROCEDURE IF EXISTS generate_products;
DROP PROCEDURE IF EXISTS generate_orders;
DROP PROCEDURE IF EXISTS generate_order_details;
DROP PROCEDURE IF EXISTS generate_user_behavior_logs;
DROP PROCEDURE IF EXISTS generate_product_reviews;