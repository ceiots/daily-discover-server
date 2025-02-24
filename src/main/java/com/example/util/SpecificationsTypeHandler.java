package com.example.util;

import org.apache.ibatis.type.MappedTypes;

import com.example.model.Specification;

import java.util.List;

@MappedTypes(List.class)
public class SpecificationsTypeHandler extends JsonTypeHandler<List<Specification>> {

    public SpecificationsTypeHandler() {
        super(List.class, Specification.class);
    }
}