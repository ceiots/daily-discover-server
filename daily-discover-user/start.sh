#!/bin/bash

echo "🚀 启动每日发现用户服务..."
echo
echo "☕ 检查 Java 环境..."
java -version
echo
echo "📦 编译项目..."
./mvnw clean compile
echo
echo "🎯 启动服务..."
./mvnw spring-boot:run