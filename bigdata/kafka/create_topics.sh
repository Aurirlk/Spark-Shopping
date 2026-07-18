#!/bin/bash
# =====================================================
# Kafka主题创建脚本
# 电商数据采集管道
# =====================================================

echo "=== 创建Kafka主题 ==="

# 订单事件主题
kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --topic order-events \
    --partitions 3 \
    --replication-factor 1 \
    --if-not-exists

# 用户行为事件主题
kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --topic user-behavior \
    --partitions 6 \
    --replication-factor 1 \
    --if-not-exists

# 库存事件主题
kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --topic inventory-events \
    --partitions 3 \
    --replication-factor 1 \
    --if-not-exists

# AI任务主题
kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --topic ai-tasks \
    --partitions 3 \
    --replication-factor 1 \
    --if-not-exists

echo "=== Kafka主题创建完成 ==="

# 列出所有主题
echo "当前主题列表："
kafka-topics.sh --list --bootstrap-server localhost:9092
