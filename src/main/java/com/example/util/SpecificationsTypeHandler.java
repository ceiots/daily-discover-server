package com.example.util;

import java.util.List;

import org.apache.ibatis.type.MappedTypes;

import com.example.model.Specification;

@MappedTypes(List.class)
public class SpecificationsTypeHandler extends ListTypeHandler<Specification> {
    public SpecificationsTypeHandler() {
        super(Specification.class);
    }
} 