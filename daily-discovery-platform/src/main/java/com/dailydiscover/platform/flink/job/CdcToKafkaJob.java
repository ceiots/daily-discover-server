package com.dailydiscover.platform.flink.job;

import com.dailydiscover.platform.config.HotTopicConfig;
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

        ModuleRegistry.all().forEach(module -> {
            LOG.info("注册 CDC 模块: {}", module.moduleName());
            module.sourceTableDdls().forEach(tEnv::executeSql);
            module.sinkTableDdls().forEach(tEnv::executeSql);
        });

        StatementSet ss = tEnv.createStatementSet();
        ModuleRegistry.all().forEach(module ->
                module.cdcInsertSqls().forEach(ss::addInsertSql)
        );
        ss.execute();

        LOG.info("CDC → Kafka 已提交");
    }
}