package com.example.util;

import org.apache.ibatis.type.MappedTypes;

import com.example.common.util.ListTypeHandler;
import com.example.model.Specification;

import java.util.List;

@MappedTypes(List.class)
public class SpecificationsTypeHandler extends ListTypeHandler<Specification> {

    public SpecificationsTypeHandler() {
        super(Specification.class);
    }
}