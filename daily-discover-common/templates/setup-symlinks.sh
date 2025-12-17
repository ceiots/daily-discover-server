#!/bin/bash

# 集中式模板符号链接部署脚本
# 为各服务创建符号链接，指向集中模板文件

set -e

echo "🔗 开始部署集中式模板符号链接..."

# 检查参数
if [ $# -ne 1 ]; then
    echo "用法: $0 <目标项目路径>"
    echo "示例: $0 /d/daily-discover/daily-discover-server/daily-discover-user"
    exit 1
fi

TARGET_DIR="$1"
TEMPLATE_BASE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 检查目标目录是否存在
if [ ! -d "$TARGET_DIR" ]; then
    echo "❌ 错误: 目标目录不存在: $TARGET_DIR"
    exit 1
fi

echo "📁 目标目录: $TARGET_DIR"
echo "📁 模板目录: $TEMPLATE_BASE"

# 创建符号链接函数
create_symlink() {
    local source="$1"
    local target="$2"
    local link_name="$3"
    
    # 检查源文件是否存在
    if [ ! -e "$source" ]; then
        echo "❌ 错误: 源文件不存在: $source"
        echo "当前目录: $(pwd)"
        ls -la "$(dirname "$source")"
        return 1
    fi
    
    # 如果目标文件已存在，备份并删除
    if [ -e "$target/$link_name" ]; then
        echo "📦 备份现有文件: $target/$link_name"
        mv "$target/$link_name" "$target/${link_name}.backup"
    fi
    
    # 创建符号链接
    echo "🔗 创建符号链接: $link_name -> $source"
    ln -sf "$source" "$target/$link_name"
}

# 创建Maven Wrapper符号链接
echo ""
echo "📦 部署Maven Wrapper符号链接..."
create_symlink "$TEMPLATE_BASE/maven-wrapper/mvnw" "$TARGET_DIR" "mvnw"
create_symlink "$TEMPLATE_BASE/maven-wrapper/mvnw.cmd" "$TARGET_DIR" "mvnw.cmd"

# 创建.mvn目录（如果不存在）
if [ ! -d "$TARGET_DIR/.mvn" ]; then
    mkdir -p "$TARGET_DIR/.mvn"
fi
if [ ! -d "$TARGET_DIR/.mvn/wrapper" ]; then
    mkdir -p "$TARGET_DIR/.mvn/wrapper"
fi

create_symlink "$TEMPLATE_BASE/maven-wrapper/.mvn/wrapper/maven-wrapper.properties" "$TARGET_DIR/.mvn/wrapper" "maven-wrapper.properties"

# 创建启动脚本符号链接
echo ""
echo "🚀 部署启动脚本符号链接..."
create_symlink "$TEMPLATE_BASE/startup-scripts/start-template.bat" "$TARGET_DIR" "start.bat"
create_symlink "$TEMPLATE_BASE/startup-scripts/start-template.sh" "$TARGET_DIR" "start.sh"

echo ""
echo "✅ 符号链接部署完成!"
echo ""
echo "📋 部署的文件:"
echo "   - mvnw -> 集中模板文件"
echo "   - mvnw.cmd -> 集中模板文件"
echo "   - .mvn/wrapper/maven-wrapper.properties -> 集中模板文件"
echo "   - start.bat -> 集中模板文件"
echo "   - start.sh -> 集中模板文件"
echo ""
echo "💡 提示: 修改集中模板文件后，所有服务将自动使用最新版本"
echo "💡 提示: 如需恢复备份文件，请查看对应的 .backup 文件"