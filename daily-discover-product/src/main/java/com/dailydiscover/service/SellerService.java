package com.dailydiscover.service;

import com.dailydiscover.model.Seller;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SellerService extends IService<Seller> {
    
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
    
    /**
     * 根据ID查询商家
     */
    Seller findById(Long id);
    
    /**
     * 查询所有商家
     */
    List<Seller> findAll();
    
    /**
     * 查询已认证商家
     */
    List<Seller> findVerifiedSellers();
    
    /**
     * 保存商家
     */
    boolean save(Seller seller);
    
    /**
     * 更新商家
     */
    boolean update(Seller seller);
    
    /**
     * 停用商家
     */
    boolean deactivate(Long id);
}