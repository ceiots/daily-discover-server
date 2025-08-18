package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ProductMapper {
    
    // 查找今日推荐商品
    @Select("SELECT * FROM products WHERE DATE(created_at) = #{date} AND is_active = true ORDER BY created_at DESC")
    List<ProductEntity> findTodayProducts(@Param("date") String date);
    
    // 查找昨日推荐商品
    @Select("SELECT * FROM products WHERE DATE(created_at) = #{date} AND is_active = true ORDER BY created_at DESC")
    List<ProductEntity> findYesterdayProducts(@Param("date") String date);
}