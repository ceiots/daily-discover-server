package com.dailydiscover.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface PurchaseMapper {
    
    @Select("SELECT * FROM purchase_history ORDER BY purchase_date DESC")
    List<Map<String, Object>> getPurchaseHistory();
    
    @Select("SELECT * FROM purchase_history WHERE product_id = #{productId} ORDER BY purchase_date DESC")
    List<Map<String, Object>> getPurchaseHistoryByProduct(String productId);
    
    @Select("SELECT * FROM purchase_history WHERE user_id = #{userId} ORDER BY purchase_date DESC")
    List<Map<String, Object>> getUserPurchaseHistory(String userId);
    
    @Insert("INSERT INTO purchase_history (user_id, product_id, quantity, purchase_date, total_amount) " +
            "VALUES (#{userId}, #{productId}, #{quantity}, CURRENT_TIMESTAMP, #{totalAmount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void recordPurchase(Map<String, Object> purchase);
    
    @Select("SELECT COUNT(*) as total_purchases, " +
            "SUM(CASE WHEN DATE(purchase_date) = CURRENT_DATE THEN 1 ELSE 0 END) as today_purchases, " +
            "SUM(CASE WHEN YEAR(purchase_date) = YEAR(CURRENT_DATE) AND MONTH(purchase_date) = MONTH(CURRENT_DATE) THEN 1 ELSE 0 END) as monthly_purchases, " +
            "SUM(total_amount) as total_revenue " +
            "FROM purchase_history")
    Map<String, Object> getPurchaseStats();
    
    @Select("SELECT COUNT(*) as total_purchases, " +
            "SUM(CASE WHEN DATE(purchase_date) = CURRENT_DATE THEN 1 ELSE 0 END) as today_purchases, " +
            "SUM(CASE WHEN YEAR(purchase_date) = YEAR(CURRENT_DATE) AND MONTH(purchase_date) = MONTH(CURRENT_DATE) THEN 1 ELSE 0 END) as monthly_purchases, " +
            "SUM(quantity) as total_quantity " +
            "FROM purchase_history WHERE product_id = #{productId}")
    Map<String, Object> getProductPurchaseStats(String productId);
}