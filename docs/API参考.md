# API 参考

> **版本**: v3.0  
> **更新日期**: 2026-06-29  
> **Base URL**: `http://localhost:8080/api`

---

## 端点总览

### 用户认证

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/user/register` | 用户注册 | ❌ |
| `POST` | `/user/login` | 用户登录 | ❌ |
| `GET` | `/user/info` | 获取用户信息 | ✅ |
| `PUT` | `/user/info` | 更新用户信息 | ✅ |
| `PUT` | `/user/password` | 修改密码 | ✅ |

### 商品服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/product/list` | 商品列表 | ❌ |
| `GET` | `/product/{id}` | 商品详情 | ❌ |
| `GET` | `/product/hot` | 热销商品 | ❌ |
| `GET` | `/product/recommend` | 推荐商品 | ❌ |

### 订单服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/order/create` | 创建订单 | ✅ |
| `GET` | `/order/list` | 订单列表 | ✅ |
| `GET` | `/order/{id}` | 订单详情 | ✅ |
| `PUT` | `/order/cancel/{id}` | 取消订单 | ✅ |
| `PUT` | `/order/confirm/{id}` | 确认收货 | ✅ |

### 搜索服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/search/products` | 关键词搜索商品 | ❌ |
| `GET` | `/search/vector` | 向量搜索（语义搜索） | ❌ |
| `GET` | `/search/web` | 网络搜索 | ❌ |
| `GET` | `/search/hybrid` | 混合搜索（推荐） | ❌ |
| `GET` | `/search/suggest` | 搜索建议+热门搜索 | ❌ |
| `GET` | `/search/hot-keywords` | 热门搜索 | ❌ |
| `POST` | `/search/sync-all` | 同步全部商品到ES | ✅ |

### AI服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/ai/chat` | AI对话 | ✅ |
| `POST` | `/ai/chat/stream` | 流式对话 | ✅ |
| `GET` | `/ai/conversations` | 会话列表 | ✅ |
| `GET` | `/ai/history/{id}` | 会话历史 | ✅ |
| `DELETE` | `/ai/conversations/{id}` | 删除会话 | ✅ |

### 推荐服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/recommend/user` | 个性化推荐 | ✅ |
| `GET` | `/recommend/similar/{id}` | 相似商品 | ❌ |
| `GET` | `/recommend/hot` | 热门推荐 | ❌ |
| `GET` | `/recommend/reason/{id}` | 推荐理由 | ✅ |
| `POST` | `/recommend/behavior` | 记录行为 | ✅ |

### 收藏服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/favorite/toggle/{productId}` | 切换收藏状态 | ✅ |
| `GET` | `/favorite/check/{productId}` | 检查是否收藏 | ✅ |
| `GET` | `/favorite/list` | 获取收藏列表 | ✅ |

### 优惠券服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/coupon/available` | 可领取优惠券 | ✅ |
| `POST` | `/coupon/claim/{couponId}` | 领取优惠券 | ✅ |
| `GET` | `/coupon/my` | 我的优惠券 | ✅ |

### 秒杀服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/seckill/list` | 秒杀商品列表 | ❌ |
| `POST` | `/seckill/buy/{seckillId}` | 秒杀下单 | ✅ |

### 签到服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/checkin/do` | 每日签到 | ✅ |
| `GET` | `/checkin/status` | 签到状态 | ✅ |

### 物流服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/logistics/track/{orderId}` | 物流追踪 | ✅ |

### 购物车服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/cart/list` | 购物车列表 | ✅ |
| `POST` | `/cart/add` | 添加购物车 | ✅ |
| `PUT` | `/cart/update/{id}` | 更新数量 | ✅ |
| `DELETE` | `/cart/delete/{id}` | 删除购物车项 | ✅ |
| `DELETE` | `/cart/clear` | 清空购物车 | ✅ |

### 支付服务

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/payment/create` | 创建支付单 | ✅ |
| `GET` | `/payment/status/{orderId}` | 查询支付状态 | ✅ |

### 文件上传

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `POST` | `/upload/image` | 上传图片到MinIO | ✅ |

### 收货地址

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/user/address/list` | 地址列表 | ✅ |
| `POST` | `/user/address` | 新增地址 | ✅ |
| `PUT` | `/user/address/{id}` | 更新地址 | ✅ |
| `DELETE` | `/user/address/{id}` | 删除地址 | ✅ |

### 管理后台-活动管理

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| `GET` | `/admin/activity/seckill` | 秒杀活动列表 | ✅ |
| `POST` | `/admin/activity/seckill` | 创建秒杀活动 | ✅ |
| `DELETE` | `/admin/activity/seckill/{id}` | 关闭秒杀活动 | ✅ |
| `GET` | `/admin/activity/coupons` | 优惠券列表 | ✅ |
| `POST` | `/admin/activity/coupons` | 创建优惠券 | ✅ |

---

## 用户认证

### 用户注册

**请求：**
```http
POST /api/user/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123123",
  "nickname": "测试用户",
  "phone": "13800138000"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": 1
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | ✅ | 用户名（唯一） |
| password | String | ✅ | 密码 |
| nickname | String | ❌ | 昵称 |
| phone | String | ❌ | 手机号 |

---

### 用户登录

**请求：**
```http
POST /api/user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123123"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | ✅ | 用户名 |
| password | String | ✅ | 密码 |

---

### 获取用户信息

**请求：**
```http
GET /api/user/info
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "测试用户",
    "phone": "13800138000",
    "email": null,
    "avatar": null,
    "gender": 0
  }
}
```

---

## 商品服务

### 商品列表

**请求：**
```http
GET /api/product/list?categoryId=6&pageNum=1&pageSize=10&sortBy=sales&sortOrder=desc
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "iPhone 14 Pro Max 256GB",
        "categoryId": 6,
        "categoryName": "手机",
        "price": 9999.00,
        "originalPrice": 10999.00,
        "sales": 50,
        "stock": 100,
        "mainImage": "/images/products/iphone.png",
        "status": 1
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | Long | ❌ | 分类ID |
| keyword | String | ❌ | 搜索关键词 |
| pageNum | Integer | ❌ | 页码（默认1） |
| pageSize | Integer | ❌ | 每页数量（默认10） |
| sortBy | String | ❌ | 排序方式：price/sales/default |
| sortOrder | String | ❌ | 排序方向：asc/desc |

---

### 商品详情

**请求：**
```http
GET /api/product/1
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "iPhone 14 Pro Max 256GB",
    "categoryId": 6,
    "categoryName": "手机",
    "price": 9999.00,
    "originalPrice": 10999.00,
    "stock": 100,
    "sales": 50,
    "mainImage": "/images/products/iphone.png",
    "subImages": "/images/iphone-1.jpg,/images/iphone-2.jpg",
    "description": "Apple iPhone 14 Pro Max 256GB 暗紫色",
    "detail": "<p>商品详情HTML</p>",
    "status": 1,
    "createTime": "2024-01-15 10:30:00"
  }
}
```

---

### 热销商品

**请求：**
```http
GET /api/product/hot?limit=10
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "iPhone 14 Pro Max",
      "price": 9999.00,
      "sales": 50,
      "mainImage": "/images/products/iphone.png"
    }
  ]
}
```

---

## 搜索服务

### 搜索商品

**请求：**
```http
GET /api/search/products?keyword=iPhone&categoryId=6&minPrice=5000&maxPrice=15000&sortBy=price&sortOrder=asc&pageNum=1&pageSize=10
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "<em class='highlight'>iPhone</em> 14 Pro Max 256GB",
        "description": "Apple <em class='highlight'>iPhone</em> 14 Pro Max",
        "categoryId": 6,
        "categoryName": "手机",
        "price": 9999.00,
        "sales": 50,
        "mainImage": "/images/products/iphone.png",
        "score": 85.5
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1
  }
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | ❌ | 搜索关键词 |
| categoryId | Long | ❌ | 分类ID |
| minPrice | Double | ❌ | 最低价格 |
| maxPrice | Double | ❌ | 最高价格 |
| sortBy | String | ❌ | 排序方式：price/sales/default |
| sortOrder | String | ❌ | 排序方向：asc/desc |
| pageNum | Integer | ❌ | 页码（默认1） |
| pageSize | Integer | ❌ | 每页数量（默认10） |

---

### 搜索建议

**请求：**
```http
GET /api/search/suggest?keyword=iPh&limit=10
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "iPhone 14 Pro Max 256GB",
    "iPhone 14 Pro",
    "iPhone 14",
    "iPhone 13"
  ]
}
```

---

### 热门搜索

**请求：**
```http
GET /api/search/hot-keywords?limit=10
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "手机",
    "笔记本",
    "耳机",
    "空调",
    "iPhone",
    "华为"
  ]
}
```

---

## 订单服务

### 创建订单

**请求：**
```http
POST /api/order/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "note": "请尽快发货",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": "20240115103000123456"
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| note | String | ❌ | 订单备注 |
| items | Array | ✅ | 订单项列表 |
| items[].productId | Long | ✅ | 商品ID |
| items[].quantity | Integer | ✅ | 购买数量 |

---

### 订单列表

**请求：**
```http
GET /api/order/list?status=0&pageNum=1&pageSize=10
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "20240115103000123456",
        "userId": 1,
        "totalAmount": 11998.00,
        "payAmount": 11998.00,
        "status": 0,
        "statusText": "待付款",
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "receiverAddress": "北京市朝阳区",
        "createTime": "2024-01-15 10:30:00"
      }
    ],
    "total": 20,
    "size": 10,
    "current": 1
  }
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | ❌ | 订单状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消 |
| pageNum | Integer | ❌ | 页码（默认1） |
| pageSize | Integer | ❌ | 每页数量（默认10） |

---

### 取消订单

**请求：**
```http
PUT /api/order/cancel/1
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## AI服务

### AI对话

**请求：**
```http
POST /api/ai/chat
Authorization: Bearer <token>
Content-Type: application/json

{
  "conversationId": "可选，会话ID",
  "message": "你好，有什么推荐的商品吗？",
  "productId": 123,
  "orderId": 456
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": "550e8400-e29b-41d4-a716-446655440000",
    "messageId": "550e8400-e29b-41d4-a716-446655440001",
    "content": "你好！为您推荐以下热销商品...",
    "suggestedQuestions": ["价格是多少？", "有优惠吗？"],
    "relatedProductIds": [101, 102, 103],
    "tokenUsage": {
      "promptTokens": 100,
      "completionTokens": 50,
      "totalTokens": 150
    },
    "done": true
  }
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| conversationId | String | ❌ | 会话ID（为空则创建新会话） |
| message | String | ✅ | 用户消息 |
| productId | Long | ❌ | 商品ID（用于商品咨询） |
| orderId | Long | ❌ | 订单ID（用于订单咨询） |

---

### 流式对话

**请求：**
```http
POST /api/ai/chat/stream
Authorization: Bearer <token>
Content-Type: application/json
Accept: text/event-stream

{
  "message": "你好"
}
```

**响应（SSE）：**
```
data: 你
data: 好
data: ！
data: 有什么
data: 可以帮您
data: 的吗？
data: [DONE]
```

---

### 获取会话列表

**请求：**
```http
GET /api/ai/conversations
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    "550e8400-e29b-41d4-a716-446655440000",
    "550e8400-e29b-41d4-a716-446655440001"
  ]
}
```

---

### 删除会话

**请求：**
```http
DELETE /api/ai/conversations/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 推荐服务

### 个性化推荐

**请求：**
```http
GET /api/recommend/user?limit=10
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [101, 102, 103, 104, 105]
}
```

---

### 相似商品

**请求：**
```http
GET /api/recommend/similar/1?limit=6
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [102, 103, 104, 105, 106, 107]
}
```

---

### 热门推荐

**请求：**
```http
GET /api/recommend/hot?categoryId=6&limit=10
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
}
```

---

### 记录用户行为

**请求：**
```http
POST /api/recommend/behavior?productId=1&behaviorType=1
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**行为类型：**

| 类型 | 值 | 说明 |
|------|-----|------|
| VIEW | 1 | 浏览 |
| FAVORITE | 2 | 收藏 |
| CART | 3 | 加购 |
| PURCHASE | 4 | 购买 |

---

## 收藏服务

### 切换收藏

**请求：**
```http
POST /api/favorite/toggle/{productId}
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 检查收藏状态

**请求：**
```http
GET /api/favorite/check/{productId}
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 收藏列表

**请求：**
```http
GET /api/favorite/list
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [1, 25, 42, 58]
}
```

---

## 优惠券服务

### 可领取优惠券

**请求：**
```http
GET /api/coupon/available
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "满200减20",
      "conditionAmount": 200.00,
      "discountAmount": 20.00,
      "stock": 100,
      "startTime": "2024-01-01",
      "endTime": "2024-12-31"
    }
  ]
}
```

### 领取优惠券

**请求：**
```http
POST /api/coupon/claim/{couponId}
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": "领取成功"
}
```

### 我的优惠券

**请求：**
```http
GET /api/coupon/my
Authorization: Bearer <token>
```

---

## 秒杀服务

### 秒杀商品列表

**请求：**
```http
GET /api/seckill/list
```

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "productId": 10,
      "productName": "iPhone 14 Pro Max",
      "mainImage": "/images/iphone.png",
      "originalPrice": 9999.00,
      "seckillPrice": 6999.00,
      "stock": 50,
      "startTime": "2024-01-15 10:00:00",
      "endTime": "2024-01-15 12:00:00"
    }
  ]
}
```

### 秒杀下单

**请求：**
```http
POST /api/seckill/buy/{seckillId}
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": "秒杀成功！"
}
```

> 注意：同一用户对同一秒杀商品只能购买一次，秒杀商品库存扣减为原子操作。

---

## 签到服务

### 每日签到

**请求：**
```http
POST /api/checkin/do
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "points": 5,
    "streak": 5,
    "message": "签到成功！获得 5 积分"
  }
}
```

**规则：** 连续签到天数越多，获得积分越多（1-7分，7分封顶）。

### 签到状态

**请求：**
```http
GET /api/checkin/status
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "todayChecked": true,
    "totalPoints": 35,
    "weekCheckins": [
      {"checkin_date": "2024-01-11", "points": 1},
      {"checkin_date": "2024-01-12", "points": 2}
    ]
  }
}
```

---

## 物流服务

### 物流追踪

**请求：**
```http
GET /api/logistics/track/{orderId}
Authorization: Bearer <token>
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "orderId": 1001,
    "company": "顺丰速运",
    "trackingNo": "SF1234567890",
    "status": "配送中",
    "currentStep": 7,
    "receiver": "张三 138****8000",
    "address": "北京市朝阳区建国路88号",
    "traces": [
      {"time": "2024-01-12 08:00:00", "desc": "订单已提交"},
      {"time": "2024-01-13 10:00:00", "desc": "商品已出库，等待快递揽收"},
      {"time": "2024-01-13 13:00:00", "desc": "快递已揽收，顺丰速运 已取件"},
      {"time": "2024-01-14 09:00:00", "desc": "快件到达【深圳中转中心】"},
      {"time": "2024-01-14 14:00:00", "desc": "快件已发往【北京中转中心】"},
      {"time": "2024-01-14 22:00:00", "desc": "快件到达【北京中转中心】"},
      {"time": "2024-01-15 08:00:00", "desc": "派送中，快递员【王师傅】正在为您派送"},
      {"time": "2024-01-15 10:30:00", "desc": "已签收，感谢使用顺丰速运"}
    ]
  }
}
```

---

## 智能搜索服务

### 向量搜索（语义搜索）

基于向量相似度的语义搜索，不依赖关键词精确匹配。

**请求：**
```http
GET /api/search/vector?query=适合学生的笔记本&limit=10
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| query | String | ✅ | 搜索描述 |
| limit | Integer | ❌ | 返回数量（默认10） |

### 网络搜索

搜索外部网络信息作为辅助结果。

**请求：**
```http
GET /api/search/web?query=最新手机评测&limit=5
```

### 混合搜索（推荐）

结合关键词匹配、向量相似度和结构化筛选的综合搜索。

**请求：**
```http
GET /api/search/hybrid?query=蓝牙耳机&limit=10&categoryId=4&minPrice=100&maxPrice=500
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| query | String | ✅ | 搜索关键词 |
| limit | Integer | ❌ | 返回数量（默认10） |
| categoryId | Long | ❌ | 分类筛选 |
| minPrice | Double | ❌ | 最低价格 |
| maxPrice | Double | ❌ | 最高价格 |

---

## 管理后台-活动管理

### 秒杀活动列表

**请求：**
```http
GET /api/admin/activity/seckill
Authorization: Bearer <token>
```

### 创建秒杀活动

**请求：**
```http
POST /api/admin/activity/seckill
Authorization: Bearer <token>
Content-Type: application/json

{
  "productId": 10,
  "seckillPrice": 6999.00,
  "stock": 100,
  "startTime": "2024-01-20 10:00:00",
  "endTime": "2024-01-20 12:00:00"
}
```

### 关闭秒杀活动

**请求：**
```http
DELETE /api/admin/activity/seckill/{id}
Authorization: Bearer <token>
```

### 优惠券管理

**请求：**
```http
GET /api/admin/activity/coupons
Authorization: Bearer <token>
```

**创建优惠券：**
```http
POST /api/admin/activity/coupons
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "满500减50",
  "conditionAmount": 500.00,
  "discountAmount": 50.00,
  "stock": 200,
  "startTime": "2024-01-01",
  "endTime": "2024-06-30"
}
```

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录） |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**错误响应示例：**
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

---

## 认证说明

### 获取Token

```bash
# 登录获取Token
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "123123"}'

# 响应
{
  "code": 200,
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 使用Token

```bash
# 在请求头中添加Token
curl http://localhost:8080/api/user/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 分页参数

所有列表接口都支持分页：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页数量 |

**分页响应：**
```json
{
  "records": [],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```
