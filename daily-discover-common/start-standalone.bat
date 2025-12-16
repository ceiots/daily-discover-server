@echo off
echo 启动 daily-discover-common 独立服务模式...

REM 使用standalone profile启动
mvn spring-boot:run -Pstandalone

pause