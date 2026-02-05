package com.dailydiscover.mapper;

import com.dailydiscover.model.Seller;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SellerMapper {
    @Select("SELECT * FROM sellers WHERE id = #{id}")
    Seller findById(Long id);
    
    @Select("SELECT * FROM sellers WHERE status = 'active' ORDER BY rating DESC")
    List<Seller> findAll();
    
    @Select("SELECT * FROM sellers WHERE is_verified = true AND status = 'active' ORDER BY rating DESC")
    List<Seller> findVerifiedSellers();
    
    @Select("SELECT * FROM sellers WHERE is_premium = true AND status = 'active' ORDER BY rating DESC")
    List<Seller> findPremiumSellers();
    
    @Insert("INSERT INTO sellers (name, description, logo_url, cover_url, rating, response_time, delivery_time, followers_count, positive_feedback, total_products, monthly_sales, contact_info, services, certifications, business_hours, is_verified, is_premium, status) " +
            "VALUES (#{name}, #{description}, #{logoUrl}, #{coverUrl}, #{rating}, #{responseTime}, #{deliveryTime}, #{followersCount}, #{positiveFeedback}, #{totalProducts}, #{monthlySales}, #{contactInfo}, #{services}, #{certifications}, #{businessHours}, #{isVerified}, #{isPremium}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Seller seller);
    
    @Update("UPDATE sellers SET name = #{name}, description = #{description}, logo_url = #{logoUrl}, cover_url = #{coverUrl}, rating = #{rating}, response_time = #{responseTime}, delivery_time = #{deliveryTime}, followers_count = #{followersCount}, positive_feedback = #{positiveFeedback}, total_products = #{totalProducts}, monthly_sales = #{monthlySales}, contact_info = #{contactInfo}, services = #{services}, certifications = #{certifications}, business_hours = #{businessHours}, is_verified = #{isVerified}, is_premium = #{isPremium}, status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(Seller seller);
    
    @Update("UPDATE sellers SET status = 'inactive' WHERE id = #{id}")
    void deactivate(Long id);
}