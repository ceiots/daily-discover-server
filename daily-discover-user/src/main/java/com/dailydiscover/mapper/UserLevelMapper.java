package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.entity.UserLevel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户等级配置Mapper接口
 */
@Mapper
public interface UserLevelMapper extends BaseMapper<UserLevel> {

    /**
     * 根据积分范围查询等级
     * 
     * @param points 用户积分
     * @return 用户等级信息
     */
    UserLevel selectLevelByPoints(Integer points);

    /**
     * 根据等级名称查询等级
     * 
     * @param levelName 等级名称
     * @return 用户等级信息
     */
    UserLevel selectByLevelName(String levelName);
}