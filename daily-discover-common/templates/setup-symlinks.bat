@echo off
setlocal enabledelayedexpansion

REM 集中式模板符号链接部署脚本
REM 为各服务创建符号链接，指向集中模板文件

echo 🔗 开始部署集中式模板符号链接...

REM 检查参数
if "%~1"=="" (
    echo 用法: %0 ^<目标项目路径^>
    echo 示例: %0 d:\daily-discover\daily-discover-server\daily-discover-user
    exit /b 1
)

set "TARGET_DIR=%~1"
set "TEMPLATE_BASE=%~dp0.."

REM 检查目标目录是否存在
if not exist "!TARGET_DIR!" (
    echo ❌ 错误: 目标目录不存在: !TARGET_DIR!
    exit /b 1
)

echo 📁 目标目录: !TARGET_DIR!
echo 📁 模板目录: !TEMPLATE_BASE!

REM 创建符号链接函数
set "ERROR_LEVEL=0"

REM 创建Maven Wrapper符号链接
echo.
echo 📦 部署Maven Wrapper符号链接...

REM mvnw
if exist "!TARGET_DIR!\mvnw" (
    echo 📦 备份现有文件: !TARGET_DIR!\mvnw
    move "!TARGET_DIR!\mvnw" "!TARGET_DIR!\mvnw.backup"
)
mklink "!TARGET_DIR!\mvnw" "!TEMPLATE_BASE!\templates\maven-wrapper\mvnw"
if !errorlevel! neq 0 set "ERROR_LEVEL=!errorlevel!"

REM mvnw.cmd
if exist "!TARGET_DIR!\mvnw.cmd" (
    echo 📦 备份现有文件: !TARGET_DIR!\mvnw.cmd
    move "!TARGET_DIR!\mvnw.cmd" "!TARGET_DIR!\mvnw.cmd.backup"
)
mklink "!TARGET_DIR!\mvnw.cmd" "!TEMPLATE_BASE!\templates\maven-wrapper\mvnw.cmd"
if !errorlevel! neq 0 set "ERROR_LEVEL=!errorlevel!"

REM 创建.mvn目录（如果不存在）
if not exist "!TARGET_DIR!\.mvn" mkdir "!TARGET_DIR!\.mvn"
if not exist "!TARGET_DIR!\.mvn\wrapper" mkdir "!TARGET_DIR!\.mvn\wrapper"

REM maven-wrapper.properties
if exist "!TARGET_DIR!\.mvn\wrapper\maven-wrapper.properties" (
    echo 📦 备份现有文件: !TARGET_DIR!\.mvn\wrapper\maven-wrapper.properties
    move "!TARGET_DIR!\.mvn\wrapper\maven-wrapper.properties" "!TARGET_DIR!\.mvn\wrapper\maven-wrapper.properties.backup"
)
mklink "!TARGET_DIR!\.mvn\wrapper\maven-wrapper.properties" "!TEMPLATE_BASE!\templates\maven-wrapper\.mvn\wrapper\maven-wrapper.properties"
if !errorlevel! neq 0 set "ERROR_LEVEL=!errorlevel!"

REM 创建启动脚本符号链接
echo.
echo 🚀 部署启动脚本符号链接...

REM start.bat
if exist "!TARGET_DIR!\start.bat" (
    echo 📦 备份现有文件: !TARGET_DIR!\start.bat
    move "!TARGET_DIR!\start.bat" "!TARGET_DIR!\start.bat.backup"
)
mklink "!TARGET_DIR!\start.bat" "!TEMPLATE_BASE!\templates\startup-scripts\start-template.bat"
if !errorlevel! neq 0 set "ERROR_LEVEL=!errorlevel!"

REM start.sh
if exist "!TARGET_DIR!\start.sh" (
    echo 📦 备份现有文件: !TARGET_DIR!\start.sh
    move "!TARGET_DIR!\start.sh" "!TARGET_DIR!\start.sh.backup"
)
mklink "!TARGET_DIR!\start.sh" "!TEMPLATE_BASE!\templates\startup-scripts\start-template.sh"
if !errorlevel! neq 0 set "ERROR_LEVEL=!errorlevel!"

echo.
if !ERROR_LEVEL! equ 0 (
    echo ✅ 符号链接部署完成!
    echo.
    echo 📋 部署的文件:
    echo    - mvnw -^> 集中模板文件
    echo    - mvnw.cmd -^> 集中模板文件
    echo    - .mvn\wrapper\maven-wrapper.properties -^> 集中模板文件
    echo    - start.bat -^> 集中模板文件
    echo    - start.sh -^> 集中模板文件
    echo.
    echo 💡 提示: 修改集中模板文件后，所有服务将自动使用最新版本
    echo 💡 提示: 如需恢复备份文件，请查看对应的 .backup 文件
) else (
    echo ⚠️ 符号链接部署过程中出现错误，请检查权限
)

pause