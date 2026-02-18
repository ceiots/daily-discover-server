package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Seller;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家基础信息表 Mapper
 */
@Mapper
public interface SellerMapper extends BaseMapper<Seller> {
}