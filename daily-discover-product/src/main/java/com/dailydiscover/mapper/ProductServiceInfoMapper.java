package com.dailydiscover.mapper;

import com.dailydiscover.model.dto.ProductServiceInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 产品服务信息Mapper
 */
@Mapper
public interface ProductServiceInfoMapper {
    
    /**
     * 根据商品ID查询产品服务信息
     */
    @Select("SELECT " +
            "psi.id as info_id, psi.info_key, psi.info_label, psi.data_type, psi.value_unit, " +
            "psiv.string_value, psiv.number_value, psiv.boolean_value, psiv.date_value, psiv.array_values, " +
            "psiv.sort_order, " +
            "psc.id as category_id, psc.category_name, psc.category_code, psc.sort_order as category_sort_order, " +
            "psc.is_collapsible, psc.display_icon, psc.display_color " +
            "FROM product_service_info_values psiv " +
            "INNER JOIN product_service_infos psi ON psiv.info_id = psi.id " +
            "INNER JOIN product_service_categories psc ON psi.category_id = psc.id " +
            "WHERE psiv.product_id = #{productId} AND psi.status = 1 AND psc.status = 1 " +
            "ORDER BY psc.sort_order, psi.sort_order, psiv.sort_order")
    List<ProductServiceInfoDTO> findServiceInfoByProductId(@Param("productId") Long productId);
    
    /**
     * 查询所有启用的产品服务信息分类
     */
    @Select("SELECT id as category_id, category_name, category_code, sort_order, is_collapsible, display_icon, display_color " +
            "FROM product_service_categories " +
            "WHERE status = 1 " +
            "ORDER BY sort_order")
    List<ProductServiceInfoDTO.ServiceCategoryDTO> findAllEnabledCategories();
    
    /**
     * 根据分类ID查询信息项
     */
    @Select("SELECT id as info_id, info_key, info_label, data_type, value_unit, placeholder, sort_order " +
            "FROM product_service_infos " +
            "WHERE category_id = #{categoryId} AND status = 1 " +
            "ORDER BY sort_order")
    List<ProductServiceInfoDTO> findInfoItemsByCategoryId(@Param("categoryId") Long categoryId);
}