 # 大型电商平台数据库架构优化方案

## 一、整体架构设计

### 1. 多层架构

```
应用层 → 缓存层 → 数据库层 → 归档层
```

- **应用层**：业务逻辑处理，实现本地缓存
- **缓存层**：分布式缓存集群，减轻数据库压力
- **数据库层**：主从分离、分库分表
- **归档层**：历史数据存储，支持离线分析

### 2. 数据分层

```
热数据：最近3个月，主数据库
温数据：3-18个月，中间层存储
冷数据：18个月以上，归档存储
```

## 二、表分区策略

### 1. 时间范围分区

适用表：
- `shop_transaction` (交易流水表)
- `shop_settlement` (结算记录表)
- `financial_statement` (财务对账单表)

```sql
-- 交易流水表按月分区示例
ALTER TABLE shop_transaction
PARTITION BY RANGE (TO_DAYS(create_time)) (
    PARTITION p202301 VALUES LESS THAN (TO_DAYS('2023-02-01')),
    PARTITION p202302 VALUES LESS THAN (TO_DAYS('2023-03-01')),
    PARTITION p202303 VALUES LESS THAN (TO_DAYS('2023-04-01')),
    -- 更多分区...
    PARTITION pmax VALUES LESS THAN MAXVALUE
);

-- 自动添加下个月分区的存储过程
DELIMITER //
CREATE PROCEDURE add_next_month_partition()
BEGIN
    DECLARE next_month_start DATE;
    DECLARE next_month_end DATE;
    DECLARE partition_name VARCHAR(10);
    
    -- 计算下个月的开始和结束日期
    SET next_month_start = DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-01'), INTERVAL 1 MONTH);
    SET next_month_end = DATE_ADD(next_month_start, INTERVAL 1 MONTH);
    SET partition_name = CONCAT('p', DATE_FORMAT(next_month_start, '%Y%m'));
    
    -- 添加新分区
    SET @sql = CONCAT('ALTER TABLE shop_transaction ADD PARTITION (PARTITION ', 
                      partition_name, 
                      ' VALUES LESS THAN (TO_DAYS(''', 
                      next_month_end, 
                      ''')))');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END //
DELIMITER ;

-- 创建定时事件每月执行
CREATE EVENT add_partition_monthly
ON SCHEDULE EVERY 1 MONTH
DO CALL add_next_month_partition();
```

### 2. 哈希分区

适用表：
- `shop_account` (店铺账户表)
- `shop_withdraw` (店铺提现申请表)

```sql
-- 店铺账户表按shop_id哈希分区示例
ALTER TABLE shop_account
PARTITION BY HASH(shop_id)
PARTITIONS 16;
```

### 3. 列表分区

适用表：
- `commission_rule` (平台佣金规则表)

```sql
-- 佣金规则表按rule_type列表分区示例
ALTER TABLE commission_rule
PARTITION BY LIST(rule_type) (
    PARTITION p_category VALUES IN (1),  -- 分类佣金
    PARTITION p_shop VALUES IN (2),      -- 店铺佣金
    PARTITION p_promotion VALUES IN (3)  -- 促销佣金
);
```

## 三、索引优化要点

### 1. 覆盖索引设计

```sql
-- 交易流水表高频查询场景的覆盖索引
CREATE INDEX idx_shop_trans_time ON shop_transaction(shop_id, transaction_type, create_time, amount, balance);

-- 结算记录表的覆盖索引
CREATE INDEX idx_shop_settle_status ON shop_settlement(shop_id, status, settlement_start_time, settlement_end_time, total_amount);
```

### 2. 复合索引顺序优化

```sql
-- 优化前：低效索引
CREATE INDEX idx_settlement_shop_time ON shop_settlement(settlement_start_time, shop_id);

-- 优化后：高效索引（遵循最左匹配原则）
CREATE INDEX idx_shop_settlement_time ON shop_settlement(shop_id, settlement_start_time, settlement_end_time);
```

### 3. 索引维护策略

```sql
-- 定期检查未使用索引
SELECT * FROM sys.schema_unused_indexes;

-- 定期检查索引碎片化
SELECT table_name, index_name, fragmentation
FROM sys.dm_db_index_physical_stats
WHERE fragmentation > 30;

-- 重建碎片化严重的索引
ALTER INDEX idx_shop_settlement_time ON shop_settlement REBUILD;
```

## 四、SQL优化要点

### 1. 避免全表扫描

```sql
-- 优化前：全表扫描
SELECT * FROM shop_transaction 
WHERE create_time >= '2023-01-01' AND create_time < '2023-02-01';

-- 优化后：使用分区裁剪
SELECT * FROM shop_transaction PARTITION(p202301)
WHERE shop_id = 12345;
```

### 2. 使用覆盖索引

```sql
-- 优化前：回表查询
SELECT transaction_no, amount, balance, create_time 
FROM shop_transaction 
WHERE shop_id = 12345 AND transaction_type = 1;

-- 优化后：覆盖索引查询
SELECT transaction_no, amount, balance, create_time 
FROM shop_transaction FORCE INDEX(idx_shop_trans_time)
WHERE shop_id = 12345 AND transaction_type = 1;
```

### 3. 避免不必要的排序

```sql
-- 优化前：额外排序操作
SELECT * FROM shop_settlement 
WHERE shop_id = 12345 
ORDER BY settlement_start_time DESC 
LIMIT 10;

-- 优化后：利用索引顺序避免排序
SELECT * FROM shop_settlement FORCE INDEX(idx_shop_settlement_time)
WHERE shop_id = 12345 
ORDER BY settlement_start_time DESC 
LIMIT 10;
```

### 4. 使用合适的锁策略

```sql
-- 优化前：可能导致锁等待
UPDATE shop_account SET balance = balance + 100 WHERE shop_id = 12345;

-- 优化后：使用行级锁并设置超时
SET innodb_lock_wait_timeout = 5;
SELECT balance FROM shop_account WHERE shop_id = 12345 FOR UPDATE;
-- 业务逻辑处理
UPDATE shop_account SET balance = [新值] WHERE shop_id = 12345;
```

### 5. 批量操作优化

```sql
-- 优化前：多次单条插入
INSERT INTO shop_transaction (shop_id, transaction_no, ...) VALUES (1, 'TX001', ...);
INSERT INTO shop_transaction (shop_id, transaction_no, ...) VALUES (2, 'TX002', ...);

-- 优化后：批量插入
INSERT INTO shop_transaction (shop_id, transaction_no, ...) 
VALUES 
(1, 'TX001', ...),
(2, 'TX002', ...),
(3, 'TX003', ...);
```

## 五、读写分离实现

### 1. 主从复制配置

```ini
# 主库配置 (my.cnf)
server-id = 1
log-bin = mysql-bin
binlog-format = ROW
sync-binlog = 1
innodb_flush_log_at_trx_commit = 1

# 从库配置 (my.cnf)
server-id = 2
relay-log = slave-relay-bin
read_only = 1
```

### 2. 读写分离路由

```java
// 伪代码示例：根据SQL类型路由到主库或从库
public Connection getConnection(String sql) {
    if (isWriteOperation(sql)) {
        return getMasterConnection();
    } else {
        return getSlaveConnection();
    }
}

private boolean isWriteOperation(String sql) {
    String upperSql = sql.trim().toUpperCase();
    return upperSql.startsWith("INSERT") || 
           upperSql.startsWith("UPDATE") || 
           upperSql.startsWith("DELETE");
}
```

## 六、分库分表设计

### 1. 垂直分库

```
财务核心库：shop_account, shop_transaction
结算业务库：shop_settlement, shop_settlement_detail
发票税务库：shop_invoice, financial_statement
```

### 2. 水平分表

```sql
-- 按shop_id范围分表示例
-- shop_transaction_0 存储shop_id 0-9999的数据
CREATE TABLE shop_transaction_0 LIKE shop_transaction;

-- shop_transaction_1 存储shop_id 10000-19999的数据
CREATE TABLE shop_transaction_1 LIKE shop_transaction;

-- 以此类推...
```

### 3. 分布式ID生成

```sql
-- 创建全局序列表
CREATE TABLE global_sequence (
    name VARCHAR(50) NOT NULL PRIMARY KEY,
    current_value BIGINT UNSIGNED NOT NULL,
    increment INT UNSIGNED NOT NULL DEFAULT 1
);

-- 获取下一个序列值的存储过程
DELIMITER //
CREATE PROCEDURE get_next_sequence(IN seq_name VARCHAR(50), OUT next_val BIGINT)
BEGIN
    UPDATE global_sequence 
    SET current_value = current_value + increment 
    WHERE name = seq_name;
    
    SELECT current_value INTO next_val
    FROM global_sequence
    WHERE name = seq_name;
END //
DELIMITER ;
```

## 七、数据归档策略

### 1. 归档表设计

```sql
-- 交易流水归档表
CREATE TABLE shop_transaction_archive (
    id BIGINT NOT NULL,
    transaction_no VARCHAR(32) NOT NULL,
    shop_id BIGINT NOT NULL,
    -- 其他字段与原表相同
    archive_time DATETIME NOT NULL COMMENT '归档时间',
    PRIMARY KEY (id),
    INDEX idx_transaction_no (transaction_no),
    INDEX idx_shop_create_time (shop_id, create_time)
) ENGINE=InnoDB COMMENT='交易流水归档表';
```

### 2. 归档存储过程

```sql
-- 交易流水归档存储过程
DELIMITER //
CREATE PROCEDURE archive_transactions(IN archive_before_date DATE)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE batch_size INT DEFAULT 5000;
    DECLARE last_id BIGINT DEFAULT 0;
    
    -- 开始归档
    WHILE NOT done DO
        -- 插入归档表
        INSERT INTO shop_transaction_archive
        SELECT *, NOW() as archive_time
        FROM shop_transaction
        WHERE create_time < archive_before_date
          AND id > last_id
        ORDER BY id
        LIMIT batch_size;
        
        -- 检查是否完成
        IF ROW_COUNT() < batch_size THEN
            SET done = TRUE;
        ELSE
            -- 记录最后处理的ID
            SELECT MAX(id) INTO last_id FROM shop_transaction_archive;
            
            -- 删除已归档数据
            DELETE FROM shop_transaction
            WHERE create_time < archive_before_date
              AND id <= last_id;
            
            -- 避免长时间占用资源，短暂休息
            DO SLEEP(0.1);
        END IF;
    END WHILE;
END //
DELIMITER ;
```

## 八、缓存策略

### 1. 多级缓存设计

```
L1: 应用本地缓存 (Caffeine/Guava)
L2: 分布式缓存 (Redis)
L3: 数据库
```

### 2. 缓存键设计

```
# 账户余额缓存键
account:balance:{shop_id}

# 结算记录缓存键
settlement:{settlement_id}

# 佣金规则缓存键
commission:rule:{rule_type}:{target_id}
```

### 3. 缓存一致性保障

```java
// 伪代码：更新账户余额的缓存一致性保障
public void updateAccountBalance(long shopId, BigDecimal newBalance) {
    // 1. 删除缓存
    cache.delete("account:balance:" + shopId);
    
    try {
        // 2. 更新数据库
        accountDao.updateBalance(shopId, newBalance);
        
        // 3. 短暂休眠，确保其他线程的读请求已经开始
        Thread.sleep(10);
        
        // 4. 再次删除缓存
        cache.delete("account:balance:" + shopId);
    } catch (Exception e) {
        // 异常处理
        log.error("Failed to update balance", e);
    }
}
```

## 九、性能监控与优化

### 1. 慢查询监控

```sql
-- 开启慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;  -- 设置阈值为1秒

-- 分析慢查询
SHOW VARIABLES LIKE '%slow_query%';
SHOW VARIABLES LIKE 'long_query_time';
```

### 2. 执行计划分析

```sql
-- 使用EXPLAIN分析查询计划
EXPLAIN SELECT * FROM shop_transaction 
WHERE shop_id = 12345 
AND create_time BETWEEN '2023-01-01' AND '2023-01-31';
```

### 3. 性能基准测试

```bash
# 使用sysbench进行基准测试
sysbench --mysql-host=localhost --mysql-user=root --mysql-password=password \
  --mysql-db=ecommerce --table-size=1000000 \
  --tables=10 --threads=16 --time=60 \
  /usr/share/sysbench/oltp_read_write.lua run
```

## 十、高可用设计

### 1. 主从切换

```bash
# 使用MHA实现自动主从切换
masterha_manager --conf=/etc/masterha/app1.cnf
```

### 2. 数据备份策略

```bash
# 每日全量备份
mysqldump --single-transaction --master-data=2 -A > full_backup_$(date +%Y%m%d).sql

# 实时增量备份 (使用binlog)
mysqlbinlog --read-from-remote-server --host=master --raw \
  --stop-never mysql-bin.000001
```

### 3. 多数据中心部署

```
主数据中心 → 同步复制 → 灾备数据中心
```

## 总结

大型电商平台数据库架构需要综合考虑性能、可扩展性、可用性和数据一致性。通过表分区、索引优化、SQL优化、读写分离、分库分表、数据归档和多级缓存等策略，可以构建一个能够支撑百万级商家、千万级交易的高性能数据库系统。