#!/bin/bash

# 每日发现用户服务 - 重启脚本
# 支持本地 Git Bash 和远程 Ubuntu 服务器

# 配置变量
PID_FILE="logs/service.pid"

# 检查服务是否在运行
is_service_running() {
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if [ "$pid" = "windows" ]; then
            # Windows 后台模式，检查是否有 Java 进程
            if ps aux | grep -v grep | grep -q "daily-discover-user"; then
                return 0
            fi
        elif kill -0 "$pid" 2>/dev/null; then
            return 0
        fi
    fi
    return 1
}

echo "🔄 重启每日发现用户服务..."
echo

# 检查服务状态
if is_service_running; then
    echo "🔍 检测到服务正在运行，执行重启流程..."
    echo
    
    # 先停止服务
    echo "1. 停止当前服务..."
    if ./stop.sh > /dev/null 2>&1; then
        echo "   ✅ 服务已停止"
    else
        echo "   ⚠️  停止服务时遇到问题"
    fi
    
    # 等待一段时间确保进程完全停止
    echo "2. 等待进程清理..."
    sleep 3
else
    echo "🔍 检测到服务未运行，直接启动服务..."
    echo
fi

# 启动服务
echo "3. 启动新服务..."
if ./start.sh --background; then
    echo
    echo "✅ 服务启动完成"
    echo "💡 查看服务状态: ./start.sh --status"
    echo "💡 查看实时日志: tail -f logs/application.log"
else
    echo
    echo "❌ 服务启动失败"
    echo "💡 查看错误信息: tail -n 20 logs/application.log"
    exit 1
fi

echo
echo "🌐 服务地址: http://localhost:8091"
echo "📚 API文档: http://localhost:8091/user/api"