package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductSku;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品SKU数据访问接口
 */
@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    /**
     * 插入SKU记录
     */
    @Insert("INSERT INTO product_sku (product_id, sku_code, price, stock, image_url, specifications, created_at) " +
            "VALUES (#{productId}, #{skuCode}, #{price}, #{stock}, #{imageUrl}, " +
            "#{specifications,typeHandler=com.example.util.MapJsonTypeHandler}, " +
            "#{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSku(ProductSku productSku);

    /**
     * 根据商品ID查询SKU列表
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.MapJsonTypeHandler.class),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<ProductSku> findByProductId(Long productId);

    /**
     * 更新SKU信息
     */
    @Update("UPDATE product_sku SET " +
            "sku_code = #{skuCode}, " +
            "price = #{price}, " +
            "stock = #{stock}, " +
            "image_url = #{imageUrl}, " +
            "specifications = #{specifications,typeHandler=com.example.util.MapJsonTypeHandler} " +
            "WHERE id = #{id}")
    void updateSku(ProductSku productSku);

    /**
     * 删除SKU
     */
    @Delete("DELETE FROM product_sku WHERE id = #{id}")
    void deleteSku(Long id);
    
    /**
     * 根据商品ID删除所有SKU
     */
    @Delete("DELETE FROM product_sku WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
    
    /**
     * 获取单个SKU
     */
    @Select("SELECT * FROM product_sku WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.MapJsonTypeHandler.class),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    ProductSku findById(Long id);
} 