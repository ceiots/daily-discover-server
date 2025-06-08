package com.example.model;

import java.util.List;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Specification {
    private String name;
    private List<String> values;
    private Boolean isSalesAttribute; // Determines if this specification generates SKUs
}