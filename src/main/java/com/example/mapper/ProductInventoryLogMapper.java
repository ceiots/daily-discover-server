package com.example.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductInventoryLog;

import java.util.List;

@Mapper
public interface ProductInventoryLogMapper extends BaseMapper<ProductInventoryLog> {

    @Insert("INSERT INTO product_inventory_log(product_id, sku_id, order_id, change_type, " +
            "quantity, before_stock, after_stock, operator_id, remark, create_time) " +
            "VALUES(#{productId}, #{skuId}, #{orderId}, #{changeType}, #{quantity}, " +
            "#{beforeStock}, #{afterStock}, #{operatorId}, #{remark}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductInventoryLog log);
    
    @Select("SELECT * FROM product_inventory_log WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "changeType", column = "change_type"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "beforeStock", column = "before_stock"),
        @Result(property = "afterStock", column = "after_stock"),
        @Result(property = "operatorId", column = "operator_id"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time")
    })
    ProductInventoryLog findById(@Param("id") Long id);
    
    @Select("SELECT * FROM product_inventory_log WHERE product_id = #{productId} AND sku_id = #{skuId} " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "changeType", column = "change_type"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "beforeStock", column = "before_stock"),
        @Result(property = "afterStock", column = "after_stock"),
        @Result(property = "operatorId", column = "operator_id"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time")
    })
    List<ProductInventoryLog> findByProductAndSku(
            @Param("productId") Long productId, 
            @Param("skuId") Long skuId, 
            @Param("limit") Integer limit);
    
    @Select("SELECT * FROM product_inventory_log WHERE order_id = #{orderId} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "changeType", column = "change_type"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "beforeStock", column = "before_stock"),
        @Result(property = "afterStock", column = "after_stock"),
        @Result(property = "operatorId", column = "operator_id"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "createTime", column = "create_time")
    })
    List<ProductInventoryLog> findByOrderId(@Param("orderId") Long orderId);
} 