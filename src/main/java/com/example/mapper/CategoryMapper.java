// CategoryMapper.java
package com.example.mapper;

import com.example.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM categories") // 假设您有一个 categories 表
    List<Category> getAllCategories();
}