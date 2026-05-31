package com.dailydiscover.platform.flink.job;

import com.dailydiscover.platform.config.HotTopicConfig;
import com.dailydiscover.platform.flink.udf.HotScoreFunction;
import com.dailydiscover.platform.flink.udf.PositiveRateFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaToWideJob {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaToWideJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("启动 Kafka → 宽表 统一作业");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = (Configuration) env.getConfiguration();
        conf.set(PipelineOptions.NAME, HotTopicConfig.JOB_NAME_WIDE);
        env.enableCheckpointing(60000);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        tEnv.createTemporarySystemFunction("hot_score", HotScoreFunction.class);
        tEnv.createTemporarySystemFunction("positive_rate", PositiveRateFunction.class);

        ModuleRegistry.all().forEach(module -> {
            LOG.info("注册宽表模块: {}", module.moduleName());
            module.wideSourceTableDdls().forEach(tEnv::executeSql);
            tEnv.executeSql(module.wideSinkTableDdl());
            tEnv.executeSql(module.wideInsertSql());
        });

        LOG.info("Kafka → 宽表 已提交");
    }
}