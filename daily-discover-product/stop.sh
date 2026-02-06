#!/bin/bash

# 每日发现用户服务 - 停止脚本
# 支持本地 Git Bash 和远程 Ubuntu 服务器

# 配置变量
SERVICE_NAME="${SERVICE_NAME:-daily-discover-product}"
SERVICE_PORT="${SERVICE_PORT:-8092}"



# 查找占用指定端口的进程
find_port_process() {
    local port="$1"
    
    # 尝试使用 lsof 查找端口进程
    if command -v lsof >/dev/null 2>&1; then
        local pid=$(lsof -ti:$port 2>/dev/null)
        if [ -n "$pid" ]; then
            echo "$pid"
            return 0
        fi
    fi
    
    # 如果 lsof 不可用，使用 netstat
    if command -v netstat >/dev/null 2>&1; then
        local pid=$(netstat -tulpn 2>/dev/null | grep ":$port " | awk '{print $7}' | cut -d'/' -f1)
        if [ -n "$pid" ]; then
            echo "$pid"
            return 0
        fi
    fi
    
    # 如果 netstat 也不可用，使用 ss
    if command -v ss >/dev/null 2>&1; then
        local pid=$(ss -tulpn 2>/dev/null | grep ":$port " | awk '{print $7}' | cut -d'=' -f2 | cut -d',' -f1)
        if [ -n "$pid" ]; then
            echo "$pid"
            return 0
        fi
    fi
    
    # 如果以上命令都不可用，使用 /proc 文件系统
    if [ -d "/proc" ]; then
        for pid_dir in /proc/[0-9]*; do
            if [ -r "$pid_dir/net/tcp" ]; then
                if grep -q ":$(printf '%04X' $port)" "$pid_dir/net/tcp" 2>/dev/null; then
                    local pid=$(basename "$pid_dir")
                    echo "$pid"
                    return 0
                fi
            fi
        done
    fi
    
    # 最后尝试使用 fuser
    if command -v fuser >/dev/null 2>&1; then
        local pid=$(fuser $port/tcp 2>/dev/null | awk '{print $1}')
        if [ -n "$pid" ]; then
            echo "$pid"
            return 0
        fi
    fi
    
    return 1
}

# 停止服务
stop_service() {
    echo "🛑 停止 $SERVICE_NAME 服务..."
    echo "🌐 服务端口: $SERVICE_PORT"
    echo
    
    # 查找占用端口的进程
    echo "🔍 正在查找占用端口 $SERVICE_PORT 的进程..."
    local port_pid=$(find_port_process "$SERVICE_PORT")
    
    if [ -n "$port_pid" ]; then
        echo "✅ 找到占用端口 $SERVICE_PORT 的进程 (PID: $port_pid)..."
        
        # 显示进程详细信息
        echo "📋 进程详细信息:"
        ps -p "$port_pid" -o pid,user,cmd 2>/dev/null || echo "⚠️  无法获取进程详细信息"
        echo
        
        # 统一停止方式 (支持 Linux/Unix 和 Windows Git Bash)
        echo "🔧 使用 kill 命令停止进程..."
        # 尝试正常停止
        if kill "$port_pid" 2>/dev/null; then
            echo "✅ 已发送停止信号到进程 (PID: $port_pid)"
        else
            echo "❌ 发送停止信号失败"
        fi
        
        # 等待进程停止
        local count=0
        while kill -0 "$port_pid" 2>/dev/null && [ $count -lt 5 ]; do
            echo "⏳ 等待进程停止... ($count/5)"
            sleep 1
            count=$((count + 1))
        done
        
        # 如果进程还在，强制终止
        if kill -0 "$port_pid" 2>/dev/null; then
            echo "⚠️  进程未正常停止，尝试强制终止..."
            if kill -9 "$port_pid" 2>/dev/null; then
                echo "✅ 已发送强制终止信号"
                sleep 1
            else
                echo "❌ 强制终止失败"
            fi
        fi
        
        # 最终检查
        if kill -0 "$port_pid" 2>/dev/null; then
            echo "❌ 无法停止进程 (PID: $port_pid)"
            echo "💡 可能需要手动停止: kill -9 $port_pid"
        else
            echo "✅ 进程已成功停止 (PID: $port_pid)"
        fi
    else
        echo "ℹ️  端口 $SERVICE_PORT 未被占用，服务可能未运行"
        echo "💡 检查端口状态命令:"
        echo "   lsof -i:$SERVICE_PORT 2>/dev/null || netstat -tulpn 2>/dev/null | grep :$SERVICE_PORT || ss -tulpn 2>/dev/null | grep :$SERVICE_PORT"
    fi
    
    echo
    echo "✅ $SERVICE_NAME 服务停止流程完成"
    
    # 最终验证
    echo "🔍 最终检查端口状态..."
    local final_check=$(find_port_process "$SERVICE_PORT")
    if [ -n "$final_check" ]; then
        echo "❌ 端口 $SERVICE_PORT 仍然被占用 (PID: $final_check)"
        echo "💡 可能需要手动检查:"
        echo "   lsof -i:$SERVICE_PORT"
        echo "   netstat -tulpn | grep :$SERVICE_PORT"
        echo "   ps aux | grep $final_check"
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
    local param="${1:-}"
    case "$param" in
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