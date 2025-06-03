package com.example.util;

import org.apache.ibatis.type.MappedTypes;
import com.example.config.ListTypeHandler;
import com.example.model.PurchaseNotice;
import java.util.List;

@MappedTypes(List.class)
public class PurchaseNoticesTypeHandler extends ListTypeHandler<PurchaseNotice> {

    public PurchaseNoticesTypeHandler() {
        super(PurchaseNotice.class);
    }
}