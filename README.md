# 运行命令
java -jar daily-discover-0.0.1-SNAPSHOT.jar

# 启停nginx
.\nginx.exe -s reload
或者
.\nginx.exe -s stop
start nginx.exe

#  登录数据库
mysql -u root -p

# 授权语句
CREATE USER 'demo0000'@'localhost' IDENTIFIED BY 'demo0000';
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,ALTER,DROP ON demo.* TO 'demo0000'@'localhost';

# 刷新权限语句
FLUSH PRIVILEGES;

# 数据库创建语句
CREATE DATABASE demo;

# 数据库使用语句
USE demo;

