package com.dailydiscover.service;

import com.dailydiscover.model.Seller;
import java.util.List;

public interface SellerService {
    
    /**
     * 根据商家名称查询商家
     */
    List<Seller> findByNameLike(String name);
    
    /**
     * 查询高评分商家
     */
    List<Seller> findHighRatingSellers(Double minRating);
    
    /**
     * 查询优选商家
     */
    List<Seller> findPremiumSellers();
    
    /**
     * 查询热门商家（按粉丝数量排序）
     */
    List<Seller> findPopularSellers(int limit);
    
    /**
     * 查询高销量商家
     */
    List<Seller> findTopSellingSellers(int limit);
    
    /**
     * 根据商家状态查询
     */
    List<Seller> findByStatus(String status);
}