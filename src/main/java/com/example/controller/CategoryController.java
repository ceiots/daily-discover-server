// CategoryController.java
package com.example.controller;

import com.example.mapper.CategoryMapper;
import com.example.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/daily-discover/categories")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("")
    public List<Category> getAllCategories() {
        return categoryMapper.getAllCategories();
    }
}