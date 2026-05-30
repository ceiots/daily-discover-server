package com.dailydiscover.platform.flink.module;

import java.util.List;

public abstract class FlinkModule {

    public abstract String moduleName();

    public List<String> sourceTableDdls() {
        return List.of();
    }

    public List<String> sinkTableDdls() {
        return List.of();
    }

    public List<String> cdcInsertSqls() {
        return List.of();
    }

    public List<String> wideSourceTableDdls() {
        return List.of();
    }

    public String wideSinkTableDdl() {
        return "";
    }

    public String wideInsertSql() {
        return "";
    }
}