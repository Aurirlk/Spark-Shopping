# Spark-Shopping 电商平台

一个功能完整的全栈电商平台，覆盖 C 端用户前台与 B 端管理后台，集成 SaToken 认证鉴权、多模型 AI 智能客服、Elasticsearch 中文分词搜索、秒杀/优惠券/签到系统、数据大屏可视化、Kafka 事件驱动、MinIO 对象存储，Docker 容器化部署等企业级能力。适合作为高等院校计算机专业、软件工程专业和大数据专业学子参考的毕业设计模板。

## 技术栈

- **后端**: Spring Boot 3.1 / Java 17 / MyBatis Plus / SaToken
- **前端**: Vue 3 / Element Plus / ECharts
- **数据库**: MySQL 8.0 / Redis 7 / Elasticsearch 8.11
- **中间件**: Kafka / MinIO
- **AI**: OpenAI / Ollama / 意图识别 / 本地 Agent
- **部署**: Docker Compose / Jenkins

## 功能

C 端（14 个页面）：
- 首页、搜索、商品列表、商品详情、购物车、下单、支付
- 订单列表、订单详情、登录注册、个人中心、秒杀、领券中心、AI 客服

B 端（6 个页面）：
- Dashboard、数据大屏、商品管理、订单管理、活动管理

## 快速启动

```bash
# 1. 初始化数据库
mysql -u root -p1234 < SQL/shopping.sql
mysql -u root -p1234 < SQL/init_order_data.sql

# 2. 启动后端
cd backend && mvn spring-boot:run

# 3. 启动前端
cd frontend && npm install && npm run dev

# 4. 打开 http://localhost:3000
```

双击 `start.bat` 可一键启动前后端。

## 项目结构

```
backend/          Java 后端代码（17 个 Controller、11 个实体类）
frontend/         Vue 前端代码（22 个页面组件、8 个通用组件）
bigdata/          Hadoop/Hive/Spark 配置与脚本
SQL/              数据库初始化脚本
docs/             文档
docker-compose.yml    容器编排
Jenkinsfile           CI/CD 流水线
```

## 部署

```bash
bash deploy.sh dev      # 开发环境部署
bash deploy.sh prod     # 生产环境部署
```
