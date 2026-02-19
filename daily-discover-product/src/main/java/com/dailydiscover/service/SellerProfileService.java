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
    boolean updateSellerProfile(Long sellerId, String contactInfo, String services, 
                               String certifications, String businessHours);
    
    /**
     * 更新卖家好评率
     */
    boolean updatePositiveFeedback(Long sellerId, java.math.BigDecimal positiveFeedback);
    
    /**
     * 更新卖家联系信息
     */
    boolean updateContactInfo(Long sellerId, String contactInfo);
    
    /**
     * 更新卖家服务项目
     */
    boolean updateServices(Long sellerId, String services);
    
    /**
     * 更新卖家认证信息
     */
    boolean updateCertifications(Long sellerId, String certifications);
    
    /**
     * 更新卖家营业时间
     */
    boolean updateBusinessHours(Long sellerId, String businessHours);
}