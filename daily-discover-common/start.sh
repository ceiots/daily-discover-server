#!/bin/bash

# 自动检测项目类型
PROJECT_TYPE="user"
if [ -f "pom.xml" ]; then
    if grep -q "daily-discover-common" "pom.xml"; then
        PROJECT_TYPE="common"
    elif grep -q "daily-discover-user" "pom.xml"; then
        PROJECT_TYPE="user"
    fi
fi

# 设置服务名称
if [ "$PROJECT_TYPE" = "common" ]; then
    SERVICE_NAME="每日发现通用模块"
else
    SERVICE_NAME="每日发现用户服务"
fi

# 检查是否使用独立模式
STANDALONE_MODE=""
if [ "$1" = "standalone" ]; then
    if [ "$PROJECT_TYPE" = "common" ]; then
        STANDALONE_MODE="-Pstandalone"
    else
        echo "警告: 独立模式仅适用于common模块"
    fi
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