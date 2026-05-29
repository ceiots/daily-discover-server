package com.dailydiscover.platform.config;

public class PipelineConfig {

    public static final String MYSQL_HOST = "100.76.38.80";
    public static final int MYSQL_PORT = 3306;
    public static final String MYSQL_USERNAME = "root";
    public static final String MYSQL_PASSWORD = "123456";
    public static final String MYSQL_DATABASE = "daily_discover";

    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";

    public static final String FLINK_CHECKPOINT_DIR = "file:///data/flink/checkpoints";

    public static final String JDBC_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + MYSQL_DATABASE;
}