package com.dailydiscover.feed.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotProductQuery {

    private String tab;

    private int page;

    private int size;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }

    public static HotProductQuery of(String tab, int page, int size) {
        return HotProductQuery.builder()
                .tab(tab != null ? tab : "trending")
                .page(Math.max(page, 1))
                .size(Math.min(Math.max(size, 1), 50))
                .build();
    }
}