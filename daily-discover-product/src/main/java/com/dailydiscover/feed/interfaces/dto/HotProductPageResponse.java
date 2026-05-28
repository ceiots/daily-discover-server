package com.dailydiscover.feed.interfaces.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HotProductPageResponse {

    private List<HotProductCardResponse> items;
    private boolean hasMore;
    private long total;
    private int page;
    private int size;
}