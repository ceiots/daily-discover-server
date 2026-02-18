package com.dailydiscover.mapper;

import com.dailydiscover.model.InventoryTransaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存操作记录表 Mapper
 */
@Mapper
public interface InventoryTransactionMapper extends BaseMapper<InventoryTransaction> {
}