@echo off
chcp 65001 >nul
setlocal

REM Daily Discover Common Module Startup Script
set "SERVICE_NAME=Daily Discover Common Module"

REM Check if using standalone mode
set "STANDALONE_MODE="
if "%1"=="standalone" (
    set "STANDALONE_MODE=-Pstandalone"
    echo Using standalone mode
)

echo Starting %SERVICE_NAME%...
echo.

echo Checking Java environment...
java -version
echo.

echo Compiling project...
call mvnw.cmd clean compile %STANDALONE_MODE%
echo.

echo Starting service...
call mvnw.cmd spring-boot:run %STANDALONE_MODE%

pause