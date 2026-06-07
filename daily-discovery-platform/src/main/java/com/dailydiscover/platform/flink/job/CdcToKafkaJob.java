package com.dailydiscover.platform.flink.job;

import com.dailydiscover.platform.config.HotTopicConfig;
import com.dailydiscover.platform.config.PipelineConfig;

import java.util.List;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdcToKafkaJob {

    private static final Logger LOG = LoggerFactory.getLogger(CdcToKafkaJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("启动 CDC → Kafka 统一作业");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = (Configuration) env.getConfiguration();
        conf.set(PipelineOptions.NAME, HotTopicConfig.JOB_NAME_CDC);
        env.enableCheckpointing(60000);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        CdcToKafkaJob job = new CdcToKafkaJob();
        
        job.sourceTableDdls().forEach(tEnv::executeSql);
        job.sinkTableDdls().forEach(tEnv::executeSql);

        StatementSet ss = tEnv.createStatementSet();
        job.cdcInsertSqls().forEach(ss::addInsertSql);
        ss.execute();

        LOG.info("CDC → Kafka 已提交");
    }

    public List<String> sourceTableDdls() {
        String database = PipelineConfig.MYSQL_DATABASE;
        String host = PipelineConfig.MYSQL_HOST;
        int port = PipelineConfig.MYSQL_PORT;
        String username = PipelineConfig.MYSQL_USERNAME;
        String password = PipelineConfig.MYSQL_PASSWORD;

        String cdcProducts = String.format("""
                CREATE TABLE IF NOT EXISTS cdc_products (
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
                CREATE TABLE IF NOT EXISTS cdc_sales_stats (
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
                CREATE TABLE IF NOT EXISTS cdc_review_stats (
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

        String cdcSceneProductRelation = String.format("""
                CREATE TABLE IF NOT EXISTS cdc_scene_product_relation (
                    scene_id            BIGINT,
                    product_id          BIGINT,
                    product_reason      STRING,
                    display_order       INT,
                    is_active           INT,
                    PRIMARY KEY (scene_id, product_id) NOT ENFORCED
                ) WITH (
                    'connector' = 'mysql-cdc',
                    'hostname' = '%s',
                    'port' = '%d',
                    'username' = '%s',
                    'password' = '%s',
                    'database-name' = '%s',
                    'table-name' = 'scene_product_relation',
                    'scan.startup.mode' = 'initial',
                    'server-id' = '5404'
                )
                """, host, port, username, password, database);

        return List.of(cdcProducts, cdcSalesStats, cdcReviewStats, cdcSceneProductRelation);
    }

    public List<String> sinkTableDdls() {
        String kafkaServers = PipelineConfig.KAFKA_BOOTSTRAP_SERVERS;

        String kafkaProducts = String.format("""
                CREATE TABLE IF NOT EXISTS kafka_products (
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
                    'value.format' = 'json',
                    'sink.transactional-id-prefix' = 'products'
                )
                """, kafkaServers);

        String kafkaSalesStats = String.format("""
                CREATE TABLE IF NOT EXISTS kafka_sales_stats (
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
                    'value.format' = 'json',
                    'sink.transactional-id-prefix' = 'sales_stats'
                )
                """, kafkaServers);

        String kafkaReviewStats = String.format("""
                CREATE TABLE IF NOT EXISTS kafka_review_stats (
                    product_id      BIGINT,
                    total_reviews   INT,
                    average_rating  DECIMAL(3,2),
                    PRIMARY KEY (product_id) NOT ENFORCED
                ) WITH (
                    'connector' = 'upsert-kafka',
                    'topic' = 'cdc_review_stats',
                    'properties.bootstrap.servers' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json',
                    'sink.transactional-id-prefix' = 'review_stats'
                )
                """, kafkaServers);

        String kafkaSceneProduct = String.format("""
                CREATE TABLE IF NOT EXISTS kafka_scene_product_relation (
                    scene_id            BIGINT,
                    product_id          BIGINT,
                    product_reason      STRING,
                    display_order       INT,
                    is_active           INT,
                    PRIMARY KEY (scene_id, product_id) NOT ENFORCED
                ) WITH (
                    'connector' = 'upsert-kafka',
                    'topic' = 'cdc_scene_product_relation',
                    'properties.bootstrap.servers' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json',
                    'sink.transactional-id-prefix' = 'scene_product'
                )
                """, kafkaServers);

        return List.of(kafkaProducts, kafkaSalesStats, kafkaReviewStats, kafkaSceneProduct);
    }

    public List<String> cdcInsertSqls() {
        String insertProducts = """
                INSERT INTO kafka_products
                SELECT id, title, main_image_url, min_price, goods_slogan, category_id, brand, status, created_at
                FROM cdc_products
                """;

        String insertSales = """
                INSERT INTO kafka_sales_stats
                SELECT product_id, sales_count, view_count, favorite_count, sales_growth_rate, time_granularity, stat_date
                FROM cdc_sales_stats
                """;

        String insertReviews = """
                INSERT INTO kafka_review_stats
                SELECT product_id, total_reviews, average_rating
                FROM cdc_review_stats
                """;

        String insertSceneProduct = """
                INSERT INTO kafka_scene_product_relation
                SELECT scene_id, product_id, product_reason, display_order, is_active
                FROM cdc_scene_product_relation
                """;

        return List.of(insertProducts, insertSales, insertReviews, insertSceneProduct);
    }



}