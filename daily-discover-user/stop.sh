#!/bin/bash

# 每日发现用户服务 - 停止脚本
# 支持本地 Git Bash 和远程 Ubuntu 服务器

# 配置变量
SERVICE_NAME="${SERVICE_NAME:-daily-discover-user}"
SERVICE_PORT="${SERVICE_PORT:-8091}"

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

# 查找占用指定端口的进程
find_port_process() {
    local port="$1"
    
    # 尝试使用 lsof 查找端口进程
    local pid=$(lsof -ti:$port 2>/dev/null)
    if [ -n "$pid" ]; then
        echo "$pid"
        return 0
    fi
    
    # 如果 lsof 不可用，使用 netstat
    pid=$(netstat -tulpn 2>/dev/null | grep ":$port " | awk '{print $7}' | cut -d'/' -f1)
    if [ -n "$pid" ]; then
        echo "$pid"
        return 0
    fi
    
    # 如果 netstat 也不可用，使用 ss
    pid=$(ss -tulpn 2>/dev/null | grep ":$port " | awk '{print $7}' | cut -d'=' -f2 | cut -d',' -f1)
    if [ -n "$pid" ]; then
        echo "$pid"
        return 0
    fi
    
    return 1
}

# 停止服务
stop_service() {
    local os_type=$(detect_os)
    
    echo "🛑 停止 $SERVICE_NAME 服务..."
    echo "🌐 服务端口: $SERVICE_PORT"
    echo
    
    # 查找占用端口的进程
    local port_pid=$(find_port_process "$SERVICE_PORT")
    
    if [ -n "$port_pid" ]; then
        echo "🔍 找到占用端口 $SERVICE_PORT 的进程 (PID: $port_pid)..."
        
        case "$os_type" in
            "linux"|"mac")
                # 尝试正常停止
                kill "$port_pid" 2>/dev/null
                
                # 等待进程停止
                local count=0
                while kill -0 "$port_pid" 2>/dev/null && [ $count -lt 5 ]; do
                    echo "⏳ 等待进程停止... ($count/5)"
                    sleep 1
                    count=$((count + 1))
                done
                
                # 如果进程还在，强制终止
                if kill -0 "$port_pid" 2>/dev/null; then
                    echo "⚠️  进程未正常停止，强制终止..."
                    kill -9 "$port_pid" 2>/dev/null
                    sleep 1
                fi
                
                # 最终检查
                if kill -0 "$port_pid" 2>/dev/null; then
                    echo "❌ 无法停止进程 (PID: $port_pid)"
                else
                    echo "✅ 进程已停止 (PID: $port_pid)"
                fi
                ;;
            "windows")
                # Windows 下停止进程
                taskkill //F //PID "$port_pid" 2>/dev/null || echo "⚠️  停止进程失败"
                echo "✅ Windows 进程停止完成"
                ;;
            *)
                echo "❌ 不支持的操作系统: $os_type"
                exit 1
                ;;
        esac
    else
        echo "ℹ️  端口 $SERVICE_PORT 未被占用，服务可能未运行"
    fi
    
    echo
    echo "✅ $SERVICE_NAME 服务停止完成"
    
    # 最终验证
    echo "🔍 最终检查端口状态..."
    local final_check=$(find_port_process "$SERVICE_PORT")
    if [ -n "$final_check" ]; then
        echo "❌ 端口 $SERVICE_PORT 仍然被占用"
        echo "💡 可能需要手动检查: lsof -i:$SERVICE_PORT 或 netstat -tulpn | grep :$SERVICE_PORT"
        return 1
    else
        echo "✅ 端口 $SERVICE_PORT 已释放，服务停止成功"
        return 0
    fi
}

# 显示使用帮助
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -h, --help     显示帮助信息"
    echo
    echo "配置变量 (可通过环境变量覆盖):"
    echo "  SERVICE_NAME: 服务名称 (默认: $SERVICE_NAME)"
    echo "  SERVICE_PORT: 服务端口 (默认: $SERVICE_PORT)"
    echo
    echo "示例:"
    echo "  $0               # 停止服务"
    echo "  SERVICE_PORT=8080 $0  # 使用自定义端口停止服务"
}

# 主函数
main() {
    # 解析命令行参数
    case "${1:--h}" in
        -h|--help)
            show_help
            exit 0
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
    
    stop_service
}

# 执行主函数
main "$@"