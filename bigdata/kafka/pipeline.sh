#!/bin/bash
# =====================================================
# 数据采集管道 - 完整流程
# MySQL -> Kafka -> HDFS -> Hive
# =====================================================

echo "=== 数据采集管道启动 ==="

# 配置参数
SYNC_DATE=${1:-$(date +%Y-%m-%d)}
HDFS_BASE="/data/ecommerce"

echo "采集日期: $SYNC_DATE"

# Step 1: MySQL数据同步到Kafka
echo "Step 1: 同步MySQL数据到Kafka..."

# 使用Kafka Connect或自定义脚本将变更数据发送到Kafka
# 这里使用简化的示例

# Step 2: Kafka数据消费到HDFS
echo "Step 2: 消费Kafka数据到HDFS..."

# 使用Kafka Consumer将数据写入HDFS
kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic order-events \
    --from-beginning \
    --timeout-ms 10000 \
    | hdfs dfs -append - $HDFS_BASE/raw/order-events/dt=$SYNC_DATE/events.json

# Step 3: HDFS数据加载到Hive ODS层
echo "Step 3: 加载HDFS数据到Hive ODS层..."

hive -e "
USE shopping_warehouse;

-- 加载订单数据
LOAD DATA INPATH '$HDFS_BASE/raw/order-events/dt=$SYNC_DATE'
INTO TABLE ods_order PARTITION (dt='$SYNC_DATE');

-- 修复分区
ALTER TABLE ods_order ADD IF NOT EXISTS PARTITION (dt='$SYNC_DATE');
"

# Step 4: 触发ETL任务
echo "Step 4: 触发Spark ETL任务..."

spark-submit \
    --master yarn \
    --deploy-mode client \
    --name "ShoppingETL" \
    --num-executors 2 \
    --executor-memory 2g \
    --executor-cores 2 \
    $SPARK_ETL_PATH/spark_etl.py

echo "=== 数据采集管道完成 ==="
