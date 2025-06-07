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

    @Select("SELECT * FROM product_sku WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    ProductSku findById(Long id);
    
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<ProductSku> findByProductId(Long productId);
    
    @Insert("INSERT INTO product_sku (product_id, sku_code, price, stock, image_url, specifications) " +
            "VALUES (#{productId}, #{skuCode}, #{price}, #{stock}, #{imageUrl}, #{specifications,typeHandler=com.example.util.JsonTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSku productSku);
    
    @Update("UPDATE product_sku SET price = #{price}, stock = #{stock}, image_url = #{imageUrl}, " +
            "specifications = #{specifications,typeHandler=com.example.util.JsonTypeHandler} " +
            "WHERE id = #{id}")
    int update(ProductSku productSku);
    
    @Delete("DELETE FROM product_sku WHERE id = #{id}")
    int delete(Long id);
} 