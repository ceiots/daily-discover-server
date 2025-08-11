package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 * 
 * @author Daily Discover Team
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（支持用户名、邮箱、手机号）
     */
    @Select("SELECT * FROM dd_user WHERE (username = #{username} OR email = #{username} OR phone = #{username}) AND deleted = 0")
    User findByUsername(@Param("username") String username);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM dd_user WHERE username = #{username} AND deleted = 0")
    int countByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM dd_user WHERE email = #{email} AND deleted = 0")
    int countByEmail(@Param("email") String email);

    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) FROM dd_user WHERE phone = #{phone} AND deleted = 0")
    int countByPhone(@Param("phone") String phone);
}