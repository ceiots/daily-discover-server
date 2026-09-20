package com.dailydiscover.behavior.domain;

import java.util.Set;

/**
 * 行为类型（01 Data-Model-MVP.md 第 8 节，与 Flow-MVP 统一）
 */
public final class BehaviorType {
    public static final String IMPRESSION = "IMPRESSION";
    public static final String DETAIL_VIEW = "DETAIL_VIEW";
    public static final String INTERESTED = "INTERESTED";
    public static final String NOT_INTERESTED = "NOT_INTERESTED";
    public static final String SKIP = "SKIP";
    public static final String ACTION_CLICK = "ACTION_CLICK";

    public static final Set<String> ALL = Set.of(
            IMPRESSION, DETAIL_VIEW, INTERESTED, NOT_INTERESTED, SKIP, ACTION_CLICK);

    private BehaviorType() {
    }
}
