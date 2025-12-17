@echo off
setlocal

REM 自动检测项目类型
set "PROJECT_TYPE=user"
if exist "pom.xml" (
    findstr /C:"daily-discover-common" "pom.xml" >nul
    if not errorlevel 1 (
        set "PROJECT_TYPE=common"
    ) else (
        findstr /C:"daily-discover-user" "pom.xml" >nul
        if not errorlevel 1 (
            set "PROJECT_TYPE=user"
        )
    )
)

REM 设置服务名称
if "%PROJECT_TYPE%"=="common" (
    set "SERVICE_NAME=每日发现通用模块"
) else (
    set "SERVICE_NAME=每日发现用户服务"
)

REM 检查是否使用独立模式
set "STANDALONE_MODE="
if "%1"=="standalone" (
    if "%PROJECT_TYPE%"=="common" (
        set "STANDALONE_MODE=-Pstandalone"
    ) else (
        echo 警告: 独立模式仅适用于common模块
    )
)

echo 🚀 启动 %SERVICE_NAME%...
echo.

echo ☕ 检查 Java 环境...
java -version
echo.

echo 📦 编译项目...
call mvnw.cmd clean compile %STANDALONE_MODE%
echo.

echo 🎯 启动服务...
call mvnw.cmd spring-boot:run %STANDALONE_MODE%

pause