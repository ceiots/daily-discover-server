package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.SellerProfileMapper;
import com.dailydiscover.model.SellerProfile;
import com.dailydiscover.service.SellerProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SellerProfileServiceImpl extends ServiceImpl<SellerProfileMapper, SellerProfile> implements SellerProfileService {
    
    @Autowired
    private SellerProfileMapper sellerProfileMapper;
    
    @Override
    public SellerProfile getBySellerId(Long sellerId) {
        return lambdaQuery().eq(SellerProfile::getSellerId, sellerId).one();
    }
    
    @Override
    public SellerProfile createProfile(Long sellerId, String storeName, String storeDescription, String contactInfo) {
        SellerProfile profile = new SellerProfile();
        profile.setSellerId(sellerId);
        profile.setStoreName(storeName);
        profile.setStoreDescription(storeDescription);
        profile.setContactInfo(contactInfo);
        profile.setStatus("active");
        
        save(profile);
        return profile;
    }
    
    @Override
    public boolean updateStoreInfo(Long sellerId, String storeName, String storeDescription) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setStoreName(storeName);
            profile.setStoreDescription(storeDescription);
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
    public boolean updateSellerRating(Long sellerId, Double rating) {
        SellerProfile profile = getBySellerId(sellerId);
        if (profile != null) {
            profile.setSellerRating(rating);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public List<SellerProfile> getTopRatedSellers(Integer limit) {
        return lambdaQuery()
                .orderByDesc(SellerProfile::getSellerRating)
                .last("LIMIT " + limit)
                .list();
    }
}