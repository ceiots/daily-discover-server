package com.dailydiscover.platform.config;

public class HotTopicConfig {

    public static final String WIDE_TABLE_NAME = "hot_topic_display_read_model";

    public static final String KAFKA_GROUP_ID = "flink-hot-topic-group";

    public static final String JOB_NAME_CDC = "HotTopic-CDC-to-Kafka";
    public static final String JOB_NAME_WIDE = "HotTopic-Kafka-to-Wide";
}