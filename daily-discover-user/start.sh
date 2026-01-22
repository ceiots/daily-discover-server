#!/bin/bash

# 每日发现用户服务 - 跨平台启动脚本
# 支持本地 Git Bash 和远程 Ubuntu 服务器

# 配置变量
SERVICE_NAME="daily-discover-user"
LOG_FILE="logs/application.log"
PID_FILE="logs/service.pid"

# 代理配置（通过环境变量获取，可选）
# 设置方式：export PROXY_SERVER="http://your-proxy-server:port"

# 创建日志目录
mkdir -p logs

# 设置代理环境变量（可选）
setup_proxy() {
    # 直接导出系统环境变量中的代理配置
    if [ -n "$PROXY_SERVER" ]; then
        echo "🔧 设置代理: $PROXY_SERVER"
        export http_proxy="$PROXY_SERVER"
        export https_proxy="$PROXY_SERVER"
        export HTTP_PROXY="$PROXY_SERVER"
        export HTTPS_PROXY="$PROXY_SERVER"
    else
        echo "ℹ️  未配置代理，使用系统默认网络设置"
    fi
}

# 拉取最新代码
pull_latest_code() {
    if [ -d ".git" ]; then
        echo "📥 拉取最新代码..."
        git fetch origin
        git pull origin main
        echo "✅ 代码更新完成"
    else
        echo "ℹ️  当前目录不是 Git 仓库，跳过代码拉取"
    fi
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

# 停止正在运行的服务
stop_running_service() {
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if [ "$pid" = "windows" ] || kill -0 "$pid" 2>/dev/null; then
            echo "🛑 检测到服务正在运行，停止旧服务..."
            ./stop.sh
            # 等待进程完全停止
            sleep 3
        else
            # 清理无效的 PID 文件
            rm -f "$PID_FILE"
        fi
    fi
}

# 检查服务是否已经在运行（用于前台模式）
check_running() {
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if kill -0 "$pid" 2>/dev/null; then
            echo "⚠️  服务已经在运行 (PID: $pid)"
            echo "   停止命令: ./stop.sh"
            echo "   查看日志: tail -f $LOG_FILE"
            exit 1
        else
            # 清理无效的 PID 文件
            rm -f "$PID_FILE"
        fi
    fi
}

# 后台启动服务
start_background() {
    local os_type=$(detect_os)
    
    echo "🚀 启动每日发现用户服务 (后台模式)..."
    echo "📝 日志文件: $LOG_FILE"
    echo "📄 PID 文件: $PID_FILE"
    echo
    
    # 显示操作系统检测结果
    echo "🔍 检测到操作系统类型: $os_type"
    echo
    
    # 1. 设置代理
    setup_proxy
    echo
    
    # 2. 拉取最新代码
    pull_latest_code
    echo
    
    # 3. 停止旧服务（如果正在运行）
    stop_running_service
    echo
    
    # 检查 Java 环境
    echo "☕ 检查 Java 环境..."
    java -version
    echo
    
    # 编译项目
    echo "📦 编译项目..."
    ./mvnw clean compile
    echo
    
    # 根据操作系统选择启动方式
    case "$os_type" in
        "linux"|"mac")
            # Linux/Unix 系统 (包括 Ubuntu)
            echo "🎯 启动服务 (后台模式)..."
            nohup ./mvnw spring-boot:run > "$LOG_FILE" 2>&1 &
            local pid=$!
            echo $pid > "$PID_FILE"
            echo "✅ 服务已启动，PID: $pid"
            ;;
        "windows")
            # Windows Git Bash 环境
            echo "🎯 启动服务 (后台模式)..."
            # 在 Git Bash 中使用 start 命令启动新窗口
            start "$SERVICE_NAME" /B ./mvnw spring-boot:run > "$LOG_FILE" 2>&1
            # 在 Windows 下难以获取准确的 PID，使用特殊标记
            echo "windows" > "$PID_FILE"
            echo "✅ 服务已启动 (Windows 后台模式)"
            ;;
        *)
            echo "❌ 不支持的操作系统: $os_type"
            echo "💡 使用前台模式启动..."
            ./mvnw spring-boot:run
            exit 1
            ;;
    esac
}

# 前台启动服务
start_foreground() {
    local os_type=$(detect_os)
    
    echo "🚀 启动每日发现用户服务 (前台模式)..."
    echo
    
    # 显示操作系统检测结果
    echo "🔍 检测到操作系统类型: $os_type"
    echo
    
    echo "☕ 检查 Java 环境..."
    java -version
    echo
    
    echo "📦 编译项目..."
    ./mvnw clean compile
    echo
    
    echo "🎯 启动服务..."
    ./mvnw spring-boot:run
}

# 显示使用帮助
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -b, --background   后台启动服务 (默认)"
    echo "  -f, --foreground   前台启动服务"
    echo "  -h, --help         显示帮助信息"
    echo "  -s, --status       检查服务状态"
    echo
    echo "示例:"
    echo "  $0 -b              # 后台启动 (推荐生产环境)"
    echo "  $0 -f              # 前台启动 (推荐开发环境)"
    echo "  $0 --status        # 检查服务状态"
}

# 检查服务状态
check_status() {
    local os_type=$(detect_os)
    
    echo "🔍 检测到操作系统类型: $os_type"
    echo
    
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if [ "$pid" = "windows" ]; then
            echo "🔵 服务状态: Windows 后台模式运行中"
            echo "💡 查看日志: tail -f $LOG_FILE"
        elif kill -0 "$pid" 2>/dev/null; then
            echo "🟢 服务状态: 运行中 (PID: $pid)"
            echo "💡 停止命令: ./stop.sh"
            echo "💡 查看日志: tail -f $LOG_FILE"
        else
            echo "🔴 服务状态: 已停止 (PID 文件存在但进程不存在)"
            rm -f "$PID_FILE"
        fi
    else
        echo "🔴 服务状态: 未运行"
    fi
    
    # 显示最后几行日志
    if [ -f "$LOG_FILE" ]; then
        echo
        echo "📋 最近日志:"
        tail -5 "$LOG_FILE"
    fi
}

# 主函数
main() {
    local mode="background"
    
    # 解析命令行参数
    case "${1:--b}" in
        -b|--background)
            mode="background"
            ;;
        -f|--foreground)
            mode="foreground"
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        -s|--status)
            check_status
            exit 0
            ;;
        *)
            echo "❌ 未知选项: $1"
            show_help
            exit 1
            ;;
    esac
    
    # 检查服务是否已经在运行
    check_running
    
    # 根据模式启动服务
    case "$mode" in
        "background")
            start_background
            ;;
        "foreground")
            start_foreground
            ;;
    esac
    
    # 显示有用的命令提示
    echo
    echo "📋 有用的命令:"
    echo "   查看日志: tail -f $LOG_FILE"
    echo "   检查状态: $0 --status"
    echo "   停止服务: ./stop.sh"
    echo "   重启服务: ./restart.sh"
    echo
    echo "🌐 服务地址: http://localhost:8091"
    echo "📚 API文档: http://localhost:8091/user/api"
}

# 执行主函数
main "$@"