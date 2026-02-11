package com.dailydiscover.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.user.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * 刷新令牌数据访问接口
 */
@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {

    /**
     * 根据令牌查找有效的刷新令牌
     */
    @Select("SELECT * FROM refresh_tokens WHERE token = #{token} AND expires_at > NOW() ORDER BY created_at DESC LIMIT 1")
    RefreshToken findValidByToken(@Param("token") String token);

    /**
     * 根据用户ID查找有效的刷新令牌
     */
    @Select("SELECT * FROM refresh_tokens WHERE user_id = #{userId} AND expires_at > NOW() ORDER BY created_at DESC LIMIT 1")
    RefreshToken findValidByUserId(@Param("userId") Long userId);

    /**
     * 删除过期的刷新令牌
     */
    @Select("DELETE FROM refresh_tokens WHERE expires_at <= NOW()")
    void deleteExpiredTokens();

    /**
     * 删除用户的所有刷新令牌
     */
    @Select("DELETE FROM refresh_tokens WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除指定的刷新令牌
     */
    @Select("DELETE FROM refresh_tokens WHERE token = #{token}")
    void deleteByToken(@Param("token") String token);
}