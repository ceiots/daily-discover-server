# Daily Discovery Platform

每日发现数据处理平台。通过 Flink CDC 将 MySQL 领域表的变更实时采集到 Kafka，然后由计算层消费 Kafka 写入读模型宽表，API 层单表查询即可获取首页所需全部字段。

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
    │
    ▼
计算层（二选一，推荐 Spring Consumer）
  ├── Flink 计算（KafkaToWideJob）：Flink SQL LEFT JOIN + UDF 写宽表
  └── Spring Consumer（推荐）：@KafkaListener 消费 Kafka，LEFT JOIN + 计算写宽表
    │
    ▼
读模型层（MySQL 宽表）
  hot_topic_display_read_model
  API 单表查询，无需 JOIN
```

### 为什么保留 CDC → Kafka

CDC 层是所有业务模块的共享数据管道。今日热点、限时机会、猜你想搜都需要消费 products / sales_stats 的变更数据。通过 CDC 一次性采集到 Kafka，各模块独立消费，避免每个模块各自轮询数据库。

### 计算层推荐用 Spring Consumer

今日热点的计算逻辑（LEFT JOIN + 四则运算）用 Spring Boot @KafkaListener 即可完成，比维护 Flink 集群更轻量。如果后续需要实时聚合计算（如推荐、实时热门排行），再引入 Flink 作为计算引擎。

---

## 环境准备

### 前置条件

- Flink 1.20+ 已安装并启动（仅 CDC 采集层需要）
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

## 启动 CDC 采集层

CDC 层使用 Flink CDC Connector 读取 binlog 写入 Kafka：

```bash
./flink run -d -m localhost:8090 \
  -c com.dailydiscover.platform.flink.job.CdcToKafkaJob \
  /home/sshuser/daily-discovery-platform.jar
```

### 验证 CDC 数据

```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic cdc_products --from-beginning --max-messages 3
```

能看到 JSON 格式的 binlog 变更数据，说明 CDC 层正常工作。

---

## 启动计算层

### 方式一（推荐）：Spring Consumer

在 `daily-discover-product` 或独立的 Spring Boot 服务中，添加 `@KafkaListener` 消费 Kafka 写入宽表。参考实现：

```java
@Component
public class WideTableConsumer {

    @KafkaListener(topics = "cdc_products")
    public void onProduct(Product p) {
        wideTableRepo.upsert(p);
    }

    @KafkaListener(topics = "cdc_product_sales_stats")
    public void onSales(SalesStats s) {
        WideRow row = wideTableRepo.findById(s.getProductId());
        row.setSalesCount(s.getSalesCount());
        row.setHotScore(calcHotScore(row));
        wideTableRepo.save(row);
    }
}
```

### 方式二：Flink 计算

如果已有 Flink 集群，可直接提交 KafkaToWideJob：

```bash
./flink run -d -m localhost:8090 \
  -c com.dailydiscover.platform.flink.job.KafkaToWideJob \
  /home/sshuser/daily-discovery-platform.jar
```

### 查看作业状态

```bash
flink list -m localhost:8081
```

### 验证宽表数据

```bash
mysql -u root -p -h <host> -e \
  "SELECT COUNT(*), MIN(hot_score), MAX(hot_score) \
   FROM daily_discover.hot_topic_display_read_model;"
```

---

## 监控

### Flink 日志

```bash
tail -100 flink/log/flink-*.log
```

### Kafka 消费堆积

```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --group flink-hot-topic-group --describe
```

如果 LAG 持续增长，说明计算层处理速度跟不上。

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
      │   ├── CdcToKafkaJob.java     ← CDC 采集作业
      │   ├── KafkaToWideJob.java    ← Flink 计算作业（可选）
      │   └── ModuleRegistry.java    ← 模块注册表
      ├── module/
      │   ├── FlinkModule.java       ← 模块抽象类
      │   └── HotTopicModule.java    ← 今日热点模块
      └── udf/
          ├── HotScoreFunction.java  ← 热度分 UDF
          ├── PositiveRateFunction.java ← 好评率 UDF
          └── HotTagFunction.java    ← 热度标签 UDF
```