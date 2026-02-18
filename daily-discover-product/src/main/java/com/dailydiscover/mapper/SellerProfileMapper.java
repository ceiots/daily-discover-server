package com.dailydiscover.mapper;

import com.dailydiscover.model.SellerProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商家资料表 Mapper
 */
@Mapper
public interface SellerProfileMapper extends BaseMapper<SellerProfile> {
    
    /**
     * 根据商家ID查询商家资料
     */
    @Select("SELECT * FROM seller_profiles WHERE seller_id = #{sellerId}")
    SellerProfile findBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * 查询高好评率的商家资料
     */
    @Select("SELECT * FROM seller_profiles WHERE positive_feedback >= #{minRating} ORDER BY positive_feedback DESC")
    SellerProfile findHighRatingProfiles(@Param("minRating") Double minRating);
}