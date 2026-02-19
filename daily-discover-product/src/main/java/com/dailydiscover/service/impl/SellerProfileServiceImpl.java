package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.SellerProfileMapper;
import com.dailydiscover.model.SellerProfile;
import com.dailydiscover.service.SellerProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class SellerProfileServiceImpl extends ServiceImpl<SellerProfileMapper, SellerProfile> implements SellerProfileService {
    
    @Autowired
    private SellerProfileMapper sellerProfileMapper;
    
    @Override
    public SellerProfile getBySellerId(Long sellerId) {
        return sellerProfileMapper.findBySellerId(sellerId);
    }
    
    @Override
    public boolean updateSellerProfile(Long sellerId, String contactInfo, String services, 
                                      String certifications, String businessHours) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setContactInfo(contactInfo);
            profile.setServices(services);
            profile.setCertifications(certifications);
            profile.setBusinessHours(businessHours);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public boolean updatePositiveFeedback(Long sellerId, BigDecimal positiveFeedback) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setPositiveFeedback(positiveFeedback);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public boolean updateContactInfo(Long sellerId, String contactInfo) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setContactInfo(contactInfo);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public boolean updateServices(Long sellerId, String services) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setServices(services);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public boolean updateCertifications(Long sellerId, String certifications) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setCertifications(certifications);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public boolean updateBusinessHours(Long sellerId, String businessHours) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setBusinessHours(businessHours);
            return updateById(profile);
        }
        return false;
    }
    
    /**
     * 获取高好评率的商家资料
     */
    public SellerProfile getHighRatingProfile(Double minRating) {
        return sellerProfileMapper.findHighRatingProfiles(minRating);
    }

}