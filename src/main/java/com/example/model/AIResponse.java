package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AIResponse {
    @JsonProperty("data")
    private AIContent[] data;

    public AIContent[] getData() {
        return data;
    }

    public void setData(AIContent[] data) {
        this.data = data;
    }
} 