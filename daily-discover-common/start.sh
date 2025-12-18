#!/bin/bash

# 每日发现通用模块启动脚本
SERVICE_NAME="每日发现通用模块"

# 检查是否使用独立模式
STANDALONE_MODE=""
if [ "$1" = "standalone" ]; then
    STANDALONE_MODE="-Pstandalone"
    echo "📋 使用独立模式启动"
fi

echo "🚀 启动 $SERVICE_NAME..."
echo

echo "☕ 检查 Java 环境..."
java -version
echo

echo "📦 编译项目..."
./mvnw clean compile $STANDALONE_MODE
echo

echo "🎯 启动服务..."
./mvnw spring-boot:run $STANDALONE_MODE