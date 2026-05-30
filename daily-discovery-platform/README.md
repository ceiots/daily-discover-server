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

## 架构设计

采用**统一作业 + 业务模块**架构：所有业务共用 2 个 Flink 作业，模块列表集中管理在 `ModuleRegistry`。

```
PipelineApplication.java
      │
      ├── CdcToKafkaJob.java          ← 作业1：CDC 采集（统一，1 slot）
      │       └── ModuleRegistry.all()
      │               ├── HotTopicModule.java   ← 今日热点
      │               ├── FlashSaleModule.java  ← 限时机会（未来）
      │               └── TomorrowModule.java   ← 明日提醒（未来）
      │
      └── KafkaToWideJob.java         ← 作业2：宽表计算（统一，1 slot）
              └── ModuleRegistry.all()（同一个列表）
```

**加新模块只改一个地方**：`ModuleRegistry.java` 的列表加一行即可，两个 Job 自动生效。

**每个业务模块 = 一个文件**：一个模块同时包含 CDC 和宽表的 DDL / SQL，继承 `FlinkModule` 按需覆盖方法。

**作业与 slot 说明：**
- **Job**：一个完整的数据处理管道（DAG）
- **Slot**：TaskManager 里的计算单元，每个 Job 设 `parallelism=1`，只占 1 个 slot
- 当前 `taskmanager.numberOfTaskSlots=2`，2 个 Job 刚好占满
- 如需扩容，改配置为 4 或加 TaskManager 即可，代码不变

---

## 数据流

```
MySQL 领域表（3 张）
products / product_sales_stats / review_stats
         │ binlog
         ▼
作业1: CdcToKafkaJob（Flink CDC 采集）
  3 个 mysql-cdc Source（scan.startup.mode=initial，首次全量→实时增量）
  3 个 Kafka Sink（JSON 格式）
         │
    ┌────┼────┐
    ▼    ▼    ▼
cdc_products / cdc_product_sales_stats / cdc_review_stats（3 个 Kafka Topic）
         │
    └────┼────┘
         ▼ 消费 Kafka
作业2: KafkaToWideJob（Flink 流计算）
  3 个 Kafka Source → LEFT JOIN → UDF 计算（hot_score / positive_rate / hot_tag）
         │ UPSERT，每 100 条或每 5s 批量写入
         ▼
hot_topic_display_read_model（今日热点读模型宽表 / MySQL）
  单表查询：SELECT * FROM hot_topic_display_read_model WHERE is_trending=1 ORDER BY hot_score DESC LIMIT 20
```

两个作业拆分：CDC 采集（IO 密集）和 JOIN 计算（CPU 密集）可独立重启。Kafka 做缓存层，任一作业重启不丢数据。

---


## 如何添加新业务模块

1. **新建 `XxxModule.java`** 继承 `FlinkModule`，放在 `flink/module/` 下
2. **在 `ModuleRegistry.java` 的列表加一行**

```java
// ModuleRegistry.java — 唯一要改的地方
private static final List<FlinkModule> ALL_MODULES = List.of(
        new HotTopicModule(),
        new FlashSaleModule()    // ← 新增一行即可，两个 Job 自动感知
);
```

**不需要改**：CdcToKafkaJob、KafkaToWideJob、Flink 配置、slot 数、提交命令、PipelineApplication。

---

## 环境准备

### MySQL 开启 binlog

```ini
[mysqld]
log_bin = ON
binlog_format = ROW
binlog_row_image = FULL
server_id = 1
```

### Kafka 创建 Topic

```bash
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-topics.sh --create --topic cdc_products --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-topics.sh --create --topic cdc_product_sales_stats --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-topics.sh --create --topic cdc_review_stats --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

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
/home/sshuser/flink-1.20.4/bin/flink run -d -m localhost:8090 \
  -c com.dailydiscover.platform.PipelineApplication \
  /home/sshuser/daily-discovery-platform.jar hotopic:submit-all

# 查看作业
/home/sshuser/flink-1.20.4/bin/flink list -m localhost:8090
```

---

## 监控与运维

```bash
# Flink Web UI: http://localhost:8090

# 查看 Kafka 消费堆积
/home/sshuser/kafka_2.13-4.2.0/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group flink-hot-topic-group --describe

# 验证宽表数据
mysql -u root -p -e "SELECT COUNT(*), MIN(hot_score), MAX(hot_score) FROM daily_discover.hot_topic_display_read_model;"

# 重启流程：先 cancel 作业2，再 cancel 作业1，然后重新提交
```