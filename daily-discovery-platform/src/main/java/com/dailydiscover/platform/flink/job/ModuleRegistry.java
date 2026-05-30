package com.dailydiscover.platform.flink.job;

import com.dailydiscover.platform.flink.module.FlinkModule;
import com.dailydiscover.platform.flink.module.HotTopicModule;

import java.util.List;

public class ModuleRegistry {

    private static final List<FlinkModule> ALL_MODULES = List.of(
            new HotTopicModule()
    );

    public static List<FlinkModule> all() {
        return ALL_MODULES;
    }
}