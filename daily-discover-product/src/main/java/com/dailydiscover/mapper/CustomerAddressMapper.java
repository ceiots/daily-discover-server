package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.CustomerAddress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerAddressMapper extends BaseMapper<CustomerAddress> {
}