#!/bin/bash
# ===== Shopping 一键部署脚本 =====
# 用法: ./deploy.sh [dev|prod]

set -e

ENV=${1:-dev}
COMPOSE_FILE="docker-compose.yml"

echo "🚀 Shopping 部署开始 (环境: ${ENV})"

# 1. 拉取最新代码
if [ -d .git ]; then
    echo "📦 拉取最新代码..."
    git pull origin main
fi

# 2. 构建后端
echo "🔨 构建后端..."
cd backend
mvn clean package -DskipTests -q
cd ..

# 3. 构建前端
echo "🔨 构建前端..."
cd frontend
pnpm install --frozen-lockfile
pnpm build
cd ..

# 4. 启动服务
echo "🐳 启动服务..."
if [ "$ENV" = "prod" ]; then
    docker-compose -f ${COMPOSE_FILE} -f docker-compose.prod.yml up -d --build
else
    docker-compose -f ${COMPOSE_FILE} up -d --build
fi

# 5. 健康检查
echo "🏥 健康检查..."
sleep 10
for i in $(seq 1 12); do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || true)
    if [ "$STATUS" = "200" ]; then
        echo "✅ 服务已就绪!"
        break
    fi
    echo "  等待服务启动... ($i/12)"
    sleep 10
done

echo ""
echo "========================================"
echo "  Shopping 部署完成!"
echo "  前端: http://localhost:3000"
echo "  后端: http://localhost:8080"
echo "  Nacos: http://localhost:8848/nacos"
echo "  Kibana: http://localhost:5601"
echo "  MinIO: http://localhost:9001"
echo "========================================"
