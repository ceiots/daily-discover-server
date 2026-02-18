package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.SellerMapper;
import com.dailydiscover.model.Seller;
import com.dailydiscover.service.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SellerServiceImpl extends ServiceImpl<SellerMapper, Seller> implements SellerService {
    
    @Autowired
    private SellerMapper sellerMapper;
    
    @Override
    public List<Seller> findByNameLike(String name) {
        return sellerMapper.findByNameLike(name);
    }
    
    @Override
    public List<Seller> findHighRatingSellers(Double minRating) {
        return sellerMapper.findHighRatingSellers(minRating);
    }
    
    @Override
    public List<Seller> findPremiumSellers() {
        return sellerMapper.findPremiumSellers();
    }
    
    @Override
    public List<Seller> findPopularSellers(int limit) {
        return sellerMapper.findPopularSellers(limit);
    }
    
    @Override
    public List<Seller> findTopSellingSellers(int limit) {
        return sellerMapper.findTopSellingSellers(limit);
    }
    
    @Override
    public List<Seller> findByStatus(String status) {
        return sellerMapper.findByStatus(status);
    }
}