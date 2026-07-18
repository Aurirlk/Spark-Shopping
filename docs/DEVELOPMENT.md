# Shopping 电商平台 · 开发者文档

> **版本**: v3.0  
> **更新日期**: 2026-06-29  
> **适用人员**: 后端开发、前端开发

---

## 一、项目结构

```
Shopping/
├── backend/                            # 后端服务（Spring Boot 3.1）
│   ├── pom.xml                         # Maven配置
│   ├── src/main/java/com/shopping/
│   │   ├── controller/                 # REST API控制器（17个）
│   │   ├── service/                    # 业务逻辑层（含LLM/Agent）
│   │   ├── entity/                     # 数据表映射（11个）
│   │   ├── mapper/                     # MyBatis数据访问
│   │   ├── event/                      # Kafka事件
│   │   ├── config/                     # SaToken/MinIO/LLM配置
│   │   ├── dto/                        # 数据传输对象
│   │   ├── utils/                      # RedisUtils等工具
│   │   └── exception/                  # 全局异常处理
│   └── src/main/resources/
│       ├── application.yml             # 主配置
│       └── mapper/                     # XML映射文件
│   │   │   │   └── LoginUser.java      # 登录用户
│   │   │   ├── event/                  # Kafka事件
│   │   │   │   ├── OrderEvent.java     # 订单事件
│   │   │   │   ├── UserBehaviorEvent.java  # 用户行为事件
│   │   │   │   ├── EventProducer.java  # 事件生产者
│   │   │   │   └── EventConsumer.java  # 事件消费者
│   │   │   ├── utils/                  # 工具类
│   │   │   │   ├── RedisUtils.java     # Redis工具
│   │   │   │   ├── JwtUtils.java       # JWT工具
│   │   │   │   └── UserContext.java    # 用户上下文
│   │   │   └── exception/              # 异常处理
│   │   └── pom.xml
│   ├── shopping-product/               # 商品服务
│   │   ├── src/main/java/com/shopping/product/
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java      # 商品API
│   │   │   │   └── ProductSearchController.java # 搜索API
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── ProductSearchService.java
│   │   │   │   └── impl/
│   │   │   ├── entity/
│   │   │   │   ├── Product.java
│   │   │   │   └── Category.java
│   │   │   ├── document/               # ES文档
│   │   │   │   └── ProductDocument.java
│   │   │   └── repository/             # ES仓库
│   │   │       └── ProductSearchRepository.java
│   │   └── pom.xml
│   ├── shopping-order/                 # 订单服务
│   │   ├── src/main/java/com/shopping/order/
│   │   │   ├── controller/
│   │   │   │   └── OrderController.java
│   │   │   ├── service/
│   │   │   │   ├── OrderService.java
│   │   │   │   └── impl/
│   │   │   └── entity/
│   │   │       ├── Order.java
│   │   │       └── OrderItem.java
│   │   └── pom.xml
│   ├── shopping-user/                  # 用户服务
│   │   ├── src/main/java/com/shopping/user/
│   │   │   ├── controller/
│   │   │   │   └── UserController.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   └── impl/
│   │   │   └── entity/
│   │   │       ├── User.java
│   │   │       └── UserAddress.java
│   │   └── pom.xml
│   └── Dockerfile                      # 后端Docker镜像
├── frontend/                           # 前端应用（Vue 3）
│   ├── src/
│   │   ├── api/                        # API接口
│   │   │   ├── ai.js                   # AI接口
│   │   │   └── recommend.js            # 推荐接口
│   │   ├── components/                 # 组件
│   │   │   ├── AIChat.vue              # AI聊天组件
│   │   │   └── RecommendSection.vue    # 推荐组件
│   │   ├── views/                      # 页面
│   │   │   ├── Home.vue                # 首页
│   │   │   ├── ProductList.vue         # 商品列表
│   │   │   ├── ProductDetail.vue       # 商品详情
│   │   │   ├── Cart.vue                # 购物车
│   │   │   ├── OrderList.vue           # 订单列表
│   │   │   ├── UserCenter.vue          # 用户中心
│   │   │   └── admin/                  # 管理后台
│   │   │       ├── Dashboard.vue       # 仪表盘
│   │   │       ├── DataScreen.vue      # 数据大屏
│   │   │       ├── ProductManage.vue   # 商品管理
│   │   │       └── OrderManage.vue     # 订单管理
│   │   ├── router/                     # 路由
│   │   ├── stores/                     # 状态管理（Pinia）
│   │   └── App.vue
│   ├── nginx.conf                      # Nginx配置
│   ├── Dockerfile                      # 前端Docker镜像
│   └── package.json
├── bigdata/                            # 大数据模块
│   ├── hadoop/                         # Hadoop配置
│   │   ├── core-site.xml
│   │   ├── hdfs-site.xml
│   │   ├── yarn-site.xml
│   │   └── setup.sh
│   ├── hive/                           # Hive脚本
│   │   ├── ddl/
│   │   │   └── create_tables.sql       # 建表脚本
│   │   ├── etl/
│   │   │   ├── sync_mysql_to_hive.sh   # 数据同步
│   │   │   └── verify_import.py        # 验证脚本
│   │   └── report/
│   │       └── queries.sql             # 统计查询
│   ├── spark/                          # Spark任务
│   │   └── src/main/python/
│   │       ├── spark_stats.py          # 统计任务
│   │       ├── spark_etl.py            # ETL任务
│   │       └── recommendation_trainer.py  # 推荐训练
│   └── kafka/                          # Kafka配置
│       ├── create_topics.sh
│       └── pipeline.sh
├── SQL/                                # 数据库脚本
│   ├── shopping.sql                    # 基础表结构
│   └── init_order_data.sql             # 测试数据生成
├── docs/                               # 文档目录
├── docker-compose.yml                  # Docker编排
├── .env.example                        # 环境变量示例
└── .dockerignore                       # Docker忽略文件
```

---

## 二、技术栈

### 后端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| **Spring Boot** | 2.7.18 | 应用框架 |
| **MyBatis Plus** | 3.5.3.1 | ORM框架 |
| **Spring Security** | - | 安全认证 |
| **Spring Data Redis** | - | Redis集成 |
| **Spring Data Elasticsearch** | - | ES集成 |
| **Spring Kafka** | - | Kafka集成 |
| **JWT (jjwt)** | 0.12.5 | Token认证 |
| **OkHttp** | 4.12.0 | HTTP客户端 |
| **Lombok** | - | 代码简化 |

### 前端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| **Vue** | 3.x | 前端框架 |
| **Element Plus** | 2.x | UI组件库 |
| **Axios** | 1.x | HTTP客户端 |
| **Pinia** | 2.x | 状态管理 |
| **Vue Router** | 4.x | 路由管理 |
| **ECharts** | 5.x | 图表库 |

### 大数据技术

| 技术 | 版本 | 用途 |
|------|------|------|
| **Hadoop** | 3.3+ | 分布式存储 |
| **Hive** | 3.1+ | 数据仓库 |
| **Spark** | 3.5+ | 计算引擎 |
| **Kafka** | 3.6+ | 消息队列 |
| **Elasticsearch** | 8.11 | 搜索引擎 |

---

## 三、开发环境搭建

### 3.1 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8+ | Java开发 |
| Maven | 3.8+ | 构建工具 |
| Node.js | 18+ | 前端构建 |
| Docker | 20.10+ | 容器化 |
| MySQL | 8.0 | 数据库 |
| Redis | 7.x | 缓存 |

### 3.2 后端开发

```bash
# 1. 克隆项目
git clone <repository-url>
cd Shopping

# 2. 构建后端
cd backend
mvn clean install -DskipTests

# 3. 启动后端（开发模式）
cd shopping-backend
mvn spring-boot:run

# 或者运行主类
# com.shopping.backend.ShoppingBackendApplication
```

### 3.3 前端开发

```bash
# 1. 安装依赖
cd frontend
pnpm install  # 或 npm install

# 2. 启动开发服务器
pnpm dev  # 或 npm run dev

# 3. 访问前端
# http://localhost:3000
```

### 3.4 Docker开发

```bash
# 启动基础服务（MySQL、Redis、Kafka、ES）
docker-compose up -d mysql redis zookeeper kafka elasticsearch

# 启动后端（本地运行）
cd backend
mvn spring-boot:run

# 启动前端（本地运行）
cd frontend
pnpm dev
```

---

## 四、开发规范

### 4.1 代码规范

#### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | 大驼峰 | `UserService`, `ProductController` |
| 方法名 | 小驼峰 | `getUserById`, `createOrder` |
| 变量名 | 小驼峰 | `userId`, `productName` |
| 常量 | 全大写 | `MAX_PAGE_SIZE`, `DEFAULT_TIMEOUT` |
| 包名 | 全小写 | `com.shopping.user.service` |

#### 分层规范

```
Controller → Service → Mapper/Repository
    ↓           ↓           ↓
  接口层      业务层      数据层
```

- **Controller**: 只做参数校验和响应封装
- **Service**: 业务逻辑处理
- **Mapper**: 数据库操作
- **Repository**: Elasticsearch操作

### 4.2 Git规范

#### 分支命名

| 分支类型 | 命名格式 | 示例 |
|----------|----------|------|
| 主分支 | `main` | - |
| 开发分支 | `develop` | - |
| 功能分支 | `feature/*` | `feature/ai-chat` |
| 修复分支 | `fix/*` | `fix/login-bug` |
| 发布分支 | `release/*` | `release/v1.0` |

#### 提交信息

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type类型：**
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建/工具

**示例：**
```
feat(ai): 添加AI智能客服功能

- 实现LLM对话服务
- 支持流式输出
- 集成商品和订单上下文

Closes #123
```

### 4.3 API规范

#### RESTful API设计

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/resource` | 查询列表 |
| `GET` | `/api/resource/{id}` | 查询详情 |
| `POST` | `/api/resource` | 创建资源 |
| `PUT` | `/api/resource/{id}` | 更新资源 |
| `DELETE` | `/api/resource/{id}` | 删除资源 |

#### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**错误响应：**
```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

---

## 五、核心模块说明

### 5.1 用户认证模块

**技术实现：**
- JWT Token认证
- Spring Security密码加密
- Redis存储Token

**关键代码：**
```java
// 生成Token
String token = JwtUtils.generateToken(userId, username);

// 存储到Redis
redisUtils.set(RedisUtils.getUserTokenKey(userId), token, 7, TimeUnit.DAYS);

// 验证Token
Claims claims = JwtUtils.parseToken(token);
```

### 5.2 商品搜索模块

**技术实现：**
- Elasticsearch全文搜索
- IK中文分词器
- Redis缓存优化

**关键代码：**
```java
// 搜索商品
NativeQuery query = NativeQuery.builder()
    .withQuery(q -> q.multiMatch(mm -> mm
        .fields("name^3", "description")
        .query(keyword)
        .fuzziness("AUTO")
    ))
    .build();

SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);
```

### 5.3 AI客服模块

**技术实现：**
- OpenAI API调用
- 流式SSE响应
- Redis对话历史
- 商品/订单上下文

**关键代码：**
```java
// 构建上下文
List<ChatMessage> messages = new ArrayList<>();
messages.add(ChatMessage.systemMessage(systemPrompt));
messages.add(ChatMessage.userMessage(userInput));

// 调用LLM
ChatResponse response = llmService.chat(messages);

// 流式输出
Flux<String> stream = llmService.chatStream(messages);
```

### 5.4 推荐算法模块

**技术实现：**
- 基于用户的协同过滤
- 基于物品的协同过滤
- 热门推荐
- Redis缓存

**关键代码：**
```java
// 协同过滤推荐
List<Long> collaborative = collaborativeFiltering(userId, limit);

// 热门推荐
List<Long> hot = getHotProducts(categoryId, limit);

// 混合推荐
Set<Long> resultSet = new LinkedHashSet<>();
resultSet.addAll(collaborative);
resultSet.addAll(hot);
```

### 5.5 Kafka事件驱动

**技术实现：**
- 订单事件（创建、支付、取消）
- 用户行为事件（浏览、收藏、加购、购买）
- 异步处理

**关键代码：**
```java
// 发送事件
OrderEvent event = OrderEvent.created(orderId, orderNo, userId, amount);
eventProducer.sendOrderEvent(event);

// 消费事件
@KafkaListener(topics = "order-events", groupId = "shopping-order-group")
public void handleOrderEvent(ConsumerRecord<String, String> record) {
    OrderEvent event = objectMapper.readValue(record.value(), OrderEvent.class);
    // 处理事件
}
```

---

## 六、数据库设计

### 6.1 核心表结构

#### 用户表 (user)
```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) UNIQUE NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `nickname` VARCHAR(50),
  `phone` VARCHAR(20),
  `email` VARCHAR(100),
  `avatar` VARCHAR(500),
  `gender` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 商品表 (product)
```sql
CREATE TABLE `product` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  `category_id` BIGINT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `original_price` DECIMAL(10,2),
  `stock` INT DEFAULT 0,
  `sales` INT DEFAULT 0,
  `main_image` VARCHAR(500),
  `description` TEXT,
  `status` TINYINT DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 订单表 (order)
```sql
CREATE TABLE `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(50) UNIQUE NOT NULL,
  `user_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `pay_amount` DECIMAL(10,2) NOT NULL,
  `status` TINYINT DEFAULT 0,
  `receiver_name` VARCHAR(50),
  `receiver_phone` VARCHAR(20),
  `receiver_address` VARCHAR(500),
  `pay_time` DATETIME,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 6.2 索引设计

```sql
-- 用户表索引
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_phone ON user(phone);

-- 商品表索引
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_status ON product(status);

-- 订单表索引
CREATE INDEX idx_order_user ON order(user_id);
CREATE INDEX idx_order_status ON order(status);
CREATE INDEX idx_order_no ON order(order_no);
```

---

## 七、配置说明

### 7.1 应用配置

**application.yml:**
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shopping
    username: root
    password: 123123

  redis:
    host: localhost
    port: 6379

  kafka:
    bootstrap-servers: localhost:9092

  elasticsearch:
    uris: localhost:9200

# LLM配置
llm:
  provider: openai
  api-key: ${LLM_API_KEY}
  base-url: https://api.openai.com/v1
  model: gpt-3.5-turbo
```

### 7.2 环境变量

```bash
# .env文件
LLM_API_KEY=your-api-key-here
LLM_BASE_URL=https://api.openai.com/v1
LLM_MODEL=gpt-3.5-turbo
```

---

## 八、测试

### 8.1 单元测试

```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=UserServiceTest

# 生成测试报告
mvn surefire-report:report
```

### 8.2 API测试

```bash
# 测试用户登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "123123"}'

# 测试商品列表
curl http://localhost:8080/api/product/list

# 测试AI聊天
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'
```

---

## 九、常见问题

### 9.1 启动失败

**问题：** MySQL连接失败
```
解决：检查MySQL服务是否启动，用户名密码是否正确
```

**问题：** Redis连接失败
```
解决：检查Redis服务是否启动，密码是否正确
```

### 9.2 编译错误

**问题：** Lombok注解不生效
```
解决：安装Lombok插件，启用注解处理
```

**问题：** MyBatis Plus报错
```
解决：检查Mapper扫描路径，确保@MapperScan配置正确
```

### 9.3 运行时错误

**问题：** JWT Token过期
```
解决：重新登录获取新Token
```

**问题：** Elasticsearch查询失败
```
解决：检查ES服务状态，确认索引是否存在
```

---

## 十、参考资源

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis Plus官方文档](https://baomidou.com/)
- [Vue 3官方文档](https://vuejs.org/)
- [Element Plus官方文档](https://element-plus.org/)
- [Elasticsearch官方文档](https://www.elastic.co/guide/)
- [Kafka官方文档](https://kafka.apache.org/documentation/)
