package com.example.util;

import java.util.List;

import org.apache.ibatis.type.MappedTypes;

import com.example.model.PurchaseNotice;

@MappedTypes(List.class)
public class PurchaseNoticesTypeHandler extends ListTypeHandler<PurchaseNotice> {
    public PurchaseNoticesTypeHandler() {
        super(PurchaseNotice.class);
    }
} 