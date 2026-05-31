# Daily Discovery Platform

每日发现数据处理平台，通过 Flink 将 MySQL 领域表（products / sales_stats / review_stats）实时同步到读模型宽表 hot_topic_display_read_model，API 层单表查询即可获取首页所需全部字段。

---

## 环境准备

### 前置条件

- Flink 1.20+ 已安装并启动
- Kafka 已安装并启动
- MySQL 已开启 binlog（log_bin=ON, binlog_format=ROW）
- MySQL 用户有 REPLICATION CLIENT + REPLICATION SLAVE 权限

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

共 2 个作业，需要分别提交：

```bash
# 作业1：MySQL CDC → Kafka
flink run -d -m localhost:8081 \
  -c com.dailydiscover.platform.flink.job.CdcToKafkaJob \
  /path/to/daily-discovery-platform.jar

# 作业2：Kafka → 宽表
flink run -d -m localhost:8081 \
  -c com.dailydiscover.platform.flink.job.KafkaToWideJob \
  /path/to/daily-discovery-platform.jar
```

也可通过 PipelineApplication 一键提交：

```bash
flink run -d -m localhost:8081 \
  -c com.dailydiscover.platform.PipelineApplication \
  /path/to/daily-discovery-platform.jar hotopic:submit-all
```

### 查看作业状态

```bash
flink list -m localhost:8081
```

成功时应看到两个 Running 作业：
- HotTopic-CDC-to-Kafka
- HotTopic-Kafka-to-Wide

---

## 监控

### Flink Web UI

访问 `http://localhost:8081`，查看作业状态、延迟、反压。

### Flink 日志

```bash
tail -100 /path/to/flink/log/flink-*.log
```

### Kafka 消费堆积

```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group flink-hot-topic-group --describe
```

如果 LAG 持续增长，说明作业处理速度跟不上。

### 验证宽表数据

```bash
mysql -u root -p -h <host> -e \
  "SELECT COUNT(*), MIN(hot_score), MAX(hot_score) \
   FROM daily_discover.hot_topic_display_read_model;"
```

---


## 如何添加新业务模块

1. 在 `flink/module/` 下新建 `XxxModule.java`，继承 `FlinkModule`
2. 在 `ModuleRegistry.java` 的列表加一行

```java
private static final List<FlinkModule> ALL_MODULES = List.of(
    new HotTopicModule(),
    new XxxModule()  // ← 新增一行即可
);
```

无需修改 CdcToKafkaJob、KafkaToWideJob、Flink 配置。

---

## 工程结构

```
src/main/java/com/dailydiscover/platform/
  ├── PipelineApplication.java       ← 一键提交入口
  ├── config/
  │   ├── PipelineConfig.java        ← MySQL/Kafka 连接配置
  │   └── HotTopicConfig.java        ← 今日热点业务配置
  └── flink/
      ├── job/
      │   ├── CdcToKafkaJob.java     ← 作业1：CDC 采集
      │   ├── KafkaToWideJob.java    ← 作业2：宽表计算
      │   └── ModuleRegistry.java    ← 模块注册表
      ├── module/
      │   ├── FlinkModule.java       ← 模块抽象类
      │   └── HotTopicModule.java    ← 今日热点模块
      └── udf/
          ├── HotScoreFunction.java  ← 热度分 UDF
          ├── PositiveRateFunction.java ← 好评率 UDF
          └── HotTagFunction.java    ← 热度标签 UDF
```