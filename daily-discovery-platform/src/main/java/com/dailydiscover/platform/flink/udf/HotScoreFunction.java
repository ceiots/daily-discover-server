package com.dailydiscover.platform.flink.udf;

import org.apache.flink.table.functions.ScalarFunction;

public class HotScoreFunction extends ScalarFunction {

    public Double eval(Integer salesCount, Integer viewCount, Integer favoriteCount) {
        int s = salesCount != null ? salesCount : 0;
        int v = viewCount != null ? viewCount : 0;
        int f = favoriteCount != null ? favoriteCount : 0;
        return (double) (s * 10 + v * 2 + f * 5);
    }
}