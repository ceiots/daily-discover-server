package com.dailydiscover.platform.flink.udf;

import org.apache.flink.table.functions.ScalarFunction;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class HotTagFunction extends ScalarFunction {

    public String eval(Integer salesCount, LocalDateTime createdAt) {
        int sales = salesCount != null ? salesCount : 0;
        if (sales > 1000) {
            return "今日热门";
        }
        if (createdAt != null) {
            long daysBetween = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
            if (daysBetween <= 7) {
                return "新品首发";
            }
        }
        return "";
    }
}