# 后端 Dockerfile - 多模块 Maven 构建
# 使用多阶段构建减小镜像体积

# ============ 构建阶段 ============
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app

# 复制 pom 文件（利用 Docker 缓存层）
COPY pom.xml .
COPY shopping-backend/pom.xml ./shopping-backend/
COPY shopping-common/pom.xml ./shopping-common/
COPY shopping-gateway/pom.xml ./shopping-gateway/
COPY shopping-order/pom.xml ./shopping-order/
COPY shopping-product/pom.xml ./shopping-product/
COPY shopping-user/pom.xml ./shopping-user/

# 下载依赖（缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY . .

# 构建项目（跳过测试）
RUN mvn clean package -DskipTests -B

# ============ 运行阶段 ============
FROM openjdk:8-jre-slim

LABEL maintainer="Shopping Team"
LABEL description="Shopping Backend Service"

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建非 root 用户
RUN groupadd -r shopping && useradd -r -g shopping shopping

WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /app/shopping-backend/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R shopping:shopping /app

# 切换到非 root 用户
USER shopping

# 暴露端口
EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar ${0} ${@}"]
