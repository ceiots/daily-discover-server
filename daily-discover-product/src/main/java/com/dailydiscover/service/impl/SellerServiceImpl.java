package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.SellerMapper;
import com.dailydiscover.model.Seller;
import com.dailydiscover.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {
    
    @Autowired
    private SellerMapper sellerMapper;
    
    @Override
    public Seller findById(Long id) {
        return sellerMapper.findById(id);
    }
    
    @Override
    public List<Seller> findAll() {
        return sellerMapper.findAll();
    }
    
    @Override
    public List<Seller> findVerifiedSellers() {
        return sellerMapper.findVerifiedSellers();
    }
    
    @Override
    public List<Seller> findPremiumSellers() {
        return sellerMapper.findPremiumSellers();
    }
    
    @Override
    public void save(Seller seller) {
        sellerMapper.insert(seller);
    }
    
    @Override
    public void update(Seller seller) {
        sellerMapper.update(seller);
    }
    
    @Override
    public void deactivate(Long id) {
        sellerMapper.deactivate(id);
    }
}