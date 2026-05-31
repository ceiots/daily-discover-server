package com.dailydiscover.platform.flink.module;

import com.dailydiscover.platform.config.HotTopicConfig;
import com.dailydiscover.platform.config.PipelineConfig;

import java.util.List;

public class HotTopicModule extends FlinkModule {

    @Override
    public String moduleName() {
        return "hotopic";
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public List<String> wideSourceTableDdls() {
        String kafkaServers = PipelineConfig.KAFKA_BOOTSTRAP_SERVERS;
        String groupId = HotTopicConfig.KAFKA_GROUP_ID;

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
                    'properties.group.id' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers, groupId);

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
                    'properties.group.id' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers, groupId);

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
                    'properties.group.id' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers, groupId);

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
                    'properties.group.id' = '%s',
                    'key.format' = 'json',
                    'value.format' = 'json'
                )
                """, kafkaServers, groupId);

        return List.of(kafkaProducts, kafkaSalesStats, kafkaReviewStats, kafkaSceneProduct);
    }

    @Override
    public String wideSinkTableDdl() {
        String jdbcUrl = PipelineConfig.JDBC_URL;
        String username = PipelineConfig.MYSQL_USERNAME;
        String password = PipelineConfig.MYSQL_PASSWORD;
        String wideTableName = HotTopicConfig.WIDE_TABLE_NAME;

        return String.format("""
                CREATE TABLE IF NOT EXISTS %s (
                    product_id          BIGINT,
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
                    updated_at          TIMESTAMP(3),
                    PRIMARY KEY (product_id) NOT ENFORCED
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
    }

    @Override
    public String wideInsertSql() {
        String wideTableName = HotTopicConfig.WIDE_TABLE_NAME;

        return String.format("""
                INSERT INTO %s
                SELECT
                    p.id,
                    p.title,
                    p.main_image_url,
                    p.min_price,
                    p.goods_slogan,
                    COALESCE(spr.product_reason, '') AS recommendation_reason,
                    p.category_id,
                    p.brand,
                    p.status,
                    COALESCE(s.sales_count, 0) AS sales_count,
                    COALESCE(s.view_count, 0) AS view_count,
                    COALESCE(s.favorite_count, 0) AS favorite_count,
                    COALESCE(s.sales_growth_rate, 0) AS sales_growth_rate,
                    hot_score(COALESCE(s.sales_count, 0), COALESCE(s.view_count, 0), COALESCE(s.favorite_count, 0)) AS hot_score,
                    COALESCE(r.average_rating, 0) AS average_rating,
                    COALESCE(r.total_reviews, 0) AS total_reviews,
                    positive_rate(COALESCE(r.average_rating, 0), COALESCE(r.total_reviews, 0)) AS positive_rate,
                    CASE WHEN COALESCE(s.sales_count, 0) > 1000 THEN 1 ELSE 0 END AS is_trending,
                    CASE WHEN p.created_at >= NOW() - INTERVAL '7' DAY THEN 1 ELSE 0 END AS is_new_arrival,
                    PROCTIME() AS updated_at
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
                LEFT JOIN (
                    SELECT product_id, product_reason
                    FROM kafka_scene_product_relation
                    WHERE is_active = 1
                ) spr ON p.id = spr.product_id
                """, wideTableName);
    }
}