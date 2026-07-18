# 究极技术栈电商平台整合方案

## TL;DR
> **Summary**: 将 Shopping 项目升级为 Spring Boot + Vue + MyBatis + MySQL + Hadoop/HDFS + Spark + Hive + Kafka + LLM + 移动端的完整商业级电商平台
> **Deliverables**: B端（商家后台）+ C端（用户前台）+ 数据中台 + AI服务
> **Effort**: XL（大型项目）
> **Parallel**: YES - 多模块并行开发
> **Critical Path**: 基础架构 → 数据层 → 业务层 → AI层 → 可视化层

---

## 一、系统架构总览

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              用户接入层                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │   PC端 (Vue)  │  │ 移动端 (H5)  │  │  小程序       │  │  商家后台     │        │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘        │
└─────────┼─────────────────┼─────────────────┼─────────────────┼────────────────┘
          │                 │                 │                 │
          ▼                 ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              API网关层 (Spring Cloud Gateway)                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │   路由转发    │  │   负载均衡    │  │   限流熔断    │  │   鉴权认证    │        │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              微服务层 (Spring Boot)                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │  用户服务     │  │  商品服务     │  │  订单服务     │  │  支付服务     │        │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │  搜索服务     │  │  推荐服务     │  │  消息服务     │  │  AI服务       │        │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              消息队列层 (Kafka)                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │  订单事件     │  │  用户行为     │  │  库存变更     │  │  日志采集     │        │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              数据存储层                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │    MySQL      │  │    Redis     │  │  Elasticsearch│  │    HDFS      │        │
│  │  (业务数据)   │  │  (缓存)      │  │  (搜索引擎)   │  │  (大数据存储) │        │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              大数据层                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │    Hadoop     │  │    Spark     │  │    Hive      │  │   Flink      │        │
│  │  (分布式存储) │  │  (计算引擎)  │  │  (数据仓库)  │  │  (流计算)    │        │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              AI服务层                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │   LLM 推理   │  │  商品推荐    │  │  价格预测     │  │  智能客服     │        │
│  │  (vLLM)      │  │  (Spark ML)  │  │  (LSTM)      │  │  (ChatBot)   │        │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、技术栈详细说明

### 2.1 原有技术栈（保留并增强）

| 技术 | 版本 | 用途 | 增强点 |
|------|------|------|--------|
| Spring Boot | 3.x | 后端框架 | 升级到正式版，添加微服务支持 |
| Vue 3 | 3.3+ | PC端前端 | 添加 TypeScript、Pinia 状态管理 |
| Element Plus | 2.4+ | UI组件库 | 保持不变 |
| MyBatis | 3.5+ | ORM框架 | 添加 MyBatis-Plus 增强 |
| MySQL | 8.0 | 主数据库 | 添加读写分离、分库分表 |

### 2.2 新增技术栈

| 技术 | 版本 | 用途 | 集成方式 |
|------|------|------|----------|
| **Hadoop** | 3.3+ | 分布式存储计算 | HDFS 存储商品图片、日志 |
| **HDFS** | 3.3+ | 分布式文件系统 | 替代本地文件存储 |
| **Spark** | 3.5+ | 大数据计算 | 离线分析、ML模型训练 |
| **Hive** | 3.1+ | 数据仓库 | 用户行为分析、销售报表 |
| **Kafka** | 3.6+ | 消息队列 | 订单事件、日志采集 |
| **LLM** | Qwen-7B | 大语言模型 | 智能客服、商品描述生成 |
| **vLLM** | latest | LLM推理引擎 | 高性能模型服务 |
| **Redis** | 7.x | 缓存 | 会话、热点数据、分布式锁 |
| **Elasticsearch** | 8.x | 搜索引擎 | 商品搜索、日志分析 |
| **Nacos** | 2.x | 注册配置中心 | 服务发现、配置管理 |
| **Sentinel** | 1.8+ | 流量控制 | 限流、熔断、降级 |

---

## 三、数据库设计（Hive数据仓库）

### 3.1 业务数据库（MySQL）

```sql
-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) UNIQUE NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `nickname` VARCHAR(50),
  `phone` VARCHAR(20),
  `email` VARCHAR(100),
  `avatar` VARCHAR(500),
  `role` TINYINT DEFAULT 0 COMMENT '0-普通用户 1-商家 2-管理员',
  `status` TINYINT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE `product` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `shop_id` BIGINT NOT NULL COMMENT '商家ID',
  `category_id` BIGINT NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `original_price` DECIMAL(10,2),
  `stock` INT DEFAULT 0,
  `sales` INT DEFAULT 0,
  `main_image` VARCHAR(500),
  `sub_images` TEXT COMMENT 'JSON数组',
  `description` TEXT,
  `detail` TEXT COMMENT '富文本详情',
  `status` TINYINT DEFAULT 1 COMMENT '0-下架 1-上架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(50) UNIQUE NOT NULL,
  `user_id` BIGINT NOT NULL,
  `shop_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `pay_amount` DECIMAL(10,2) NOT NULL,
  `freight_amount` DECIMAL(10,2) DEFAULT 0,
  `pay_type` TINYINT DEFAULT 1 COMMENT '1-在线支付 2-货到付款',
  `status` TINYINT DEFAULT 0 COMMENT '0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
  `receiver_name` VARCHAR(50),
  `receiver_phone` VARCHAR(20),
  `receiver_address` VARCHAR(500),
  `pay_time` DATETIME,
  `send_time` DATETIME,
  `receive_time` DATETIME,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 商家表
CREATE TABLE `shop` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `shop_name` VARCHAR(100) NOT NULL,
  `logo` VARCHAR(500),
  `description` TEXT,
  `status` TINYINT DEFAULT 0 COMMENT '0-待审核 1-正常 2-封禁',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户行为日志表
CREATE TABLE `user_behavior` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT,
  `product_id` BIGINT,
  `behavior_type` TINYINT COMMENT '1-浏览 2-收藏 3-加购 4-购买',
  `session_id` VARCHAR(100),
  `device_type` VARCHAR(20) COMMENT 'pc/mobile/miniapp',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user_time` (`user_id`, `create_time`),
  INDEX `idx_product_time` (`product_id`, `create_time`)
);
```

### 3.2 数据仓库（Hive）

```sql
-- 创建数据仓库分层
-- ODS层：原始数据
CREATE EXTERNAL TABLE ods_user_behavior (
  `user_id` BIGINT,
  `product_id` BIGINT,
  `behavior_type` INT,
  `session_id` STRING,
  `device_type` STRING,
  `create_time` STRING
) PARTITIONED BY (dt STRING)
STORED AS ORC
LOCATION '/data/ecommerce/ods/user_behavior';

-- DWD层：明细数据
CREATE TABLE dwd_user_behavior_detail AS
SELECT 
  user_id,
  product_id,
  behavior_type,
  session_id,
  device_type,
  create_time,
  HOUR(create_time) as hour,
  DAYOFWEEK(create_time) as day_of_week
FROM ods_user_behavior
WHERE dt = '${bizdate}';

-- DWS层：汇总数据
CREATE TABLE dws_user_daily_stats AS
SELECT
  user_id,
  dt,
  COUNT(CASE WHEN behavior_type=1 THEN 1 END) as browse_count,
  COUNT(CASE WHEN behavior_type=2 THEN 1 END) as favorite_count,
  COUNT(CASE WHEN behavior_type=3 THEN 1 END) as cart_count,
  COUNT(CASE WHEN behavior_type=4 THEN 1 END) as order_count
FROM dwd_user_behavior_detail
GROUP BY user_id, dt;

-- ADS层：应用数据
CREATE TABLE ads_product_recommend AS
SELECT
  user_id,
  COLLECT_LIST(product_id) as recommend_products
FROM (
  SELECT user_id, product_id, 
    ROW_NUMBER() OVER(PARTITION BY user_id ORDER BY score DESC) as rn
  FROM (
    SELECT user_id, product_id, SUM(score) as score
    FROM dwd_user_behavior_detail
    GROUP BY user_id, product_id
  ) t
) t2
WHERE rn <= 20
GROUP BY user_id;
```

---

## 四、项目结构

```
Shopping/
├── backend/                          # 后端服务
│   ├── shopping-gateway/            # API网关
│   │   ├── src/main/java/
│   │   │   └── com.shopping.gateway/
│   │   │       ├── config/          # 网关配置
│   │   │       ├── filter/          # 过滤器
│   │   │       └── GatewayApplication.java
│   │   └── pom.xml
│   │
│   ├── shopping-user/               # 用户服务
│   │   ├── src/main/java/
│   │   │   └── com.shopping.user/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── mapper/
│   │   │       └── entity/
│   │   └── pom.xml
│   │
│   ├── shopping-product/            # 商品服务
│   ├── shopping-order/              # 订单服务
│   ├── shopping-payment/            # 支付服务
│   ├── shopping-search/             # 搜索服务
│   ├── shopping-recommend/          # 推荐服务
│   ├── shopping-ai/                 # AI服务
│   │   ├── src/main/java/
│   │   │   └── com.shopping.ai/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       │   ├── LlmService.java        # LLM推理
│   │   │       │   ├── RecommendService.java   # 推荐算法
│   │   │       │   └── PredictService.java     # 价格预测
│   │   │       └── config/
│   │   └── pom.xml
│   │
│   └── shopping-common/             # 公共模块
│       ├── src/main/java/
│       │   └── com.shopping.common/
│       │       ├── config/
│       │       ├── exception/
│       │       ├── utils/
│       │       └── dto/
│       └── pom.xml
│
├── frontend/                        # PC端前端
│   ├── src/
│   │   ├── api/                     # API接口
│   │   ├── assets/                  # 静态资源
│   │   │   └── images/             # 迁移的图片资源
│   │   ├── components/              # 公共组件
│   │   ├── router/                  # 路由
│   │   ├── stores/                  # 状态管理
│   │   ├── views/                   # 页面
│   │   │   ├── admin/              # B端管理页面
│   │   │   ├── home/               # C端首页
│   │   │   ├── product/            # 商品相关
│   │   │   ├── order/              # 订单相关
│   │   │   └── user/               # 用户中心
│   │   └── App.vue
│   └── package.json
│
├── mobile/                          # 移动端（已迁移）
│   ├── index.html
│   ├── css/
│   ├── js/
│   └── images/
│
├── bigdata/                         # 大数据模块
│   ├── hadoop/                      # Hadoop配置
│   │   ├── core-site.xml
│   │   ├── hdfs-site.xml
│   │   └── yarn-site.xml
│   ├── spark/                       # Spark作业
│   │   ├── src/main/scala/
│   │   │   └── com.shopping.spark/
│   │   │       ├── etl/            # 数据清洗
│   │   │       ├── ml/             # 机器学习
│   │   │       │   ├── RecommendModel.scala   # 推荐模型
│   │   │       │   ├── PricePredictModel.scala # 价格预测
│   │   │       │   └── UserCluster.scala      # 用户聚类
│   │   │       └── analysis/       # 数据分析
│   │   └── pom.xml
│   ├── hive/                        # Hive脚本
│   │   ├── ddl/                    # 建表语句
│   │   ├── etl/                    # ETL脚本
│   │   └── report/                 # 报表脚本
│   └── kafka/                       # Kafka配置
│       └── topics.sh               # 主题创建脚本
│
├── ai/                              # AI模块
│   ├── llm/                         # LLM服务
│   │   ├── model/                   # 模型文件
│   │   ├── server.py               # vLLM推理服务
│   │   └── api.py                  # FastAPI接口
│   └── training/                    # 训练脚本
│       ├── train_recommend.py
│       └── train_price_predict.py
│
├── docker/                          # Docker配置
│   ├── docker-compose.yml          # 一键部署
│   ├── Dockerfile.*                # 各服务Dockerfile
│   └── config/                     # 配置文件
│
├── docs/                            # 文档
│   ├── api/                        # API文档
│   ├── architecture/               # 架构文档
│   └── deployment/                 # 部署文档
│
└── SQL/                             # 数据库脚本
    ├── shopping.sql                # 业务数据库
    ├── hive_init.sql               # Hive初始化
    └── kafka_topics.sql            # Kafka主题
```

---

## 五、核心功能模块

### 5.1 C端功能（用户前台）

| 模块 | 功能 | 技术实现 |
|------|------|----------|
| **首页** | 轮播图、分类导航、推荐商品、秒杀 | Vue + Spark推荐算法 |
| **商品** | 列表、详情、搜索、筛选 | Vue + ES搜索引擎 |
| **购物车** | 增删改查、选中、结算 | Vue + Redis缓存 |
| **订单** | 下单、支付、查看、取消 | Vue + Spring Boot + Kafka |
| **用户中心** | 个人信息、收货地址、收藏 | Vue + MySQL |
| **AI客服** | 智能问答、商品咨询 | Vue + LLM API |
| **移动适配** | 响应式布局、移动端页面 | Vue + SparkMall H5 |

### 5.2 B端功能（商家后台）

| 模块 | 功能 | 技术实现 |
|------|------|----------|
| **商品管理** | 发布、编辑、上下架、库存 | Vue + Spring Boot |
| **订单管理** | 查看、发货、退款 | Vue + Kafka事件 |
| **数据大屏** | 销售统计、用户分析、趋势 | Vue + ECharts + Hive |
| **AI助手** | 商品描述生成、智能定价 | Vue + LLM API |
| **库存预警** | 低库存提醒、补货建议 | Spark + Kafka |

### 5.3 数据中台

| 功能 | 技术栈 | 说明 |
|------|--------|------|
| **用户画像** | Spark + Hive + Redis | 用户标签、行为分析 |
| **商品推荐** | Spark MLlib + LLM | 协同过滤 + 语义推荐 |
| **价格预测** | Spark + LSTM | 历史价格趋势预测 |
| **舆情分析** | Kafka + LLM | 评论情感分析 |
| **实时大屏** | Flink + WebSocket | 实时销售数据 |

---

## 六、Kafka事件设计

### 6.1 Topic设计

```bash
# 创建Kafka主题
kafka-topics.sh --create --topic order-events \
  --partitions 6 --replication-factor 3

kafka-topics.sh --create --topic user-behavior \
  --partitions 12 --replication-factor 3

kafka-topics.sh --create --topic inventory-events \
  --partitions 6 --replication-factor 3

kafka-topics.sh --create --topic ai-tasks \
  --partitions 3 --replication-factor 3
```

### 6.2 事件消息格式

```json
// 订单事件
{
  "eventType": "ORDER_CREATED",
  "orderId": 123456,
  "userId": 789,
  "shopId": 101,
  "totalAmount": 299.00,
  "items": [
    {"productId": 1, "quantity": 2, "price": 149.50}
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}

// 用户行为事件
{
  "eventType": "PRODUCT_VIEW",
  "userId": 789,
  "productId": 1,
  "categoryId": 10,
  "duration": 120,
  "deviceType": "mobile",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 七、LLM集成方案

### 7.1 智能客服

```python
# ai/llm/server.py
from vllm import LLM, SamplingParams

class CustomerServiceLLM:
    def __init__(self, model_path: str):
        self.llm = LLM(
            model=model_path,
            tensor_parallel_size=4,
            max_model_len=4096,
            gpu_memory_utilization=0.9
        )
        self.sampling_params = SamplingParams(
            temperature=0.7,
            top_p=0.9,
            max_tokens=512
        )
    
    def answer_question(self, question: str, context: str = "") -> str:
        prompt = f"""你是一个专业的电商客服助手。请根据以下信息回答用户问题。

商品信息：{context}
用户问题：{question}

请用友好、专业的语气回答："""
        
        output = self.llm.generate([prompt], self.sampling_params)
        return output[0].outputs[0].text
```

### 7.2 商品描述生成

```python
def generate_product_description(product_info: dict) -> str:
    prompt = f"""请为以下商品生成一段吸引人的商品描述：

商品名称：{product_info['name']}
类别：{product_info['category']}
价格：{product_info['price']}元
特点：{product_info['features']}

请生成100-200字的商品描述："""
    
    return llm.generate(prompt)
```

---

## 八、Spark任务示例

### 8.1 用户推荐模型训练

```scala
// bigdata/spark/src/main/scala/com/shopping/spark/ml/RecommendModel.scala
package com.shopping.spark.ml

import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.SparkSession

object RecommendModel {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("UserRecommendModel")
      .config("spark.executor.resource.gpu.amount", "1")
      .config("spark.task.resource.gpu.amount", "0.5")
      .getOrCreate()
    
    // 读取用户行为数据
    val behaviorDF = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/shopping")
      .option("dbtable", "user_behavior")
      .load()
    
    // 构建评分矩阵
    val ratingsDF = behaviorDF
      .filter($"behavior_type" === 4) // 购买行为
      .groupBy("user_id", "product_id")
      .count()
      .withColumnRenamed("count", "rating")
    
    // 训练ALS模型
    val als = new ALS()
      .setMaxIter(10)
      .setRegParam(0.01)
      .setRank(20)
      .setUserCol("user_id")
      .setItemCol("product_id")
      .setRatingCol("rating")
    
    val model = als.fit(ratingsDF)
    
    // 保存模型
    model.save("hdfs:///models/als_recommend")
    
    // 生成推荐结果
    val recommendations = model.recommendForAllUsers(20)
    recommendations.write
      .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/shopping")
      .option("dbtable", "user_recommendations")
      .mode("overwrite")
      .save()
    
    spark.stop()
  }
}
```

---

## 九、部署架构

### 9.1 Docker Compose配置

```yaml
# docker/docker-compose.yml
version: '3.8'

services:
  # 基础设施
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: shopping
    volumes:
      - mysql_data:/var/lib/mysql
      - ../SQL/shopping.sql:/docker-entrypoint-initdb.d/init.sql
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  elasticsearch:
    image: elasticsearch:8.11.0
    ports:
      - "9200:9200"
    environment:
      - discovery.type=single-node
  
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
  
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
  
  # 后端服务
  gateway:
    build:
      context: ../backend
      dockerfile: ../docker/Dockerfile.gateway
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
  
  user-service:
    build:
      context: ../backend
      dockerfile: ../docker/Dockerfile.user
    depends_on:
      - mysql
      - redis
      - kafka
  
  product-service:
    build:
      context: ../backend
      dockerfile: ../docker/Dockerfile.product
    depends_on:
      - mysql
      - redis
      - elasticsearch
  
  order-service:
    build:
      context: ../backend
      dockerfile: ../docker/Dockerfile.order
    depends_on:
      - mysql
      - redis
      - kafka
  
  ai-service:
    build:
      context: ../ai
      dockerfile: Dockerfile
    ports:
      - "8000:8000"
    deploy:
      resources:
        reservations:
          devices:
            - driver: nvidia
              count: 4
              capabilities: [gpu]
  
  # 前端
  frontend:
    build:
      context: ../frontend
      dockerfile: ../docker/Dockerfile.frontend
    ports:
      - "3000:80"
    depends_on:
      - gateway

volumes:
  mysql_data:
```

---

## 十、开发路线图

### Phase 1: 基础架构（2周）
- [ ] Spring Boot 微服务拆分
- [ ] API网关搭建
- [ ] MySQL 数据库完善
- [ ] Redis 缓存集成
- [ ] Vue 前端重构（B端+C端）

### Phase 2: 核心业务（3周）
- [ ] 用户服务（注册、登录、鉴权）
- [ ] 商品服务（CRUD、搜索）
- [ ] 订单服务（下单、支付、状态流转）
- [ ] Kafka 事件驱动集成

### Phase 3: 大数据平台（2周）
- [ ] Hadoop/HDFS 集群搭建
- [ ] Hive 数据仓库建模
- [ ] Spark ETL 任务开发
- [ ] 数据采集管道（Flume/Kafka）

### Phase 4: AI能力（2周）
- [ ] LLM 服务部署（vLLM）
- [ ] 智能客服集成
- [ ] 推荐算法开发
- [ ] 价格预测模型

### Phase 5: 集成测试（1周）
- [ ] 端到端测试
- [ ] 性能压测
- [ ] 部署文档编写

---

## 十一、技术难点与解决方案

| 难点 | 解决方案 |
|------|----------|
| HDFS存取图片 | 自定义FileService，图片上传到HDFS，返回访问URL |
| Spark任务调度 | 使用Apache Airflow或Azkaban调度ETL任务 |
| LLM推理性能 | 使用vLLM + Tensor Parallel，4卡部署Qwen-7B |
| 实时推荐 | Spark Streaming + Redis缓存推荐结果 |
| 数据一致性 | Kafka事务 + 最终一致性方案 |
| 微服务通信 | OpenFeign + Sentinel限流熔断 |

---

## 十二、预期成果

| 指标 | 目标值 |
|------|--------|
| 商品搜索响应 | < 200ms |
| 推荐接口响应 | < 500ms |
| LLM客服响应 | < 2s |
| 订单处理吞吐 | > 1000 TPS |
| 数据分析延迟 | < 5min（离线） |
| 系统可用性 | 99.9% |

---

## Commit Strategy
- 每个Phase完成后提交一次
- 提交信息格式：`feat(phase): 完成xxx模块`

## Success Criteria
1. PC端和移动端均可正常访问
2. B端商家后台功能完整
3. C端用户购物流程通畅
4. 大数据平台正常运行
5. AI服务可正常调用
6. 系统可一键Docker部署
