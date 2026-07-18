-- 购物网站数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `shopping` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE shopping;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT '' COMMENT '昵称',
  `phone` varchar(20) DEFAULT '' COMMENT '手机号',
  `email` varchar(100) DEFAULT '' COMMENT '邮箱',
  `avatar` varchar(500) DEFAULT '' COMMENT '头像URL',
  `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `level` tinyint DEFAULT 1 COMMENT '分类级别：1-一级，2-二级，3-三级',
  `icon` varchar(500) DEFAULT '' COMMENT '分类图标',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `sales` int DEFAULT 0 COMMENT '销量',
  `main_image` varchar(500) DEFAULT '' COMMENT '主图',
  `sub_images` text COMMENT '副图，JSON格式',
  `description` text COMMENT '商品描述',
  `detail` text COMMENT '商品详情，富文本',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `checked` tinyint DEFAULT 1 COMMENT '是否选中：0-未选中，1-选中',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `freight_amount` decimal(10,2) DEFAULT 0.00 COMMENT '运费',
  `pay_type` tinyint DEFAULT 1 COMMENT '支付方式：1-在线支付，2-货到付款',
  `status` tinyint DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `receiver_address` varchar(500) NOT NULL COMMENT '收货地址',
  `note` varchar(500) DEFAULT '' COMMENT '订单备注',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `send_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_image` varchar(500) DEFAULT '' COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `quantity` int NOT NULL COMMENT '数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 用户收货地址表
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(50) NOT NULL COMMENT '省',
  `city` varchar(50) NOT NULL COMMENT '市',
  `district` varchar(50) NOT NULL COMMENT '区',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `is_default` tinyint DEFAULT 0 COMMENT '是否默认：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- 插入测试数据

-- 插入商品分类
INSERT INTO `category` (`name`, `parent_id`, `level`, `sort`) VALUES
('手机数码', 0, 1, 1),
('电脑办公', 0, 1, 2),
('家用电器', 0, 1, 3),
('服装鞋包', 0, 1, 4),
('食品生鲜', 0, 1, 5);

INSERT INTO `category` (`name`, `parent_id`, `level`, `sort`) VALUES
('手机', 1, 2, 1),
('耳机', 1, 2, 2),
('笔记本', 2, 2, 1),
('台式机', 2, 2, 2),
('冰箱', 3, 2, 1),
('洗衣机', 3, 2, 2);

-- 插入商品数据
INSERT INTO `product` (`name`, `category_id`, `price`, `original_price`, `stock`, `sales`, `main_image`, `description`) VALUES
('iPhone 14 Pro Max 256GB', 6, 9999.00, 10999.00, 100, 50, 'https://example.com/iphone14.jpg', 'Apple iPhone 14 Pro Max 256GB 暗紫色'),
('华为 Mate 60 Pro', 6, 6999.00, 7999.00, 200, 120, 'https://example.com/huawei60.jpg', '华为 Mate 60 Pro 12GB+512GB'),
('AirPods Pro 2', 7, 1999.00, 2499.00, 500, 300, 'https://example.com/airpods.jpg', 'Apple AirPods Pro 第二代'),
('联想小新Pro16', 8, 5999.00, 6999.00, 80, 60, 'https://example.com/lenovo.jpg', '联想小新Pro16 2024 R7-7840H'),
('戴尔XPS 13', 8, 8999.00, 9999.00, 50, 30, 'https://example.com/dell.jpg', '戴尔XPS 13 2024 i7-1360P'),
('海尔冰箱 BCD-218', 10, 2999.00, 3599.00, 150, 80, 'https://example.com/haier.jpg', '海尔冰箱 BCD-218STPS'),
('小天鹅洗衣机', 11, 1999.00, 2499.00, 120, 90, 'https://example.com/littleswan.jpg', '小天鹅洗衣机 TG100V62WADY5');

-- 插入测试用户（密码：123456，BCrypt加密后）
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', '13800138000');
