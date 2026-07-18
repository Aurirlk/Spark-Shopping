-- =====================================================
-- Hive统计查询SQL
-- 用于大数据课程展示和验证
-- =====================================================

USE shopping_warehouse;

-- =====================================================
-- 一、基础统计查询
-- =====================================================

-- 1.1 各表记录数统计
SELECT '=== 各表记录数统计 ===' AS info;

SELECT 'ods_order' AS table_name, COUNT(*) AS record_count FROM ods_order
UNION ALL
SELECT 'ods_order_detail', COUNT(*) FROM ods_order_detail
UNION ALL
SELECT 'ods_product', COUNT(*) FROM ods_product
UNION ALL
SELECT 'ods_user', COUNT(*) FROM ods_user
UNION ALL
SELECT 'ods_user_behavior', COUNT(*) FROM ods_user_behavior;

-- =====================================================
-- 二、销售分析查询
-- =====================================================

-- 2.1 各品类销售额排名
SELECT '=== 各品类销售额排名 ===' AS info;

SELECT 
    category_id,
    total_orders,
    total_users,
    total_quantity,
    total_revenue,
    rank_num
FROM ads_category_ranking
ORDER BY rank_num
LIMIT 10;

-- 2.2 热销商品Top10
SELECT '=== 热销商品Top10 ===' AS info;

SELECT 
    product_id,
    product_name,
    total_sold,
    total_revenue,
    buyer_count,
    rank_num
FROM ads_hot_products
ORDER BY rank_num;

-- 2.3 月度销售趋势
SELECT '=== 月度销售趋势 ===' AS info;

SELECT 
    month,
    order_count,
    user_count,
    total_quantity,
    total_revenue
FROM ads_monthly_trend
ORDER BY month;

-- =====================================================
-- 三、用户分析查询
-- =====================================================

-- 3.1 用户价值分层统计
SELECT '=== 用户价值分层统计 ===' AS info;

SELECT 
    user_level,
    COUNT(*) AS user_count,
    SUM(total_orders) AS total_orders,
    SUM(total_amount) AS total_amount,
    AVG(total_amount) AS avg_amount
FROM ads_user_value
GROUP BY user_level
ORDER BY total_amount DESC;

-- 3.2 用户转化漏斗
SELECT '=== 用户转化漏斗 ===' AS info;

SELECT 
    behavior_date,
    view_count,
    favorite_count,
    cart_count,
    order_count,
    conversion_rate
FROM ads_conversion_funnel
ORDER BY behavior_date DESC
LIMIT 7;

-- =====================================================
-- 四、商品分析查询
-- =====================================================

-- 4.1 商品销售日统计（最近7天）
SELECT '=== 商品销售日统计（最近7天）===' AS info;

SELECT 
    product_id,
    product_name,
    order_date,
    order_count,
    total_quantity,
    total_revenue
FROM dws_product_sales_daily
ORDER BY order_date DESC, total_revenue DESC
LIMIT 20;

-- 4.2 分类销售日统计
SELECT '=== 分类销售日统计 ===' AS info;

SELECT 
    category_id,
    order_date,
    order_count,
    user_count,
    total_quantity,
    total_revenue
FROM dws_category_sales_daily
ORDER BY order_date DESC, total_revenue DESC
LIMIT 20;

-- =====================================================
-- 五、用户行为分析查询
-- =====================================================

-- 5.1 用户行为日统计
SELECT '=== 用户行为日统计 ===' AS info;

SELECT 
    behavior_date,
    SUM(view_count) AS total_views,
    SUM(search_count) AS total_searches,
    SUM(favorite_count) AS total_favorites,
    SUM(cart_count) AS total_carts,
    SUM(order_count) AS total_orders
FROM dws_user_behavior_daily
GROUP BY behavior_date
ORDER BY behavior_date DESC
LIMIT 7;

-- 5.2 设备类型分布
SELECT '=== 设备类型分布 ===' AS info;

SELECT 
    device_type,
    COUNT(*) AS behavior_count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) AS percentage
FROM dwd_user_behavior_detail
GROUP BY device_type
ORDER BY behavior_count DESC;

-- 5.3 行为类型分布
SELECT '=== 行为类型分布 ===' AS info;

SELECT 
    behavior_name,
    COUNT(*) AS behavior_count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) AS percentage
FROM dwd_user_behavior_detail
GROUP BY behavior_name
ORDER BY behavior_count DESC;

-- =====================================================
-- 六、高级分析查询
-- =====================================================

-- 6.1 用户消费能力分析
SELECT '=== 用户消费能力分析 ===' AS info;

SELECT 
    CASE 
        WHEN total_amount >= 10000 THEN '高消费用户'
        WHEN total_amount >= 5000 THEN '中高消费用户'
        WHEN total_amount >= 1000 THEN '中低消费用户'
        ELSE '低消费用户'
    END AS user_level,
    COUNT(*) AS user_count,
    AVG(total_amount) AS avg_consumption,
    MIN(total_amount) AS min_consumption,
    MAX(total_amount) AS max_consumption
FROM (
    SELECT user_id, SUM(total_amount) AS total_amount
    FROM dws_user_consume_daily
    GROUP BY user_id
) t
GROUP BY 
    CASE 
        WHEN total_amount >= 10000 THEN '高消费用户'
        WHEN total_amount >= 5000 THEN '中高消费用户'
        WHEN total_amount >= 1000 THEN '中低消费用户'
        ELSE '低消费用户'
    END
ORDER BY avg_consumption DESC;

-- 6.2 商品销售排行（按品类）
SELECT '=== 商品销售排行（按品类）===' AS info;

SELECT 
    p.category_id,
    d.product_id,
    d.product_name,
    SUM(d.total_quantity) AS total_sold,
    SUM(d.total_revenue) AS total_revenue,
    RANK() OVER(PARTITION BY p.category_id ORDER BY SUM(d.total_revenue) DESC) AS rank_in_category
FROM dws_product_sales_daily d
JOIN ods_product p ON d.product_id = p.id
GROUP BY p.category_id, d.product_id, d.product_name
HAVING SUM(d.total_revenue) > 0
ORDER BY p.category_id, rank_in_category
LIMIT 30;

-- 6.3 用户活跃度分析
SELECT '=== 用户活跃度分析 ===' AS info;

SELECT 
    CASE 
        WHEN active_days >= 30 THEN '高活跃用户'
        WHEN active_days >= 15 THEN '中活跃用户'
        WHEN active_days >= 5 THEN '低活跃用户'
        ELSE '沉默用户'
    END AS activity_level,
    COUNT(*) AS user_count,
    AVG(total_behaviors) AS avg_behaviors
FROM (
    SELECT 
        user_id,
        COUNT(DISTINCT behavior_date) AS active_days,
        SUM(view_count + search_count + favorite_count + cart_count + order_count) AS total_behaviors
    FROM dws_user_behavior_daily
    GROUP BY user_id
) t
GROUP BY 
    CASE 
        WHEN active_days >= 30 THEN '高活跃用户'
        WHEN active_days >= 15 THEN '中活跃用户'
        WHEN active_days >= 5 THEN '低活跃用户'
        ELSE '沉默用户'
    END
ORDER BY avg_behaviors DESC;

-- =====================================================
-- 七、数据质量检查
-- =====================================================

-- 7.1 数据完整性检查
SELECT '=== 数据完整性检查 ===' AS info;

SELECT 
    'ODS订单' AS check_item,
    COUNT(*) AS total_count,
    COUNT(CASE WHEN user_id IS NULL THEN 1 END) AS null_user_id,
    COUNT(CASE WHEN total_amount IS NULL THEN 1 END) AS null_amount
FROM ods_order
UNION ALL
SELECT 
    'ODS订单明细',
    COUNT(*),
    COUNT(CASE WHEN product_id IS NULL THEN 1 END),
    COUNT(CASE WHEN price IS NULL THEN 1 END)
FROM ods_order_detail
UNION ALL
SELECT 
    'ODS用户行为',
    COUNT(*),
    COUNT(CASE WHEN behavior_type IS NULL THEN 1 END),
    COUNT(CASE WHEN create_time IS NULL THEN 1 END)
FROM ods_user_behavior;

-- 7.2 数据一致性检查
SELECT '=== 数据一致性检查 ===' AS info;

SELECT 
    '订单金额一致性' AS check_item,
    CASE 
        WHEN ABS(SUM(o.total_amount) - SUM(d.total_amount)) < 0.01 THEN 'PASS'
        ELSE 'FAIL'
    END AS result,
    SUM(o.total_amount) AS order_total,
    SUM(d.total_amount) AS detail_total
FROM ods_order o
JOIN (
    SELECT order_id, SUM(total_amount) AS total_amount 
    FROM ods_order_detail 
    GROUP BY order_id
) d ON o.id = d.order_id;

SELECT '=== 查询完成 ===' AS info;
