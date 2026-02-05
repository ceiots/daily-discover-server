package com.dailydiscover.service;

import com.dailydiscover.model.Seller;
import java.util.List;

public interface SellerService {
    Seller findById(Long id);
    List<Seller> findAll();
    List<Seller> findVerifiedSellers();
    List<Seller> findPremiumSellers();
    void save(Seller seller);
    void update(Seller seller);
    void deactivate(Long id);
}