#!/bin/bash

# 每日发现用户服务 - 跨平台启动脚本
# 支持本地 Git Bash 和远程 Ubuntu 服务器

# 配置变量
SERVICE_NAME="${SERVICE_NAME:-daily-discover-user}"
JAR_FILE="${JAR_FILE:-target/daily-discover-user-1.0.0.jar}"
LOG_FILE="logs/application.log"
PID_FILE="logs/service.pid"
SERVICE_PORT="${SERVICE_PORT:-8091}"
MAVEN_ARGS="${MAVEN_ARGS:--DskipTests}"

# 代理配置（通过环境变量获取，可选）
# 设置方式：export PROXY_SERVER="http://your-proxy-server:port"

# 创建日志目录
mkdir -p logs

# 设置代理环境变量（可选）
setup_proxy() {
    # 从环境变量获取代理配置
    local proxy_server="${PROXY_SERVER:-}"
    
    if [ -n "$proxy_server" ]; then
        echo "🔧 设置代理: $proxy_server"
        export http_proxy="$proxy_server"
        export https_proxy="$proxy_server"
        export HTTP_PROXY="$proxy_server"
        export HTTPS_PROXY="$proxy_server"
    else
        echo "ℹ️  未配置代理，使用系统默认网络设置"
    fi
}

# 拉取最新代码
pull_latest_code() {
    #if [ -d ".git" ]; then
        echo "📥 拉取最新代码..."
        git fetch origin
        git pull origin main
        echo "✅ 代码更新完成"
    #else
    #    echo "ℹ️  当前目录不是 Git 仓库，跳过代码拉取"
    #fi
}

# 检测操作系统类型
detect_os() {
    case "$(uname -s)" in
        Linux*)     echo "linux";;
        Darwin*)    echo "mac";;
        CYGWIN*)    echo "windows";;
        MINGW*)     echo "windows";;
        *)          echo "unknown";;
    esac
}


# 持续监控日志输出
monitor_logs_continuously() {
    echo "📊 开始持续监控日志输出..."
    echo "💡 按 Ctrl+C 停止监控（服务会继续在后台运行）"
    echo "--- 开始日志输出 ---"
    
    if [ -f "$LOG_FILE" ]; then
        # 显示已有的日志
        if [ -s "$LOG_FILE" ]; then
            echo "📋 已有日志内容:"
            tail -20 "$LOG_FILE"
            echo "--- 开始实时监控 ---"
        fi
        
        # 持续监控新日志
        tail -f "$LOG_FILE"
    else
        echo "⚠️  日志文件不存在，等待日志文件创建..."
        # 等待日志文件创建
        local wait_count=0
        while [ $wait_count -lt 12 ] && [ ! -f "$LOG_FILE" ]; do
            sleep 5
            wait_count=$((wait_count + 1))
            echo "⏱️  等待日志文件创建... ($wait_count/12)"
        done
        
        if [ -f "$LOG_FILE" ]; then
            echo "✅ 日志文件已创建，开始监控..."
            tail -f "$LOG_FILE"
        else
            echo "❌ 日志文件未创建，可能启动失败"
            echo "💡 检查服务状态: ./start.sh --status"
        fi
    fi
}



# 后台启动服务
start_background() {
    echo "🚀 启动每日发现用户服务 (后台模式)..."
    echo "📝 日志文件: $LOG_FILE"
    echo "📄 PID 文件: $PID_FILE"
    echo
    
    # 1. 设置代理
    setup_proxy
    echo
    
    # 2. 拉取最新代码
    pull_latest_code
    echo
    
    # 3. 调用重启服务
    restart_service
}



# 显示使用帮助
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -b, --background   启动服务 (默认)"
    echo "  -r, --restart      重启服务"
    echo "  -h, --help         显示帮助信息"
    echo
    echo "配置变量 (可通过环境变量覆盖):"
    echo "  SERVICE_NAME: 服务名称 (默认: $SERVICE_NAME)"
    echo "  JAR_FILE: JAR文件路径 (默认: $JAR_FILE)"
    echo "  SERVICE_PORT: 服务端口 (默认: $SERVICE_PORT)"
    echo "  MAVEN_ARGS: Maven构建参数 (默认: $MAVEN_ARGS)"
    echo
    echo "示例:"
    echo "  $0 -b              # 启动服务 (推荐生产环境)"
    echo "  $0 --restart       # 重启服务"
    echo "  SERVICE_PORT=8080 $0 -b  # 使用自定义端口启动服务"
}



# 重启服务
restart_service() {
    echo "🔄 重启每日发现用户服务..."
    echo
    
    # 1. 停止当前服务
    echo "1. 停止当前服务..."
    ./stop.sh
    
    # 等待一段时间确保进程完全停止
    echo "2. 等待进程清理..."
    sleep 3
    
    # 3. 启动服务核心逻辑
    echo "3. 启动新服务..."
    start_service_core
}

# 启动服务核心逻辑
start_service_core() {
    local os_type=$(detect_os)
    
    echo "🚀 启动每日发现用户服务..."
    echo "📝 日志文件: $LOG_FILE"
    echo "📄 PID 文件: $PID_FILE"
    echo
    
    # 显示操作系统检测结果
    echo "🔍 检测到操作系统类型: $os_type"
    echo
    
    # 检查 Java 环境
    echo "☕ 检查 Java 环境..."
    java -version
    echo
    
    # 编译项目并打包
    echo "📦 编译并打包项目..."
    ./mvnw clean package $MAVEN_ARGS
    echo
    
    # 检查 JAR 文件是否存在
    if [ ! -f "$JAR_FILE" ]; then
        echo "❌ JAR 文件不存在: $JAR_FILE"
        echo "💡 请检查 Maven 构建是否成功"
        exit 1
    fi
    
    echo "🎯 启动服务..."
    echo "📦 使用 JAR 文件: $JAR_FILE"
    
    # 根据操作系统选择启动方式
    case "$os_type" in
        "linux"|"mac")
            # Linux/Unix 系统 (包括 Ubuntu)
            nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
            local pid=$!
            echo $pid > "$PID_FILE"
            echo "✅ 服务已启动，PID: $pid"
            ;;
        "windows")
            # Windows Git Bash 环境
            # 在 Git Bash 中使用 start 命令启动新窗口
            start "$SERVICE_NAME" /B java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1
            # 在 Windows 下难以获取准确的 PID，使用特殊标记
            echo "windows" > "$PID_FILE"
            echo "✅ 服务已启动 (Windows 后台模式)"
            ;;
        *)
            echo "❌ 不支持的操作系统: $os_type"
            echo "💡 使用前台模式启动..."
            java -jar "$JAR_FILE"
            exit 1
            ;;
    esac
    
    # 等待一段时间让进程开始写入日志
    echo "⏳ 等待进程启动并开始写入日志..."
    sleep 3
    
    # 调用独立的日志监控方法
    monitor_logs_continuously
}

# 主函数
main() {
    # 解析命令行参数
    case "${1:--b}" in
        -b|--background)
            start_background
            ;;
        -r|--restart)
            restart_service
            exit 0
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo "❌ 未知选项: $1"
            show_help
            exit 1
            ;;
    esac
    
    # 显示服务启动信息
    echo
    echo "✅ 服务启动完成"
    echo "📝 日志文件: $LOG_FILE"
    echo "🌐 服务端口: $SERVICE_PORT"
}

# 执行主函数
main "$@"