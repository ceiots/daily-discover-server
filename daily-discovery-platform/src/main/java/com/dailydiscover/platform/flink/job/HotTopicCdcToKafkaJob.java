package com.dailydiscover.platform.flink.job;

import com.dailydiscover.platform.config.PipelineConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotTopicCdcToKafkaJob {

    private static final Logger LOG = LoggerFactory.getLogger(HotTopicCdcToKafkaJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("启动今日热点作业1: CDC → Kafka");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(60000);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        String database = PipelineConfig.MYSQL_DATABASE;
        String host = PipelineConfig.MYSQL_HOST;
        int port = PipelineConfig.MYSQL_PORT;
        String username = PipelineConfig.MYSQL_USERNAME;
        String password = PipelineConfig.MYSQL_PASSWORD;
        String kafkaServers = PipelineConfig.KAFKA_BOOTSTRAP_SERVERS;

        String cdcProducts = String.format("""
                CREATE TABLE cdc_products (
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
                    'connector' = 'mysql-cdc',
                    'hostname' = '%s',
                    'port' = '%d',
                    'username' = '%s',
                    'password' = '%s',
                    'database-name' = '%s',
                    'table-name' = 'products',
                    'scan.startup.mode' = 'initial',
                    'server-id' = '5401'
                )
                """, host, port, username, password, database);

        String cdcSalesStats = String.format("""
                CREATE TABLE cdc_sales_stats (
                    product_id          BIGINT,
                    sales_count         INT,
                    view_count          INT,
                    favorite_count      INT,
                    sales_growth_rate   DECIMAL(5,2),
                    time_granularity    STRING,
                    stat_date           DATE,
                    PRIMARY KEY (product_id, time_granularity, stat_date) NOT ENFORCED
                ) WITH (
                    'connector' = 'mysql-cdc',
                    'hostname' = '%s',
                    'port' = '%d',
                    'username' = '%s',
                    'password' = '%s',
                    'database-name' = '%s',
                    'table-name' = 'product_sales_stats',
                    'scan.startup.mode' = 'initial',
                    'server-id' = '5402'
                )
                """, host, port, username, password, database);

        String cdcReviewStats = String.format("""
                CREATE TABLE cdc_review_stats (
                    product_id      BIGINT,
                    total_reviews   INT,
                    average_rating  DECIMAL(3,2),
                    PRIMARY KEY (product_id) NOT ENFORCED
                ) WITH (
                    'connector' = 'mysql-cdc',
                    'hostname' = '%s',
                    'port' = '%d',
                    'username' = '%s',
                    'password' = '%s',
                    'database-name' = '%s',
                    'table-name' = 'review_stats',
                    'scan.startup.mode' = 'initial',
                    'server-id' = '5403'
                )
                """, host, port, username, password, database);

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
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers);

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
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers);

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
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers);

        String insertSql = """
                INSERT INTO kafka_products
                SELECT id, title, main_image_url, min_price, goods_slogan, category_id, brand, status, created_at
                FROM cdc_products
                """;

        String insertSalesSql = """
                INSERT INTO kafka_sales_stats
                SELECT product_id, sales_count, view_count, favorite_count, sales_growth_rate, time_granularity, stat_date
                FROM cdc_sales_stats
                """;

        String insertReviewSql = """
                INSERT INTO kafka_review_stats
                SELECT product_id, total_reviews, average_rating
                FROM cdc_review_stats
                """;

        tEnv.executeSql(cdcProducts);
        tEnv.executeSql(cdcSalesStats);
        tEnv.executeSql(cdcReviewStats);
        tEnv.executeSql(kafkaProducts);
        tEnv.executeSql(kafkaSalesStats);
        tEnv.executeSql(kafkaReviewStats);

        tEnv.createStatementSet()
                .addInsertSql(insertSql)
                .addInsertSql(insertSalesSql)
                .addInsertSql(insertReviewSql)
                .execute();

        LOG.info("今日热点作业1: CDC → Kafka 已提交");
    }
}