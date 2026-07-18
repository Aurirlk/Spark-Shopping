#!/bin/bash
# =====================================================
# MySQL到Hive数据同步脚本
# 使用Sqoop进行数据同步
# =====================================================

# 配置参数
MYSQL_HOST="localhost"
MYSQL_PORT="3306"
MYSQL_DB="shopping"
MYSQL_USER="root"
MYSQL_PASS="1234"

HIVE_DB="shopping_warehouse"
HDFS_BASE="/data/ecommerce"

# 同步日期（默认昨天）
SYNC_DATE=${1:-$(date -d "yesterday" +%Y-%m-%d)}

echo "=== 开始数据同步 ==="
echo "同步日期: $SYNC_DATE"

# 1. 同步订单表
echo "同步订单表..."
sqoop import \
    --connect jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB \
    --username $MYSQL_USER \
    --password $MYSQL_PASS \
    --table order \
    --where "DATE(create_time)='$SYNC_DATE'" \
    --target-dir $HDFS_BASE/ods/order/dt=$SYNC_DATE \
    --delete-target-dir \
    --fields-terminated-by '\001' \
    --null-string '\\N' \
    --null-non-string '\\N' \
    -m 1

# 2. 同步订单明细表
echo "同步订单明细表..."
sqoop import \
    --connect jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB \
    --username $MYSQL_USER \
    --password $MYSQL_PASS \
    --table order_detail \
    --where "DATE(create_time)='$SYNC_DATE'" \
    --target-dir $HDFS_BASE/ods/order_detail/dt=$SYNC_DATE \
    --delete-target-dir \
    --fields-terminated-by '\001' \
    --null-string '\\N' \
    --null-non-string '\\N' \
    -m 1

# 3. 同步商品表
echo "同步商品表..."
sqoop import \
    --connect jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB \
    --username $MYSQL_USER \
    --password $MYSQL_PASS \
    --table product \
    --target-dir $HDFS_BASE/ods/product/dt=$SYNC_DATE \
    --delete-target-dir \
    --fields-terminated-by '\001' \
    --null-string '\\N' \
    --null-non-string '\\N' \
    -m 1

# 4. 同步用户表
echo "同步用户表..."
sqoop import \
    --connect jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB \
    --username $MYSQL_USER \
    --password $MYSQL_PASS \
    --table user \
    --target-dir $HDFS_BASE/ods/user/dt=$SYNC_DATE \
    --delete-target-dir \
    --fields-terminated-by '\001' \
    --null-string '\\N' \
    --null-non-string '\\N' \
    -m 1

# 5. 同步用户行为日志表
echo "同步用户行为日志表..."
sqoop import \
    --connect jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB \
    --username $MYSQL_USER \
    --password $MYSQL_PASS \
    --table user_behavior_log \
    --where "DATE(create_time)='$SYNC_DATE'" \
    --target-dir $HDFS_BASE/ods/user_behavior/dt=$SYNC_DATE \
    --delete-target-dir \
    --fields-terminated-by '\001' \
    --null-string '\\N' \
    --null-non-string '\\N' \
    -m 1

# 6. 修复Hive分区
echo "修复Hive分区..."
hive -e "
USE $HIVE_DB;
ALTER TABLE ods_order ADD IF NOT EXISTS PARTITION (dt='$SYNC_DATE');
ALTER TABLE ods_order_detail ADD IF NOT EXISTS PARTITION (dt='$SYNC_DATE');
ALTER TABLE ods_product ADD IF NOT EXISTS PARTITION (dt='$SYNC_DATE');
ALTER TABLE ods_user ADD IF NOT EXISTS PARTITION (dt='$SYNC_DATE');
ALTER TABLE ods_user_behavior ADD IF NOT EXISTS PARTITION (dt='$SYNC_DATE');
"

echo "=== 数据同步完成 ==="
