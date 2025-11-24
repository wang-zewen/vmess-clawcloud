#!/bin/bash
# VMess Server Java版本构建脚本

echo "=========================================="
echo "🔨 Building VMess Server (Java Version)"
echo "=========================================="

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# 清理并构建
echo "📦 Building with Maven..."
mvn clean package

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ Build successful!"
    echo "=========================================="
    echo "📍 JAR file: target/vmess-server.jar"
    echo ""
    echo "🚀 To run:"
    echo "   java -jar target/vmess-server.jar"
    echo ""
    echo "🐳 To build Docker image:"
    echo "   docker build -t vmess-server:java ."
    echo "=========================================="
else
    echo "❌ Build failed!"
    exit 1
fi
