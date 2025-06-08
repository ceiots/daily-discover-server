package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

/**
 * 商品分类数据库操作
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 根据ID查询分类
     */
    @Select("SELECT * FROM product_categories WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "icon", column = "icon"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "level", column = "level"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "isVisible", column = "is_visible"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    Category findById(Long id);
} 