package com.dailydiscover.platform;

import com.dailydiscover.platform.flink.job.CdcToKafkaJob;
import com.dailydiscover.platform.flink.job.KafkaToWideTableJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineApplication {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineApplication.class);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            LOG.info("用法:");
            LOG.info("  flink run -c com.dailydiscover.platform.PipelineApplication <jar> submit-all");
            LOG.info("  flink run -c com.dailydiscover.platform.PipelineApplication <jar> submit-cdc");
            LOG.info("  flink run -c com.dailydiscover.platform.PipelineApplication <jar> submit-wide");
            return;
        }

        switch (args[0]) {
            case "submit-all" -> {
                LOG.info("提交所有作业...");
                CdcToKafkaJob.main(new String[]{});
                KafkaToWideTableJob.main(new String[]{});
                LOG.info("全部提交完成。查看状态: flink list -m localhost:8081");
            }
            case "submit-cdc" -> {
                LOG.info("提交作业1: CDC → Kafka...");
                CdcToKafkaJob.main(new String[]{});
            }
            case "submit-wide" -> {
                LOG.info("提交作业2: Kafka → 宽表...");
                KafkaToWideTableJob.main(new String[]{});
            }
            default -> LOG.warn("未知命令: {}", args[0]);
        }
    }
}
