package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(String phone);

    /**
     * 根据昵称查询用户
     * 
     * @param nickname 昵称
     * @return 用户信息
     */
    User selectByNickname(String nickname);

    /**
     * 更新用户积分
     * 
     * @param userId 用户ID
     * @param points 积分
     * @return 影响行数
     */
    int updateUserPoints(Long userId, Integer points);

    /**
     * 更新用户等级
     * 
     * @param userId 用户ID
     * @param levelId 等级ID
     * @return 影响行数
     */
    int updateUserLevel(Long userId, Long levelId);
}