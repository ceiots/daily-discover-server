package com.dailydiscover.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.user.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 验证码数据访问接口
 */
@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {

    /**
     * 根据邮箱和类型查找有效的验证码
     */
    @Select("SELECT * FROM verification_codes WHERE email = #{email} AND type = #{type} AND used = false AND expires_at > NOW() ORDER BY created_at DESC LIMIT 1")
    VerificationCode findValidByEmailAndType(@Param("email") String email, @Param("type") String type);

    /**
     * 根据手机号和类型查找有效的验证码
     */
    @Select("SELECT * FROM verification_codes WHERE phone = #{phone} AND type = #{type} AND used = false AND expires_at > NOW() ORDER BY created_at DESC LIMIT 1")
    VerificationCode findValidByPhoneAndType(@Param("phone") String phone, @Param("type") String type);

    /**
     * 根据验证码和类型查找有效的验证码
     */
    @Select("SELECT * FROM verification_codes WHERE code = #{code} AND type = #{type} AND used = false AND expires_at > NOW() ORDER BY created_at DESC LIMIT 1")
    VerificationCode findValidByCodeAndType(@Param("code") String code, @Param("type") String type);

    /**
     * 标记验证码为已使用
     */
    @Select("UPDATE verification_codes SET used = true WHERE id = #{id}")
    void markAsUsed(@Param("id") Long id);

    /**
     * 删除过期的验证码
     */
    @Select("DELETE FROM verification_codes WHERE expires_at <= NOW()")
    void deleteExpiredCodes();

    /**
     * 根据用户ID查找验证码
     */
    @Select("SELECT * FROM verification_codes WHERE user_id = #{userId} AND type = #{type} AND used = false AND expires_at > NOW() ORDER BY created_at DESC LIMIT 1")
    VerificationCode findValidByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
}