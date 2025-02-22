package com.example.dto;

import java.util.List;

import com.example.model.Event;
import com.example.model.Product;

import lombok.Data;

@Data
public class SearchResults {
    private List<Event> events;
    private List<Product> recommendations;

    public SearchResults(List<Event> events, List<Product> recommendations) {
        this.events = events;
        this.recommendations = recommendations;
    }

    // Getters and Setters
}
