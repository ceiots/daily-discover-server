package com.dailydiscover.mapper;

import com.dailydiscover.model.SellerProfile;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SellerProfileMapper {
    @Select("SELECT * FROM seller_profiles WHERE seller_id = #{sellerId}")
    SellerProfile findBySellerId(Long sellerId);
    
    @Insert("INSERT INTO seller_profiles (seller_id, positive_feedback, contact_info, services, certifications, business_hours) " +
            "VALUES (#{sellerId}, #{positiveFeedback}, #{contactInfo}, #{services}, #{certifications}, #{businessHours})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SellerProfile sellerProfile);
    
    @Update("UPDATE seller_profiles SET positive_feedback = #{positiveFeedback}, contact_info = #{contactInfo}, " +
            "services = #{services}, certifications = #{certifications}, business_hours = #{businessHours}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE seller_id = #{sellerId}")
    void update(SellerProfile sellerProfile);
    
    @Delete("DELETE FROM seller_profiles WHERE seller_id = #{sellerId}")
    void deleteBySellerId(Long sellerId);
}