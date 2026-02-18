package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Purchase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购记录表 Mapper
 */
@Mapper
public interface PurchaseMapper extends BaseMapper<Purchase> {
}