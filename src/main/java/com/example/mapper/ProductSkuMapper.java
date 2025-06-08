package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductSku;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Map;

/**
 * 商品SKU数据访问接口
 */
@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    /**
     * 插入SKU记录
     */
    @Insert("INSERT INTO product_sku (product_id, sku_code, barcode, price, original_price, stock, locked_stock, " +
            "sales_count, image_url, specifications, is_default, status, create_time, deleted) " +
            "VALUES (#{productId}, #{skuCode}, #{barcode}, #{price}, #{originalPrice}, #{stock}, #{lockedStock}, " +
            "#{salesCount}, #{imageUrl}, #{specifications,typeHandler=com.example.util.JsonTypeHandler}, " +
            "#{isDefault}, #{status}, #{createTime}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSku(ProductSku productSku);

    /**
     * 根据商品ID查询SKU列表
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} AND deleted = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "barcode", column = "barcode"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "lockedStock", column = "locked_stock"),
        @Result(property = "salesCount", column = "sales_count"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "isDefault", column = "is_default"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted")
    })
    List<ProductSku> findByProductId(Long productId);

    /**
     * 更新SKU信息
     */
    @Update("UPDATE product_sku SET " +
            "sku_code = #{skuCode}, " +
            "barcode = #{barcode}, " +
            "price = #{price}, " +
            "original_price = #{originalPrice}, " +
            "stock = #{stock}, " +
            "locked_stock = #{lockedStock}, " +
            "sales_count = #{salesCount}, " +
            "image_url = #{imageUrl}, " +
            "specifications = #{specifications,typeHandler=com.example.util.JsonTypeHandler}, " +
            "is_default = #{isDefault}, " +
            "status = #{status}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void updateSku(ProductSku productSku);

    /**
     * 锁定库存（下单时调用）
     * 添加乐观锁以防止超卖
     */
    @Update("UPDATE product_sku SET " +
            "stock = stock - #{quantity}, " +
            "locked_stock = IFNULL(locked_stock, 0) + #{quantity}, " +
            "update_time = NOW() " +
            "WHERE id = #{skuId} AND stock >= #{quantity}")
    int lockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 解锁库存（取消订单或支付超时时调用）
     */
    @Update("UPDATE product_sku SET " +
            "stock = stock + #{quantity}, " +
            "locked_stock = locked_stock - #{quantity}, " +
            "update_time = NOW() " +
            "WHERE id = #{skuId} AND locked_stock >= #{quantity}")
    int unlockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 扣减锁定库存并增加销量（支付成功时调用）
     */
    @Update("UPDATE product_sku SET " +
            "locked_stock = locked_stock - #{quantity}, " +
            "sales_count = sales_count + #{quantity}, " +
            "update_time = NOW() " +
            "WHERE id = #{skuId} AND locked_stock >= #{quantity}")
    int deductLockedStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 逻辑删除SKU
     */
    @Update("UPDATE product_sku SET deleted = 1, update_time = NOW() WHERE id = #{id}")
    void deleteSku(Long id);
    
    /**
     * 根据商品ID逻辑删除所有SKU
     */
    @Update("UPDATE product_sku SET deleted = 1, update_time = NOW() WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
    
    /**
     * 获取单个SKU
     */
    @Select("SELECT * FROM product_sku WHERE id = #{id} AND deleted = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "barcode", column = "barcode"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "lockedStock", column = "locked_stock"),
        @Result(property = "salesCount", column = "sales_count"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "isDefault", column = "is_default"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted")
    })
    ProductSku findById(Long id);
    
    /**
     * 根据SKU编码查找SKU
     */
    @Select("SELECT * FROM product_sku WHERE sku_code = #{skuCode} AND deleted = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "barcode", column = "barcode"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "lockedStock", column = "locked_stock"),
        @Result(property = "salesCount", column = "sales_count"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "isDefault", column = "is_default"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted")
    })
    ProductSku findBySkuCode(String skuCode);
    
    /**
     * 更新商品总库存和总销量（根据所有SKU计算）
     * 此操作使用单条SQL提高性能
     */
    @Update("UPDATE product p SET " +
            "p.total_stock = (SELECT IFNULL(SUM(ps.stock), 0) FROM product_sku ps WHERE ps.product_id = p.id AND ps.deleted = 0), " +
            "p.total_sales = (SELECT IFNULL(SUM(ps.sales_count), 0) FROM product_sku ps WHERE ps.product_id = p.id AND ps.deleted = 0), " +
            "p.update_time = NOW() " +
            "WHERE p.id = #{productId}")
    int updateProductTotalStock(@Param("productId") Long productId);
} 