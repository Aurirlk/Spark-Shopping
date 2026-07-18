<div align="center">

# 🛒 Shopping 电商平台

> 一个功能完整的全栈电商平台 —— 覆盖 C 端用户前台与 B 端管理后台，集成 SaToken 认证鉴权、多模型 AI 智能客服、Elasticsearch 中文分词搜索、秒杀/优惠券/签到系统、数据大屏可视化、Kafka 事件驱动、MinIO 对象存储，Docker 容器化部署等企业级能力。
>
> **适合作为高等院校学子参考的毕业设计模板。**

</div>

## 📋 项目简介

Shopping 是一个功能完整的电商平台，覆盖 C 端用户前台和 B 端管理后台，集成 AI 智能客服、大数据分析、微服务架构等企业级能力。适用于毕业设计、课程实训、个人学习等场景。

## ✨ 核心特性

| 特性 | 说明 |
|------|------|
| 🛍️ **完整电商功能** | 商品管理、购物车、订单、支付、秒杀、优惠券、签到 |
| 🤖 **AI 智能客服** | 意图识别、知识库检索、工具调用、多模型 LLM（OpenAI/Ollama） |
| 📊 **B端管理后台** | 数据大屏、Dashboard报表、商品/订单/活动管理 |
| 🔍 **搜索引擎** | Elasticsearch + IK 中文分词、向量搜索、混合搜索 |
| 🎨 **响应式设计** | PC + 手机双端适配、暗色模式、骨架屏、图片懒加载 |
| 🐳 **Docker 部署** | 一键启动、CI/CD、多环境配置 |
| 🔐 **SaToken 鉴权** | JWT Token、同端互斥、会话管理 |

## 🚀 快速启动

### 前置条件

- JDK 17+
- Node.js 18+
- MySQL 8.0
- Redis 7.x
- Maven 3.8+

### 启动步骤

```bash
# 1. 克隆项目
git clone https://github.com/your-username/shopping.git
cd shopping

# 2. 初始化数据库
mysql -u root -p1234 < SQL/shopping.sql
mysql -u root -p1234 < SQL/init_order_data.sql

# 3. 启动后端
cd backend
mvn spring-boot:run

# 4. 新开终端，启动前端
cd frontend
npm install
npm run dev

# 5. 访问 http://localhost:3000
```

> 双击 `start.bat` 一键启动前后端。

## 🏗️ 技术栈

| 层级 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.1.0 / Java 17 / MyBatis Plus 3.5.5 |
| **认证授权** | SaToken 1.39 + JWT |
| **数据库** | MySQL 8.0 / Redis 7 / Elasticsearch 8.11 |
| **消息队列** | Kafka 3.6 / Spring Kafka |
| **对象存储** | MinIO / 本地文件存储 |
| **AI 服务** | OpenAI / Ollama / 意图识别 / 本地 Agent |
| **前端框架** | Vue 3 / Element Plus / Pinia / Axios |
| **可视化** | ECharts 5 |
| **部署** | Docker Compose / Jenkins CI |

## 📱 功能总览

### C 端（14 页面）

| 页面 | 功能 |
|------|------|
| 首页 | 轮播图、分类宫格、限时秒杀、热销推荐 |
| 搜索 | 热门搜索、搜索历史、建议补全 |
| 商品列表 | 分类筛选、无限滚动、骨架屏 |
| 商品详情 | 收藏、分享、图片懒加载 |
| 购物车 | 满减进度、双端响应、左滑删除 |
| 下单/支付 | 地址选择、15分钟倒计时、支付成功 |
| 订单 | 列表筛选、物流追踪、时间线 |
| 个人中心 | 签到、收藏、优惠券、设置、暗色模式 |
| 秒杀/领券 | 倒计时抢购、满减优惠券 |
| AI 客服 | 意图识别、知识库、工具调用 |

### B 端（6 页面）

| 页面 | 功能 |
|------|------|
| Dashboard | 销售趋势、用户增长图表 |
| 数据大屏 | ECharts 实时统计 |
| 商品管理 | CRUD + 图片上传 |
| 订单管理 | 发货/取消/完成 |
| 活动管理 | 秒杀/优惠券配置 |

## 📸 界面预览

```
首页                 商品列表              购物车
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ 🖼️ 轮播图        │  │ 🔍搜索筛选       │  │ 🛒 商品清单      │
│ 📦 限时秒杀      │  │ 📦 商品卡片     │  │ ────────────   │
│ 🏷️ 分类宫格      │  │ 📄 无限滚动     │  │ 满100减10进度条 │
│ 🔥 热销推荐      │  │                 │  │ 🧾 合计: ¥xxx   │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

## 🐳 Docker 部署

```bash
# 一键部署
bash deploy.sh dev

# 或手动启动服务
docker-compose up -d mysql redis elasticsearch
```

### 服务端口

| 服务 | 端口 |
|------|------|
| 前端 | 3000 |
| 后端 API | 8080 |
| MySQL | 3306 |
| Redis | 6379 |
| Elasticsearch | 9200 |
| Kibana | 5601 |
| MinIO | 9000 / 9001 |

## 📁 目录结构

```
Shopping/
├── backend/          # Java 后端 (Spring Boot)
├── frontend/         # Vue 3 前端
├── bigdata/          # Hadoop/Hive/Spark 配置
├── SQL/              # 数据库脚本
├── docker-compose.yml    # Docker 编排
├── Jenkinsfile           # CI/CD 流水线
├── deploy.sh             # 部署脚本
└── start.bat             # 本地启动
```

## 📄 License

MIT License - 仅供学习参考

---

<div align="center">
⭐ 如果这个项目对你有帮助，欢迎 Star！
</div>
