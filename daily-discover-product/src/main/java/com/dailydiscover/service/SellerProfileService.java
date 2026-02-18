package com.dailydiscover.service;

import com.dailydiscover.model.SellerProfile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 卖家资料服务接口
 */
public interface SellerProfileService extends IService<SellerProfile> {
    
    /**
     * 根据卖家ID查询卖家资料
     */
    SellerProfile getBySellerId(Long sellerId);
    
    /**
     * 更新卖家资料
     */
    boolean updateSellerProfile(Long sellerId, String companyName, String contactPerson, 
                               String contactPhone, String businessLicense, String description);
    
    /**
     * 更新卖家评分
     */
    boolean updateSellerRating(Long sellerId, Double rating, Integer totalReviews);
    
    /**
     * 更新卖家状态
     */
    boolean updateSellerStatus(Long sellerId, String status);
    
    /**
     * 获取高评分卖家列表
     */
    java.util.List<SellerProfile> getHighRatedSellers(Double minRating, Integer limit);
    
    /**
     * 获取卖家统计信息
     */
    java.util.Map<String, Object> getSellerStats();
}