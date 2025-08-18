package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    
    // 查找今日推荐商品
    List<ProductEntity> findTodayProducts(@Param("date") String date);
    
    // 查找昨日推荐商品
    List<ProductEntity> findYesterdayProducts(@Param("date") String date);
}