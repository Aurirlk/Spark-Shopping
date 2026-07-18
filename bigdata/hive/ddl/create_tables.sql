-- =====================================================
-- Hive数据仓库建表脚本
-- 电商数据分析平台
-- 分层：ODS -> DWD -> DWS -> ADS
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS shopping_warehouse
COMMENT '电商数据仓库'
LOCATION '/data/ecommerce/warehouse';

USE shopping_warehouse;

-- =====================================================
-- ODS层：原始数据层（贴源层）
-- 直接从MySQL同步过来的原始数据
-- =====================================================

-- ODS订单表
DROP TABLE IF EXISTS ods_order;
CREATE EXTERNAL TABLE ods_order (
    `id` BIGINT COMMENT '订单ID',
    `order_no` STRING COMMENT '订单号',
    `user_id` BIGINT COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) COMMENT '总金额',
    `pay_amount` DECIMAL(10,2) COMMENT '实付金额',
    `freight_amount` DECIMAL(10,2) COMMENT '运费',
    `pay_type` INT COMMENT '支付方式',
    `status` INT COMMENT '订单状态',
    `receiver_name` STRING COMMENT '收货人姓名',
    `receiver_phone` STRING COMMENT '收货人电话',
    `receiver_address` STRING COMMENT '收货地址',
    `note` STRING COMMENT '订单备注',
    `pay_time` STRING COMMENT '支付时间',
    `send_time` STRING COMMENT '发货时间',
    `receive_time` STRING COMMENT '收货时间',
    `create_time` STRING COMMENT '创建时间',
    `update_time` STRING COMMENT '更新时间'
) COMMENT 'ODS订单表'
PARTITIONED BY (dt STRING COMMENT '日期分区')
STORED AS ORC
LOCATION '/data/ecommerce/ods/order'
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- ODS订单明细表
DROP TABLE IF EXISTS ods_order_detail;
CREATE EXTERNAL TABLE ods_order_detail (
    `id` BIGINT COMMENT '明细ID',
    `order_id` BIGINT COMMENT '订单ID',
    `product_id` BIGINT COMMENT '商品ID',
    `product_name` STRING COMMENT '商品名称',
    `product_image` STRING COMMENT '商品图片',
    `price` DECIMAL(10,2) COMMENT '单价',
    `quantity` INT COMMENT '数量',
    `total_amount` DECIMAL(10,2) COMMENT '小计金额',
    `create_time` STRING COMMENT '创建时间'
) COMMENT 'ODS订单明细表'
PARTITIONED BY (dt STRING COMMENT '日期分区')
STORED AS ORC
LOCATION '/data/ecommerce/ods/order_detail'
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- ODS商品表
DROP TABLE IF EXISTS ods_product;
CREATE EXTERNAL TABLE ods_product (
    `id` BIGINT COMMENT '商品ID',
    `name` STRING COMMENT '商品名称',
    `category_id` BIGINT COMMENT '分类ID',
    `price` DECIMAL(10,2) COMMENT '价格',
    `original_price` DECIMAL(10,2) COMMENT '原价',
    `stock` INT COMMENT '库存',
    `sales` INT COMMENT '销量',
    `main_image` STRING COMMENT '主图',
    `description` STRING COMMENT '商品描述',
    `status` INT COMMENT '状态',
    `create_time` STRING COMMENT '创建时间',
    `update_time` STRING COMMENT '更新时间'
) COMMENT 'ODS商品表'
PARTITIONED BY (dt STRING COMMENT '日期分区')
STORED AS ORC
LOCATION '/data/ecommerce/ods/product'
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- ODS用户表
DROP TABLE IF EXISTS ods_user;
CREATE EXTERNAL TABLE ods_user (
    `id` BIGINT COMMENT '用户ID',
    `username` STRING COMMENT '用户名',
    `nickname` STRING COMMENT '昵称',
    `phone` STRING COMMENT '手机号',
    `email` STRING COMMENT '邮箱',
    `gender` INT COMMENT '性别',
    `role` INT COMMENT '角色',
    `status` INT COMMENT '状态',
    `create_time` STRING COMMENT '注册时间'
) COMMENT 'ODS用户表'
PARTITIONED BY (dt STRING COMMENT '日期分区')
STORED AS ORC
LOCATION '/data/ecommerce/ods/user'
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- ODS用户行为日志表
DROP TABLE IF EXISTS ods_user_behavior;
CREATE EXTERNAL TABLE ods_user_behavior (
    `id` BIGINT COMMENT '日志ID',
    `user_id` BIGINT COMMENT '用户ID',
    `product_id` BIGINT COMMENT '商品ID',
    `category_id` BIGINT COMMENT '分类ID',
    `behavior_type` INT COMMENT '行为类型',
    `device_type` STRING COMMENT '设备类型',
    `ip_address` STRING COMMENT 'IP地址',
    `session_id` STRING COMMENT '会话ID',
    `create_time` STRING COMMENT '行为时间'
) COMMENT 'ODS用户行为日志表'
PARTITIONED BY (dt STRING COMMENT '日期分区')
STORED AS ORC
LOCATION '/data/ecommerce/ods/user_behavior'
TBLPROPERTIES ('orc.compress'='SNAPPY');

-- =====================================================
-- DWD层：明细数据层
-- 清洗、标准化后的明细数据
-- =====================================================

-- DWD订单明细宽表（关联订单+商品+用户）
DROP TABLE IF EXISTS dwd_order_detail_wide;
CREATE TABLE dwd_order_detail_wide COMMENT 'DWD订单明细宽表'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    od.id AS detail_id,
    od.order_id,
    o.order_no,
    o.user_id,
    u.username,
    u.nickname,
    od.product_id,
    od.product_name,
    p.category_id,
    od.price,
    od.quantity,
    od.total_amount,
    o.pay_type,
    o.status AS order_status,
    o.receiver_name,
    o.receiver_phone,
    o.receiver_address,
    o.create_time AS order_time,
    o.pay_time,
    o.send_time,
    o.receive_time,
    SUBSTR(o.create_time, 1, 10) AS order_date,
    HOUR(o.create_time) AS order_hour,
    DAYOFWEEK(o.create_time) AS order_day_of_week
FROM ods_order o
JOIN ods_order_detail od ON o.id = od.order_id
LEFT JOIN ods_user u ON o.user_id = u.id
LEFT JOIN ods_product p ON od.product_id = p.id
WHERE o.dt = '2024-01-15' AND od.dt = '2024-01-15';

-- DWD用户行为明细表
DROP TABLE IF EXISTS dwd_user_behavior_detail;
CREATE TABLE dwd_user_behavior_detail COMMENT 'DWD用户行为明细表'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    ub.id,
    ub.user_id,
    u.username,
    u.nickname,
    ub.product_id,
    p.name AS product_name,
    p.category_id,
    ub.behavior_type,
    CASE ub.behavior_type
        WHEN 1 THEN '浏览'
        WHEN 2 THEN '搜索'
        WHEN 3 THEN '收藏'
        WHEN 4 THEN '加购'
        WHEN 5 THEN '下单'
        ELSE '未知'
    END AS behavior_name,
    ub.device_type,
    ub.ip_address,
    ub.session_id,
    ub.create_time,
    SUBSTR(ub.create_time, 1, 10) AS behavior_date,
    HOUR(ub.create_time) AS behavior_hour,
    DAYOFWEEK(ub.create_time) AS behavior_day_of_week
FROM ods_user_behavior ub
LEFT JOIN ods_user u ON ub.user_id = u.id
LEFT JOIN ods_product p ON ub.product_id = p.id
WHERE ub.dt = '2024-01-15';

-- =====================================================
-- DWS层：汇总数据层
-- 按主题聚合的汇总数据
-- =====================================================

-- DWS分类销售日统计
DROP TABLE IF EXISTS dws_category_sales_daily;
CREATE TABLE dws_category_sales_daily COMMENT 'DWS分类销售日统计'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    p.category_id,
    order_date,
    COUNT(DISTINCT order_id) AS order_count,
    COUNT(DISTINCT user_id) AS user_count,
    SUM(quantity) AS total_quantity,
    SUM(total_amount) AS total_revenue,
    AVG(total_amount) AS avg_order_amount
FROM dwd_order_detail_wide d
JOIN ods_product p ON d.product_id = p.id
GROUP BY p.category_id, order_date;

-- DWS用户消费日统计
DROP TABLE IF EXISTS dws_user_consume_daily;
CREATE TABLE dws_user_consume_daily COMMENT 'DWS用户消费日统计'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    user_id,
    order_date,
    COUNT(DISTINCT order_id) AS order_count,
    SUM(total_amount) AS total_amount,
    SUM(quantity) AS total_quantity,
    COUNT(DISTINCT product_id) AS product_count
FROM dwd_order_detail_wide
GROUP BY user_id, order_date;

-- DWS商品销售日统计
DROP TABLE IF EXISTS dws_product_sales_daily;
CREATE TABLE dws_product_sales_daily COMMENT 'DWS商品销售日统计'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    product_id,
    product_name,
    order_date,
    COUNT(DISTINCT order_id) AS order_count,
    SUM(quantity) AS total_quantity,
    SUM(total_amount) AS total_revenue,
    COUNT(DISTINCT user_id) AS user_count
FROM dwd_order_detail_wide
GROUP BY product_id, product_name, order_date;

-- DWS用户行为日统计
DROP TABLE IF EXISTS dws_user_behavior_daily;
CREATE TABLE dws_user_behavior_daily COMMENT 'DWS用户行为日统计'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    user_id,
    behavior_date,
    COUNT(CASE WHEN behavior_type=1 THEN 1 END) AS view_count,
    COUNT(CASE WHEN behavior_type=2 THEN 1 END) AS search_count,
    COUNT(CASE WHEN behavior_type=3 THEN 1 END) AS favorite_count,
    COUNT(CASE WHEN behavior_type=4 THEN 1 END) AS cart_count,
    COUNT(CASE WHEN behavior_type=5 THEN 1 END) AS order_count
FROM dwd_user_behavior_detail
GROUP BY user_id, behavior_date;

-- =====================================================
-- ADS层：应用数据层
-- 面向业务应用的结果数据
-- =====================================================

-- ADS热销商品Top10
DROP TABLE IF EXISTS ads_hot_products;
CREATE TABLE ads_hot_products COMMENT 'ADS热销商品Top10'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    product_id,
    product_name,
    SUM(total_quantity) AS total_sold,
    SUM(total_revenue) AS total_revenue,
    COUNT(DISTINCT user_id) AS buyer_count,
    RANK() OVER (ORDER BY SUM(total_quantity) DESC) AS rank_num
FROM dws_product_sales_daily
GROUP BY product_id, product_name
ORDER BY total_sold DESC
LIMIT 10;

-- ADS分类销售排行
DROP TABLE IF EXISTS ads_category_ranking;
CREATE TABLE ads_category_ranking COMMENT 'ADS分类销售排行'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    category_id,
    SUM(order_count) AS total_orders,
    SUM(user_count) AS total_users,
    SUM(total_quantity) AS total_quantity,
    SUM(total_revenue) AS total_revenue,
    RANK() OVER (ORDER BY SUM(total_revenue) DESC) AS rank_num
FROM dws_category_sales_daily
GROUP BY category_id;

-- ADS用户价值分层
DROP TABLE IF EXISTS ads_user_value;
CREATE TABLE ads_user_value COMMENT 'ADS用户价值分层'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    user_id,
    SUM(order_count) AS total_orders,
    SUM(total_amount) AS total_amount,
    SUM(total_quantity) AS total_quantity,
    CASE
        WHEN SUM(total_amount) >= 10000 THEN '高价值'
        WHEN SUM(total_amount) >= 5000 THEN '中价值'
        WHEN SUM(total_amount) >= 1000 THEN '低价值'
        ELSE '潜在用户'
    END AS user_level
FROM dws_user_consume_daily
GROUP BY user_id;

-- ADS转化漏斗
DROP TABLE IF EXISTS ads_conversion_funnel;
CREATE TABLE ads_conversion_funnel COMMENT 'ADS转化漏斗'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    behavior_date,
    COUNT(CASE WHEN behavior_type=1 THEN 1 END) AS view_count,
    COUNT(CASE WHEN behavior_type=3 THEN 1 END) AS favorite_count,
    COUNT(CASE WHEN behavior_type=4 THEN 1 END) AS cart_count,
    COUNT(CASE WHEN behavior_type=5 THEN 1 END) AS order_count,
    ROUND(COUNT(CASE WHEN behavior_type=5 THEN 1 END) * 100.0 /
          NULLIF(COUNT(CASE WHEN behavior_type=1 THEN 1 END), 0), 2) AS conversion_rate
FROM dwd_user_behavior_detail
GROUP BY behavior_date;

-- ADS月度销售趋势
DROP TABLE IF EXISTS ads_monthly_trend;
CREATE TABLE ads_monthly_trend COMMENT 'ADS月度销售趋势'
STORED AS ORC
TBLPROPERTIES ('orc.compress'='SNAPPY') AS
SELECT
    SUBSTR(order_date, 1, 7) AS month,
    COUNT(DISTINCT order_id) AS order_count,
    COUNT(DISTINCT user_id) AS user_count,
    SUM(total_quantity) AS total_quantity,
    SUM(total_revenue) AS total_revenue
FROM dwd_order_detail_wide
GROUP BY SUBSTR(order_date, 1, 7);

SELECT '=== Hive数据仓库建表完成 ===' AS info;
