#!/bin/bash

# MySQL连接参数（根据之前的配置）
MYSQL_HOST="localhost"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASSWORD="DailyDiscover@2024!"
MYSQL_DATABASE="daily_discover"

echo "正在执行第一个SQL文件：001_create_product_core_tables.sql"
echo "========================================================="

# 执行SQL文件
mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} < "daily-discover-product/src/main/resources/db/migration/001_create_product_core_tables.sql"

# 检查执行结果
if [ $? -eq 0 ]; then
    echo "✅ SQL文件执行成功！"
    
    # 验证表是否创建成功
    echo "验证创建的表："
    mysql -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u ${MYSQL_USER} -p${MYSQL_PASSWORD} ${MYSQL_DATABASE} -e "
        SHOW TABLES;
        
        echo -e '\n各表结构：'
        SHOW CREATE TABLE products;
        SHOW CREATE TABLE product_categories;
        SHOW CREATE TABLE product_details;
        SHOW CREATE TABLE product_skus;
        SHOW CREATE TABLE product_sku_specs;
        SHOW CREATE TABLE product_sku_spec_options;
    "
else
    echo "❌ SQL文件执行失败！错误码：$?"
fi

echo "========================================================="
echo "第一个SQL文件执行完成"