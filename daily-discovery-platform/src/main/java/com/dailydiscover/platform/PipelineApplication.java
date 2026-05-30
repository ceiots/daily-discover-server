package com.dailydiscover.platform;

import com.dailydiscover.platform.flink.job.CdcToKafkaJob;
import com.dailydiscover.platform.flink.job.KafkaToWideJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineApplication {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineApplication.class);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            LOG.info("用法:");
            LOG.info("  Pipeline 路由管理:");
            LOG.info("    flink run -c com.dailydiscover.platform.PipelineApplication <jar> <module>:<command>");
            LOG.info("");
            LOG.info("  今日热点模块:");
            LOG.info("    flink run -c com.dailydiscover.platform.HotTopicPipelineApplication <jar> submit-all");
            LOG.info("    flink run -c com.dailydiscover.platform.HotTopicPipelineApplication <jar> submit-cdc");
            LOG.info("    flink run -c com.dailydiscover.platform.HotTopicPipelineApplication <jar> submit-wide");
            return;
        }

        String[] parts = args[0].split(":", 2);
        if (parts.length < 2) {
            LOG.warn("格式错误，请使用 <module>:<command>，例如 hotopic:submit-all");
            LOG.warn("或直接使用模块专属入口，例如 com.dailydiscover.platform.HotTopicPipelineApplication");
            return;
        }

        String module = parts[0];
        String command = parts[1];

        switch (module) {
            case "hotopic" -> {
                switch (command) {
                    case "submit-all" -> {
                        LOG.info("提交今日热点所有作业...");
                        try {
                            CdcToKafkaJob.main(new String[]{});
                        } catch (Exception e) {
                            LOG.error("作业1 CDC→Kafka 提交失败", e);
                        }
                        try {
                            KafkaToWideJob.main(new String[]{});
                        } catch (Exception e) {
                            LOG.error("作业2 Kafka→宽表 提交失败", e);
                        }
                    }
                    case "submit-cdc" -> {
                        LOG.info("提交今日热点作业1: CDC → Kafka...");
                        CdcToKafkaJob.main(new String[]{});
                    }
                    case "submit-wide" -> {
                        LOG.info("提交今日热点作业2: Kafka → 宽表...");
                        KafkaToWideJob.main(new String[]{});
                    }
                    default -> LOG.warn("今日热点未知命令: {}", command);
                }
            }
            default -> LOG.warn("未知模块: {}. 可用模块: hotopic", module);
        }
    }
}