# 多阶段构建 - 构建阶段
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /build

# 复制项目文件
COPY pom.xml .
COPY src ./src

# 构建项目
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:11-jre-jammy

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /build/target/vmess-server.jar .

# 暴露端口
EXPOSE 25727

# 运行应用
CMD ["java", "-jar", "vmess-server.jar"]
