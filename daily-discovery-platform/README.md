# Daily Discovery Platform

每日发现数据处理平台 — 基于 Apache Flink 的实时数据管道，将 MySQL 业务数据通过 CDC 采集、Kafka 中转，最终合并为商品展示读模型宽表，为「今日热点」等高频查询场景提供高性能读服务。

---

## 背景与问题

### 为什么需要这个平台？

今日热点首页需要展示：商品标题、主图、价格、销量、好评率、热度标签、跟风验证等字段。

但这些字段分散在 3 张领域表中：

| 数据 | 所在表 | 所属迁移文件 |
|------|--------|-------------|
| 商品基础信息（标题、主图、价格等） | `products` | 001_create_product_core_tables.sql |
| 销量/浏览/收藏统计 | `product_sales_stats` | 004_create_relationship_recommendation_tables.sql |
| 评价聚合（评分、评价数） | `review_stats` | 003_create_review_interaction_tables.sql |

如果每次查询都要 JOIN 三张表，面对高并发首页场景性能极差。

### 解决方案

构建一张**读模型宽表** [product_display_read_model](file:///d:/daily-discover/daily-discover-server/daily-discover-product/src/main/resources/db/migration/014_create_product_display_read_model.sql)，将三张表的数据预 JOIN + 预计算后写入一张表，API 层单表查询即可，无需任何 JOIN。

但宽表需要**实时同步**领域表的变更，因此有了本平台——通过 Flink CDC 流式管道，将 MySQL 的 binlog 变更实时同步到宽表。

---

## 架构设计

### 数据流

```
┌──────────────────────────────────────────────────────────────────────────┐
│  MySQL 领域表（3 张）                                                    │
│  products / product_sales_stats / review_stats                          │
│  binlog_format = ROW / binlog_row_image = FULL                          │
└────────────────────────────┬─────────────────────────────────────────────┘
                             │ binlog
                             ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  作业1: CdcToKafkaJob（Flink CDC 采集）                                  │
│                                                                          │
│  Flink SQL CDC Connector → 3 个 mysql-cdc Source 表                      │
│    scan.startup.mode = initial（首次全量 → 自动切换实时增量）              │
│    server-id = 5401 / 5402 / 5403（每个表独立 server-id 避免冲突）        │
│                                                                          │
│  3 个 Kafka Sink 表（JSON 格式）                                         │
│    cdc_products │ cdc_product_sales_stats │ cdc_review_stats             │
└────────────────────────────┬─────────────────────────────────────────────┘
                             │
            ┌────────────────┼────────────────┐
            ▼                ▼                ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  cdc_products    │ │ cdc_sales_stats  │ │  cdc_review_     │
│  Kafka Topic     │ │   Kafka Topic    │ │  stats Topic     │
│  partitions = 3  │ │  partitions = 3   │ │  partitions = 3   │
│  retention = 7d  │ │  retention = 7d  │ │  retention = 7d  │
└────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘
         │                  │                   │
         └────────┬─────────┴─────────┬─────────┘
                  │                   │
                  ▼ 消费 Kafka        ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  作业2: KafkaToWideTableJob（Flink 流计算）                              │
│                                                                          │
│  3 个 Kafka Source → 多流 LEFT JOIN                                     │
│    │                                                                     │
│    ├─ hot_score     = sales_count × 10 + view_count × 2 + favorite_count × 5│
│    ├─ positive_rate = ROUND(average_rating / 5 × 100, 2)                │
│    ├─ hot_tag       = 销量 > 1000 → "今日热门" / 7天内 → "新品首发"       │
│    └─ 3 个自定义 UDF 函数                                                │
│                                                                          │
│  UPSERT → product_display_read_model（JDBC Sink）                        │
│  批量写入：每 100 条或每 5s 刷新一次                                       │
└────────────────────────────┬─────────────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  product_display_read_model（读模型宽表 / MySQL）                        │
│                                                                          │
│  单表查询，无需 JOIN                                                     │
│  SELECT * FROM product_display_read_model                                │
│  WHERE is_trending = 1 ORDER BY hot_score DESC LIMIT 20                  │
│                                                                          │
│  索引优化：idx_hot_score / idx_is_trending / idx_category_hot            │
└──────────────────────────────────────────────────────────────────────────┘
```

### 为什么用两个作业 + Kafka 缓存层？

| 设计 | 原因 |
|------|------|
| **作业1 和 作业2 分离** | CDC 采集（IO 密集）和 JOIN 计算（CPU 密集）可独立重启维护，职责清晰 |
| **Kafka 缓存层** | 解耦采集和消费。CDC 作业重启时不丢失 binlog 位点，宽表作业重启时可从 Kafka 回溯重放 |
| **不直接用 CDC Source → JOIN → 宽表** | 单作业失败全部重来，无法独立维护和监控 |
| **Kafka 持久化** | 生产建议保留 7 天，支持下游多消费（宽表、ES、推荐系统等） |

> 注：架构图中每个 Kafka Topic 设 3 个 partition，在单节点开发环境下不会带来并行收益，但预留了未来扩容能力——当 Kafka 扩展为多节点集群时无需重建 Topic。

## 前置条件

| 依赖 | 版本要求 | 说明 |
|------|---------|------|
| Java | 17+ | Flink 1.20 要求 Java 17 |
| Apache Flink | 1.20.4 | 运行两个 Flink 作业（单机 Standalone 模式即可） |
| Apache Kafka | 4.2+（KRaft 模式） | 4.x 无需 Zookeeper，单节点即可运行 |
| MySQL | 8.0+ | 需开启 binlog |
| Maven | 3.8+ | 编译打包 |

---

## 环境准备

### MySQL binlog 配置

MySQL 必须开启 binlog 才能使用 CDC。确认以下参数：

```ini
# my.cnf 配置
[mysqld]
log_bin = ON
binlog_format = ROW
binlog_row_image = FULL
server_id = 1
expire_logs_days = 7

# 验证配置
mysql> SHOW VARIABLES LIKE 'log_bin';
mysql> SHOW VARIABLES LIKE 'binlog_format';
```

CDC 会在首次启动时自动快照全量数据（`scan.startup.mode=initial`），之后增量读取 binlog，无需手动初始化。

### Kafka Topic 创建

作业1 需要以下 Topic，需提前创建：

```bash
# 使用 Kafka 4.x KRaft 模式
/opt/kafka/bin/kafka-topics.sh --create \
  --topic cdc_products \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

/opt/kafka/bin/kafka-topics.sh --create \
  --topic cdc_product_sales_stats \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

/opt/kafka/bin/kafka-topics.sh --create \
  --topic cdc_review_stats \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
```

生产环境多节点部署时建议 `replication-factor >= 2` 保证可用性，单节点开发环境 `replication-factor=1` 即可。

---

## 源表 → 宽表字段映射

宽表 DDL 定义见 [014_create_product_display_read_model.sql](file:///d:/daily-discover/daily-discover-server/daily-discover-product/src/main/resources/db/migration/014_create_product_display_read_model.sql)。

### 字段映射明细

| 宽表字段 | 源表 | 源字段 | 计算逻辑 |
|---------|------|--------|----------|
| `product_id` | products | id | 直接映射 |
| `title` | products | title | 直接映射 |
| `main_image_url` | products | main_image_url | 直接映射 |
| `min_price` | products | min_price | 直接映射 |
| `goods_slogan` | products | goods_slogan | 直接映射 |
| `category_id` | products | category_id | 直接映射 |
| `brand` | products | brand | 直接映射 |
| `status` | products | status | 直接映射 |
| `sales_count` | product_sales_stats | sales_count | 取 `time_granularity='daily'` 的最新值，`COALESCE` 兜底 0 |
| `view_count` | product_sales_stats | view_count | 同上 |
| `favorite_count` | product_sales_stats | favorite_count | 同上 |
| `sales_growth_rate` | product_sales_stats | sales_growth_rate | 同上 |
| `average_rating` | review_stats | average_rating | 直接映射，`COALESCE` 兜底 0 |
| `total_reviews` | review_stats | total_reviews | 直接映射 |
| `hot_score` | — | — | `sales_count × 10 + view_count × 2 + favorite_count × 5` |
| `positive_rate` | — | average_rating | `ROUND(average_rating / 5 × 100, 2)`，评价数为 0 时返回 0 |
| `is_trending` | — | sales_count | `sales_count > 1000 → 1`，否则 0 |
| `is_new_arrival` | products | created_at | 创建 7 天内 → 1，否则 0 |
| `hot_tag` | — | sales_count + created_at | 销量 > 1000 → "今日热门" / 7 天内 → "新品首发" |
| `recommendation_reason` | — | — | 当前为固定文案，可接入 AI 生成 |

---

## 配置说明

所有连接信息在 [PipelineConfig.java](file:///d:/daily-discover/daily-discover-server/daily-discovery-platform/src/main/java/com/dailydiscover/platform/config/PipelineConfig.java) 中集中管理，修改配置**只需改这一个文件**。

---

## 快速开始

### 1. 打包

```bash
cd daily-discovery-platform
mvn clean package -DskipTests
```

打包后在 `target/daily-discovery-platform.jar` 生成 Fat JAR（含所有依赖），可直接提交到 Flink 运行（单机 Standalone 或集群均可）。

### 2. 提交流程

使用 Flink CLI 提交作业到 Flink 集群（单机 Standalone 即可）：

```bash
# 一键提交所有流程
/path/to/flink/bin/flink run -d -m localhost:8081 \
  -c com.dailydiscover.platform.PipelineApplication \
  target/daily-discovery-platform.jar submit-all

# 分别提交（可独立维护和重启）
/path/to/flink/bin/flink run -d -m localhost:8081 \
  -c com.dailydiscover.platform.PipelineApplication \
  target/daily-discovery-platform.jar submit-cdc

/path/to/flink/bin/flink run -d -m localhost:8081 \
  -c com.dailydiscover.platform.PipelineApplication \
  target/daily-discovery-platform.jar submit-wide

# 查看作业状态
/path/to/flink/bin/flink list -m localhost:8081
```

### Windows 环境

```powershell
& "C:\flink-1.20.4\bin\flink.bat" run -d -m localhost:8081 `
  -c com.dailydiscover.platform.PipelineApplication `
  target/daily-discovery-platform.jar submit-all

& "C:\flink-1.20.4\bin\flink.bat" list -m localhost:8081
```

---

## 监控与运维

### 常用命令

```bash
# Flink 作业列表
/path/to/flink/bin/flink list -m localhost:8081

# Web UI: http://localhost:8081

# Kafka 消费堆积
/path/to/kafka/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group flink-wide-table-group --describe

# 手动触发 Savepoint（升级前必做）
/path/to/flink/bin/flink stop --savepointPath file:///data/flink/savepoints <job-id>

# 从 Savepoint 恢复
/path/to/flink/bin/flink run -s file:///data/flink/savepoints/savepoint-xxx \
  -d -m localhost:8081 \
  -c com.dailydiscover.platform.PipelineApplication \
  target/daily-discovery-platform.jar submit-cdc

# 重启流程：先 cancel 作业2，再 cancel 作业1，然后重新提交即可
#（Kafka 会保留位点，不会丢数据）
```

### 常见问题排查

| 问题 | 排查方法 |
|------|---------|
| 宽表数据为空 | 确认作业1 的 `scan.startup.mode=initial` 是否已跑完全量同步；检查 Kafka Topic 是否有消息：`kafka-console-consumer.sh --topic cdc_products --from-beginning --bootstrap-server localhost:9092` |
| Kafka 消费堆积 | `kafka-consumer-groups.sh --describe` 查看 LAG 列，过大说明作业2 处理跟不上，可适当增大 Flink 并行度（不超过 CPU 核心数） |
| CDC 延迟高 | Flink Web UI → CdcToKafkaJob → currentFetchEventTimeLag，正常 < 5s |
| MySQL 连接失败 | 确认 binlog 已开启：`SHOW VARIABLES LIKE 'log_bin'` 必须为 ON |
| JDBC Sink 写入慢 | 调整 `sink.buffer-flush.max-rows` 和 `sink.buffer-flush.interval` 参数 |
| Flink 作业频繁重启 | 检查 Checkpoint 目录是否可写，确认 `state.backend` 配置正确 |

---

## 与上下游工程的关系

```
daily-discover-product                ← 领域模型 + 数据宽表 DDL
  └── db/migration/
      └── 014_create_product_display_read_model.sql  ← 宽表定义

daily-discovery-platform               ← 本工程：数据管道
  └── Flink CDC → Kafka → Flink JOIN → UPSERT 宽表

daily-discover-ui                     ← 前端消费宽表
  └── API 读取 product_display_read_model 无需 JOIN
```

---