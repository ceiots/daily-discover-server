@echo off
chcp 65001 >nul
setlocal

REM Daily Discover User Service Startup Script
set "SERVICE_NAME=Daily Discover User Service"

echo Starting %SERVICE_NAME%...
echo.

echo Checking Java environment...
java -version
echo.

echo Compiling project...
call mvnw.cmd clean compile
echo.

echo Starting service...
call mvnw.cmd spring-boot:run

pause