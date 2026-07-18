# 前端 Dockerfile - 多阶段构建
# Node.js 构建 + Nginx 部署

# ============ 构建阶段 ============
FROM node:18-alpine AS builder

WORKDIR /app

# 复制 package 文件（利用 Docker 缓存层）
COPY package.json bun.lock ./

# 安装依赖
RUN npm install -g pnpm && pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 构建生产版本
RUN pnpm build

# ============ 运行阶段 ============
FROM nginx:alpine

LABEL maintainer="Shopping Team"
LABEL description="Shopping Frontend"

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 从构建阶段复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 创建缓存目录
RUN mkdir -p /var/cache/nginx && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /usr/share/nginx/html

# 暴露端口
EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
