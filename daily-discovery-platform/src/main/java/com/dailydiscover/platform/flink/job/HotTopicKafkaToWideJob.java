package com.dailydiscover.platform.flink.job;

import com.dailydiscover.platform.config.HotTopicConfig;
import com.dailydiscover.platform.config.PipelineConfig;
import com.dailydiscover.platform.flink.udf.HotScoreFunction;
import com.dailydiscover.platform.flink.udf.PositiveRateFunction;
import com.dailydiscover.platform.flink.udf.HotTagFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotTopicKafkaToWideJob {

    private static final Logger LOG = LoggerFactory.getLogger(HotTopicKafkaToWideJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("启动今日热点作业2: Kafka → 宽表");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(60000);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        tEnv.createTemporarySystemFunction("hot_score", HotScoreFunction.class);
        tEnv.createTemporarySystemFunction("positive_rate", PositiveRateFunction.class);
        tEnv.createTemporarySystemFunction("hot_tag", HotTagFunction.class);

        String kafkaServers = PipelineConfig.KAFKA_BOOTSTRAP_SERVERS;
        String groupId = HotTopicConfig.KAFKA_GROUP_ID;
        String jdbcUrl = PipelineConfig.JDBC_URL;
        String username = PipelineConfig.MYSQL_USERNAME;
        String password = PipelineConfig.MYSQL_PASSWORD;
        String wideTableName = HotTopicConfig.WIDE_TABLE_NAME;

        String kafkaProducts = String.format("""
                CREATE TABLE kafka_products (
                    id              BIGINT,
                    title           STRING,
                    main_image_url  STRING,
                    min_price       DECIMAL(10,2),
                    goods_slogan    STRING,
                    category_id     BIGINT,
                    brand           STRING,
                    status          INT,
                    created_at      TIMESTAMP(3),
                    PRIMARY KEY (id) NOT ENFORCED
                ) WITH (
                    'connector' = 'upsert-kafka',
                    'topic' = 'cdc_products',
                    'properties.bootstrap.servers' = '%s',
                    'properties.group.id' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers, groupId);

        String kafkaSalesStats = String.format("""
                CREATE TABLE kafka_sales_stats (
                    product_id          BIGINT,
                    sales_count         INT,
                    view_count          INT,
                    favorite_count      INT,
                    sales_growth_rate   DECIMAL(5,2),
                    time_granularity    STRING,
                    stat_date           DATE,
                    PRIMARY KEY (product_id, time_granularity, stat_date) NOT ENFORCED
                ) WITH (
                    'connector' = 'upsert-kafka',
                    'topic' = 'cdc_product_sales_stats',
                    'properties.bootstrap.servers' = '%s',
                    'properties.group.id' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers, groupId);

        String kafkaReviewStats = String.format("""
                CREATE TABLE kafka_review_stats (
                    product_id      BIGINT,
                    total_reviews   INT,
                    average_rating  DECIMAL(3,2),
                    PRIMARY KEY (product_id) NOT ENFORCED
                ) WITH (
                    'connector' = 'upsert-kafka',
                    'topic' = 'cdc_review_stats',
                    'properties.bootstrap.servers' = '%s',
                    'properties.group.id' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers, groupId);

        String wideTable = String.format("""
                CREATE TABLE %s (
                    product_id          BIGINT PRIMARY KEY,
                    title               STRING,
                    main_image_url      STRING,
                    min_price           DECIMAL(10,2),
                    goods_slogan        STRING,
                    recommendation_reason STRING,
                    category_id         BIGINT,
                    brand               STRING,
                    status              INT,
                    sales_count         INT,
                    view_count          INT,
                    favorite_count      INT,
                    sales_growth_rate   DECIMAL(5,2),
                    hot_score           DECIMAL(10,4),
                    average_rating      DECIMAL(3,2),
                    total_reviews       INT,
                    positive_rate       DECIMAL(5,2),
                    is_trending         INT,
                    is_new_arrival      INT,
                    hot_tag             STRING,
                    updated_at          TIMESTAMP(3)
                ) WITH (
                    'connector' = 'jdbc',
                    'url' = '%s',
                    'table-name' = '%s',
                    'username' = '%s',
                    'password' = '%s',
                    'driver' = 'com.mysql.cj.jdbc.Driver',
                    'sink.buffer-flush.max-rows' = '100',
                    'sink.buffer-flush.interval' = '5s'
                )
                """, wideTableName, jdbcUrl, wideTableName, username, password);

        String insertSql = """
                INSERT INTO %s
                SELECT
                    p.id,
                    p.title,
                    p.main_image_url,
                    p.min_price,
                    p.goods_slogan,
                    '好评如潮，用户反馈品质出众，值得入手' AS recommendation_reason,
                    p.category_id,
                    p.brand,
                    p.status,
                    COALESCE(s.sales_count, 0),
                    COALESCE(s.view_count, 0),
                    COALESCE(s.favorite_count, 0),
                    COALESCE(s.sales_growth_rate, 0),
                    hot_score(COALESCE(s.sales_count, 0), COALESCE(s.view_count, 0), COALESCE(s.favorite_count, 0)),
                    COALESCE(r.average_rating, 0),
                    COALESCE(r.total_reviews, 0),
                    positive_rate(COALESCE(r.average_rating, 0), COALESCE(r.total_reviews, 0)),
                    CASE WHEN COALESCE(s.sales_count, 0) > 1000 THEN 1 ELSE 0 END,
                    CASE WHEN p.created_at >= NOW() - INTERVAL '7' DAY THEN 1 ELSE 0 END,
                    hot_tag(COALESCE(s.sales_count, 0), p.created_at),
                    PROCTIME()
                FROM kafka_products p
                LEFT JOIN (
                    SELECT product_id,
                        MAX(sales_count) AS sales_count,
                        MAX(view_count) AS view_count,
                        MAX(favorite_count) AS favorite_count,
                        MAX(sales_growth_rate) AS sales_growth_rate
                    FROM kafka_sales_stats
                    WHERE time_granularity = 'daily'
                    GROUP BY product_id
                ) s ON p.id = s.product_id
                LEFT JOIN kafka_review_stats r ON p.id = r.product_id
                """;

        String insertSqlFinal = String.format(insertSql, wideTableName);

        tEnv.executeSql(kafkaProducts);
        tEnv.executeSql(kafkaSalesStats);
        tEnv.executeSql(kafkaReviewStats);
        tEnv.executeSql(wideTable);

        tEnv.executeSql(insertSqlFinal);

        LOG.info("今日热点作业2: Kafka → 宽表 已提交");
    }
}