package com.dailydiscover.platform.flink.udf;

import org.apache.flink.table.functions.ScalarFunction;

public class PositiveRateFunction extends ScalarFunction {

    public Double eval(Double averageRating, Integer totalReviews) {
        int reviews = totalReviews != null ? totalReviews : 0;
        double rating = averageRating != null ? averageRating : 0.0;
        if (reviews > 0) {
            return Math.round((rating / 5.0) * 10000.0) / 100.0;
        }
        return 0.0;
    }
}