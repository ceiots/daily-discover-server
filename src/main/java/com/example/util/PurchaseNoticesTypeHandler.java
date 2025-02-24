package com.example.util;

import org.apache.ibatis.type.MappedTypes;
import com.example.model.PurchaseNotice;
import java.util.List;

@MappedTypes(List.class)
public class PurchaseNoticesTypeHandler extends JsonTypeHandler<List<PurchaseNotice>> {

    public PurchaseNoticesTypeHandler() {
        super(List.class, PurchaseNotice.class);
    }
}