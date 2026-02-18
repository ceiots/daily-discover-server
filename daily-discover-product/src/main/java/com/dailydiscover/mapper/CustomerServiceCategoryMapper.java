package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.CustomerServiceCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 客服分类表 Mapper
 */
@Mapper
public interface CustomerServiceCategoryMapper extends BaseMapper<CustomerServiceCategory> {
    
    /**
     * 查询所有启用的客服分类
     */
    @Select("SELECT * FROM customer_service_categories WHERE status = 'active' ORDER BY sort_order ASC")
    List<CustomerServiceCategory> findActiveCategories();
    
    /**
     * 根据父级分类ID查询子分类
     */
    @Select("SELECT * FROM customer_service_categories WHERE parent_id = #{parentId} AND status = 'active' ORDER BY sort_order ASC")
    List<CustomerServiceCategory> findByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查询顶级分类
     */
    @Select("SELECT * FROM customer_service_categories WHERE parent_id IS NULL AND status = 'active' ORDER BY sort_order ASC")
    List<CustomerServiceCategory> findTopLevelCategories();
    
    /**
     * 根据分类名称模糊查询
     */
    @Select("SELECT * FROM customer_service_categories WHERE category_name LIKE CONCAT('%', #{name}, '%') AND status = 'active' ORDER BY sort_order ASC")
    List<CustomerServiceCategory> findByNameLike(@Param("name") String name);
}