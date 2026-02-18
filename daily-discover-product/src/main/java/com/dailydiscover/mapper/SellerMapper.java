package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商家基础信息表 Mapper
 */
@Mapper
public interface SellerMapper extends BaseMapper<Seller> {
    
    /**
     * 查询所有启用状态的商家
     */
    @Select("SELECT * FROM sellers WHERE status = 'active' ORDER BY rating DESC")
    List<Seller> findAllActiveSellers();
    
    /**
     * 查询优选商家
     */
    @Select("SELECT * FROM sellers WHERE is_premium = true AND status = 'active' ORDER BY rating DESC")
    List<Seller> findPremiumSellers();
    
    /**
     * 查询已认证的商家
     */
    @Select("SELECT * FROM sellers WHERE is_verified = true AND status = 'active' ORDER BY rating DESC")
    List<Seller> findVerifiedSellers();
    
    /**
     * 根据商家名称模糊查询
     */
    @Select("SELECT * FROM sellers WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 'active' ORDER BY rating DESC")
    List<Seller> findByNameLike(@Param("name") String name);
    
    /**
     * 查询热门商家（按粉丝数量排序）
     */
    @Select("SELECT * FROM sellers WHERE status = 'active' ORDER BY followers_count DESC LIMIT #{limit}")
    List<Seller> findPopularSellers(@Param("limit") int limit);
    
    /**
     * 查询高评分商家
     */
    @Select("SELECT * FROM sellers WHERE rating >= #{minRating} AND status = 'active' ORDER BY rating DESC")
    List<Seller> findByRating(@Param("minRating") Double minRating);
}