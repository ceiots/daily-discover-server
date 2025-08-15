package com.dailydiscover.mapper;

import com.dailydiscover.model.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BrandMapper {
    
    @Select("SELECT * FROM brands")
    List<Brand> findAll();
    
    @Select("SELECT * FROM brands WHERE featured = true")
    List<Brand> findFeaturedBrands();
    
    @Select("SELECT * FROM brands WHERE category = #{category}")
    List<Brand> findByCategory(String category);
    
    @Select("SELECT * FROM brands WHERE id = #{id}")
    Brand findById(Long id);
}