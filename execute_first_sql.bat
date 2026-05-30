@echo off
chcp 65001 >nul
echo 正在执行第一个SQL文件：001_create_product_core_tables.sql
echo =========================================================

REM MySQL连接参数（根据之前的配置）
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=DailyDiscover@2024!
set MYSQL_DATABASE=daily_discover

REM 执行SQL文件
mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < "daily-discover-product\src\main\resources\db\migration\001_create_product_core_tables.sql"

REM 检查执行结果
if %ERRORLEVEL% EQU 0 (
    echo ✅ SQL文件执行成功！
    echo.
    echo 验证创建的表：
    mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% -e "SHOW TABLES;"
    
    echo.
    echo 查看数据插入情况：
    mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% -e "SELECT COUNT(*) as products_count FROM products;"
    mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% -e "SELECT COUNT(*) as categories_count FROM product_categories;"
    mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% -e "SELECT COUNT(*) as product_details_count FROM product_details;"
    mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% -e "SELECT COUNT(*) as product_skus_count FROM product_skus;"
) else (
    echo ❌ SQL文件执行失败！错误码：%ERRORLEVEL%
)

echo.
echo =========================================================
echo 第一个SQL文件执行完成
pause