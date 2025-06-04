package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class UserInterestRequest {
    private Long userId;
    private List<Long> tagIds;
}