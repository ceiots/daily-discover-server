# Daily Discovery Platform

每日发现数据处理平台 — 基于 Apache Flink 的实时数据管道。

**解决的问题**：「今日热点」首页的展示字段（标题、主图、价格、销量、好评率、热度标签等）分散在 3 张领域表中，每次查询都要 JOIN。本平台通过 Flink CDC 实时同步到一张读模型宽表，API 层单表查询即可。

**与上下游的关系：**
```
daily-discover-product     →  领域模型 + 宽表 DDL
daily-discovery-platform   →  本工程：Flink CDC → Kafka → Flink JOIN → UPSERT 宽表
daily-discover-ui          →  API 读取宽表，无需 JOIN
```

---

## 数据流

```
MySQL 领域表（3 张）
products / product_sales_stats / review_stats
         │ binlog
         ▼
作业1: HotTopicCdcToKafkaJob（Flink CDC 采集）
  3 个 mysql-cdc Source（scan.startup.mode=initial，首次全量→实时增量）
  3 个 Kafka Sink（JSON 格式）
         │
    ┌────┼────┐
    ▼    ▼    ▼
cdc_products / cdc_product_sales_stats / cdc_review_stats（3 个 Kafka Topic）
         │
    └────┼────┘
         ▼ 消费 Kafka
作业2: HotTopicKafkaToWideJob（Flink 流计算）
  3 个 Kafka Source → LEFT JOIN → UDF 计算（hot_score / positive_rate / hot_tag）
         │ UPSERT，每 100 条或每 5s 批量写入
         ▼
hot_topic_display_read_model（今日热点读模型宽表 / MySQL）
  单表查询：SELECT * FROM hot_topic_display_read_model WHERE is_trending=1 ORDER BY hot_score DESC LIMIT 20
```

**两个作业拆分的原因：** CDC 采集（IO 密集）和 JOIN 计算（CPU 密集）可独立重启，职责清晰。Kafka 做缓存层，任一作业重启不丢数据。


---

## 环境准备

### MySQL 开启 binlog

```ini
[mysqld]
log_bin = ON
binlog_format = ROW
binlog_row_image = FULL
server_id = 1

# 验证：SHOW VARIABLES LIKE 'log_bin'; 应为 ON
```

CDC 首次启动自动全量快照，之后增量读 binlog，无需手动初始化。

### Kafka 创建 Topic

```bash
# 开发环境 replication-factor=1 即可
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-topics.sh --create --topic cdc_products --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-topics.sh --create --topic cdc_product_sales_stats --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-topics.sh --create --topic cdc_review_stats --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```
# 查看 Kafka Topic
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

---

## 源表 → 宽表字段映射

宽表 DDL 定义见 [hot_topic_display_read_model](file:///d:/daily-discover/daily-discover-server/daily-discover-product/src/main/resources/db/migration/015_create_hot_topic_display_read_model.sql)。

## 配置说明

所有连接信息在 [PipelineConfig.java](file:///d:/daily-discover/daily-discover-server/daily-discovery-platform/src/main/java/com/dailydiscover/platform/config/PipelineConfig.java) 中集中管理，修改只需改这一个文件。

---

## 快速开始

### 打包

```bash
cd daily-discovery-platform
mvn clean package -DskipTests
```

### 提交作业

```bash
# 提交今日热点所有作业
/home/sshuser/flink-1.20.4/bin/flink run -d -m localhost:8090 -c com.dailydiscover.platform.PipelineApplication /home/sshuser/daily-discovery-platform.jar hotopic:submit-all

# 也可分开提交
# hotopic:submit-cdc（仅作业1）或 hotopic:submit-wide（仅作业2）

# 查看作业
/home/sshuser/flink-1.20.4/bin/flink list -m localhost:8090
```

---

## 监控与运维

### 常用命令

```bash
# 查看 Flink Web UI：http://localhost:8090

# 查看 Kafka 消费堆积
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group flink-hot-topic-group --describe

# 验证宽表数据
mysql -u root -p -e "SELECT COUNT(*), MIN(hot_score), MAX(hot_score) FROM daily_discover.hot_topic_display_read_model;"

# 重启流程：先 cancel 作业2，再 cancel 作业1，然后重新提交
```
