#!/bin/bash

# 每日发现用户服务 - 停止脚本
# 支持本地 Git Bash 和远程 Ubuntu 服务器

# 配置变量
SERVICE_NAME="daily-discover-user"
PID_FILE="logs/service.pid"

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

# 停止服务
stop_service() {
    local os_type=$(detect_os)
    
    echo "🛑 停止每日发现用户服务..."
    
    if [ ! -f "$PID_FILE" ]; then
        echo "⚠️  PID 文件不存在，服务可能未运行"
        echo "💡 尝试查找并停止相关进程..."
        
        # 查找相关的 Java 进程
        case "$os_type" in
            "linux"|"mac")
                local pids=$(ps aux | grep "daily-discover-user" | grep -v grep | awk '{print $2}')
                if [ -n "$pids" ]; then
                    echo "🔍 找到相关进程: $pids"
                    kill $pids
                    echo "✅ 已停止相关进程"
                else
                    echo "ℹ️  未找到相关进程"
                fi
                ;;
            "windows")
                # Windows 下使用 taskkill
                taskkill //F //IM java.exe 2>/dev/null || echo "ℹ️  未找到 Java 进程"
                ;;
        esac
        
        exit 0
    fi
    
    local pid=$(cat "$PID_FILE")
    
    case "$os_type" in
        "linux"|"mac")
            if kill -0 "$pid" 2>/dev/null; then
                echo "🔍 停止进程 (PID: $pid)..."
                kill "$pid"
                
                # 等待进程停止
                local count=0
                while kill -0 "$pid" 2>/dev/null && [ $count -lt 10 ]; do
                    echo "⏳ 等待进程停止... ($count/10)"
                    sleep 1
                    count=$((count + 1))
                done
                
                if kill -0 "$pid" 2>/dev/null; then
                    echo "⚠️  进程未正常停止，强制终止..."
                    kill -9 "$pid"
                fi
                
                rm -f "$PID_FILE"
                echo "✅ 服务已停止"
            else
                echo "⚠️  PID 文件存在但进程不存在"
                rm -f "$PID_FILE"
                echo "✅ 已清理无效的 PID 文件"
            fi
            ;;
        "windows")
            if [ "$pid" = "windows" ]; then
                echo "🔍 停止 Windows 后台服务..."
                # Windows 下停止所有相关的 Java 进程
                taskkill //F //IM java.exe 2>/dev/null || echo "ℹ️  未找到 Java 进程"
                rm -f "$PID_FILE"
                echo "✅ 服务已停止"
            else
                echo "❌ 无效的 PID 文件内容"
                rm -f "$PID_FILE"
            fi
            ;;
        *)
            echo "❌ 不支持的操作系统: $os_type"
            exit 1
            ;;
    esac
}

# 显示使用帮助
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -h, --help   显示帮助信息"
    echo "  -f, --force   强制停止服务"
    echo
    echo "示例:"
    echo "  $0           # 正常停止服务"
    echo "  $0 --force   # 强制停止服务"
}

# 主函数
main() {
    local force=false
    
    # 解析命令行参数
    case "${1:--h}" in
        -h|--help)
            show_help
            exit 0
            ;;
        -f|--force)
            force=true
            ;;
        "")
            # 无参数，正常停止
            ;;
        *)
            echo "❌ 未知选项: $1"
            show_help
            exit 1
            ;;
    esac
    
    if [ "$force" = true ]; then
        echo "💥 强制停止模式..."
        # 强制停止所有相关进程
        local os_type=$(detect_os)
        case "$os_type" in
            "linux"|"mac")
                pkill -f "daily-discover-user" 2>/dev/null || echo "ℹ️  未找到相关进程"
                pkill -f "mvnw" 2>/dev/null || echo "ℹ️  未找到 Maven 进程"
                ;;
            "windows")
                taskkill //F //IM java.exe 2>/dev/null || echo "ℹ️  未找到 Java 进程"
                ;;
        esac
        rm -f "$PID_FILE"
        echo "✅ 强制停止完成"
    else
        stop_service
    fi
    
    # 显示状态检查命令
    echo
    echo "📋 检查服务状态:"
    echo "   ./start.sh --status"
}

# 执行主函数
main "$@"