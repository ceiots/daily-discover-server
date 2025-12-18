#!/bin/bash

# 每日发现用户服务启动脚本
SERVICE_NAME="每日发现用户服务"

echo "🚀 启动 $SERVICE_NAME..."
echo

echo "☕ 检查 Java 环境..."
java -version
echo

echo "📦 编译项目..."
./mvnw clean compile
echo

echo "🎯 启动服务..."
./mvnw spring-boot:run