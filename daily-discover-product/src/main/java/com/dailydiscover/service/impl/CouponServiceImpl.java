package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.CouponMapper;
import com.dailydiscover.model.Coupon;
import com.dailydiscover.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Override
    public List<Coupon> findActiveCoupons() {
        return couponMapper.findActiveCoupons();
    }
    
    @Override
    public List<Coupon> findByCouponType(String couponType) {
        return couponMapper.findByCouponType(couponType);
    }
    
    @Override
    public List<Coupon> findAvailableCoupons() {
        return couponMapper.findAvailableCoupons();
    }
    
    @Override
    public Coupon findByCouponCode(String couponCode) {
        return couponMapper.findByCouponCode(couponCode);
    }
}