# Daily Discovery Platform

每日发现数据处理平台。通过 Flink CDC 将 MySQL 领域表的变更实时采集到 Kafka，再由 Flink 计算层消费 Kafka 写入读模型宽表，API 层单表查询即可获取首页所需全部字段。所有实时数据处理统一在 Flink 中完成，保持架构一致性。

---

## 架构

```
MySQL 领域表（products / sales_stats / review_stats）
    │ binlog
    ▼
CDC 采集层（Flink CDC Connector，运行在 Flink 集群）
  输出：Kafka Topic
    │
    ▼
消息缓冲层（Kafka）
  Topic：cdc_products / cdc_product_sales_stats / cdc_review_stats
  作用：解耦采集和计算，CDC 作业重启不影响计算层
    │
    ▼
计算层（Flink SQL）
  LEFT JOIN 3 个 Kafka 流 → UDF 计算（hot_score / positive_rate / hot_tag）
  输出：UPSERT 写入宽表
    │
    ▼
读模型层（MySQL 宽表）
  hot_topic_display_read_model
  API 单表查询，无需 JOIN
```

---

## 环境准备

### 前置条件

- Flink 1.20+ 已安装并启动
- Kafka 已安装并启动
- MySQL 已开启 binlog（log_bin=ON, binlog_format=ROW）
- MySQL 用户有 REPLICATION CLIENT + REPLICATION SLAVE 权限

### Flink lib 依赖

确保 `flink-1.20.4/lib/` 目录包含以下 JAR 包：

```
flink-sql-connector-kafka-4.0.0-2.0.jar    ← Kafka connector
flink-sql-connector-mysql-cdc-3.2.1.jar     ← MySQL CDC
flink-connector-jdbc-3.3.0-1.20.jar         ← JDBC sink
mysql-connector-j-8.3.0.jar                 ← MySQL 驱动
```

### 确认 Kafka Topic

```bash
kafka-topics.sh --bootstrap-server localhost:9092 --list
```

如果缺少以下 topic，手动创建：

```bash
kafka-topics.sh --create --topic cdc_products \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic cdc_product_sales_stats \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic cdc_review_stats \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic cdc_scene_product_relation \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

---

## 打包

```bash
cd daily-discovery-platform
mvn clean package -DskipTests
```

生成 jar 包：`target/daily-discovery-platform.jar`

---

## 提交作业

需要分别提交 2 个作业：

```bash
# 作业1：MySQL CDC → Kafka
./flink run -d -m localhost:8090 \
  -c com.dailydiscover.platform.flink.job.CdcToKafkaJob \
  /home/sshuser/daily-discovery-platform.jar

# 作业2：Kafka → 宽表
./flink run -d -m localhost:8090 \
  -c com.dailydiscover.platform.flink.job.KafkaToWideJob \
  /home/sshuser/daily-discovery-platform.jar
```


### 验证 CDC 数据

```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic cdc_products --from-beginning --max-messages 3
```

能看到 JSON 格式的 binlog 变更数据，说明 CDC 层正常工作。

### 验证宽表数据

```bash
mysql -u root -p -h 100.76.38.80 -e \
  "SELECT COUNT(*), MIN(hot_score), MAX(hot_score) \
   FROM daily_discover.hot_topic_display_read_model;"
```

---

## 监控

### Flink 日志

```bash
tail -100 ~/flink-1.20.4/log/flink-*.log
```

### Kafka 消费堆积

```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group flink-hot-topic-group --describe
```

如果 LAG 持续增长，说明作业处理速度跟不上，需检查并行度或资源。

---

## 如何添加新业务模块

1. 在 `flink/module/` 下新建 `XxxModule.java`，继承 `FlinkModule`
2. 在 `ModuleRegistry.java` 的列表加一行

```java
private static final List<FlinkModule> ALL_MODULES = List.of(
    new HotTopicModule(),
    new XxxModule()
);
```

无需修改 CdcToKafkaJob 和 KafkaToWideJob 的代码。
