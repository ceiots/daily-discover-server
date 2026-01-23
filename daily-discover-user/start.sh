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

# 停止正在运行的服务
stop_running_service() {
    local port=8091
    
    echo "🔍 检查端口 ${port} 占用情况..."
    
    # 检查端口是否被占用
    if command -v lsof >/dev/null 2>&1; then
        # 使用 lsof 检查端口占用
        local port_pid=$(lsof -ti:${port} 2>/dev/null | head -1)
        if [ -n "$port_pid" ]; then
            echo "🛑 检测到端口 ${port} 被进程占用 (PID: $port_pid)，停止该进程..."
            kill -9 "$port_pid" 2>/dev/null
            sleep 2
        fi
    elif command -v netstat >/dev/null 2>&1; then
        # 使用 netstat 检查端口占用
        local port_pid=$(netstat -tlnp 2>/dev/null | grep ":${port} " | awk '{print $7}' | cut -d'/' -f1)
        if [ -n "$port_pid" ] && [ "$port_pid" != "-" ]; then
            echo "🛑 检测到端口 ${port} 被进程占用 (PID: $port_pid)，停止该进程..."
            kill -9 "$port_pid" 2>/dev/null
            sleep 2
        fi
    fi
    
    # 检查 PID 文件并停止服务
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
    
    # 再次检查端口是否已释放
    echo "🔍 确认端口 ${port} 已释放..."
    if command -v lsof >/dev/null 2>&1; then
        if lsof -ti:${port} >/dev/null 2>&1; then
            echo "⚠️  端口 ${port} 仍然被占用，尝试强制释放..."
            lsof -ti:${port} | xargs kill -9 2>/dev/null
            sleep 2
        else
            echo "✅ 端口 ${port} 已释放"
        fi
    fi
}

# 检查服务启动状态（用于后台模式）
check_service_status() {
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        
        if [ "$pid" = "windows" ]; then
            # Windows 后台模式，检查日志判断状态
            echo "🔵 Windows 后台模式启动中..."
            echo "📋 查看启动日志..."
            if [ -f "$LOG_FILE" ]; then
                tail -30 "$LOG_FILE"
                echo "💡 使用 'tail -f $LOG_FILE' 查看实时日志"
            else
                echo "⚠️  日志文件不存在，可能启动失败"
            fi
        elif kill -0 "$pid" 2>/dev/null; then
            echo "🟢 进程运行正常 (PID: $pid)"
            echo "📋 查看启动日志..."
            if [ -f "$LOG_FILE" ]; then
                # 显示最后30行日志，重点关注启动信息
                tail -30 "$LOG_FILE" | grep -E "(启动|启动成功|ERROR|Exception|失败)" || tail -10 "$LOG_FILE"
                echo "💡 使用 'tail -f $LOG_FILE' 查看实时日志"
            else
                echo "⚠️  日志文件不存在，可能启动失败"
            fi
        else
            echo "🔴 进程已退出，启动可能失败"
            echo "💡 查看详细错误信息:"
            if [ -f "$LOG_FILE" ]; then
                tail -50 "$LOG_FILE"
                echo "\n🔍 错误摘要:"
                tail -50 "$LOG_FILE" | grep -i -E "(error|exception|failed|无法启动|启动失败)" || echo "未找到明显错误信息"
            else
                echo "日志文件不存在，请检查构建过程"
            fi
            # 清理无效的 PID 文件
            rm -f "$PID_FILE"
        fi
    else
        echo "🔴 PID 文件不存在，启动失败"
    fi
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
            check_service_status
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
    
    # 编译项目并打包
    echo "📦 编译并打包项目..."
    ./mvnw clean package -DskipTests
    echo
    
    # 检查 JAR 文件是否存在
    local jar_file="target/daily-discover-user-1.0.0.jar"
    if [ ! -f "$jar_file" ]; then
        echo "❌ JAR 文件不存在: $jar_file"
        echo "💡 请检查 Maven 构建是否成功"
        exit 1
    fi
    
    echo "🎯 启动服务 (后台模式)..."
    echo "📦 使用 JAR 文件: $jar_file"
    
    # 根据操作系统选择启动方式
    case "$os_type" in
        "linux"|"mac")
            # Linux/Unix 系统 (包括 Ubuntu)
            nohup java -jar "$jar_file" > "$LOG_FILE" 2>&1 &
            local pid=$!
            echo $pid > "$PID_FILE"
            echo "✅ 服务已启动，PID: $pid"
            ;;
        "windows")
            # Windows Git Bash 环境
            # 在 Git Bash 中使用 start 命令启动新窗口
            start "$SERVICE_NAME" /B java -jar "$jar_file" > "$LOG_FILE" 2>&1
            # 在 Windows 下难以获取准确的 PID，使用特殊标记
            echo "windows" > "$PID_FILE"
            echo "✅ 服务已启动 (Windows 后台模式)"
            ;;
        *)
            echo "❌ 不支持的操作系统: $os_type"
            echo "💡 使用前台模式启动..."
            java -jar "$jar_file"
            exit 1
            ;;
    esac
    
    # 等待一段时间让进程开始写入日志
    echo "⏳ 等待进程启动并开始写入日志..."
    sleep 3
    
    # 调用独立的日志监控方法
    monitor_logs_continuously
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
    
    # 编译项目并打包
    echo "📦 编译并打包项目..."
    ./mvnw clean package -DskipTests
    echo
    
    # 检查 JAR 文件是否存在
    local jar_file="target/daily-discover-user-1.0.0.jar"
    if [ ! -f "$jar_file" ]; then
        echo "❌ JAR 文件不存在: $jar_file"
        echo "💡 请检查 Maven 构建是否成功"
        exit 1
    fi
    
    echo "🎯 启动服务..."
    echo "📦 使用 JAR 文件: $jar_file"
    java -jar "$jar_file"
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
    
    # 显示服务启动信息
    echo
    echo "✅ 服务启动完成"
    echo "📝 日志文件: $LOG_FILE"
}

# 执行主函数
main "$@"