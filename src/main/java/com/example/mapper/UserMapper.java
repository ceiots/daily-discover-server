package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.model.User;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE phone_number = #{phone_number} AND password = #{password}")
    User findByPhoneNumberAndPassword(@Param("phone_number") String phone_number, @Param("password") String password);

    @Insert("INSERT INTO user (phone_number, password, nickname) VALUES (#{phoneNumber}, #{password}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerUser(User user);

    @Select("SELECT * FROM user WHERE phone_number = #{phoneNumber}")
    User findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    void updatePassword(@Param("phoneNumber") String phoneNumber, @Param("password") String password);

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(@Param("id") Long id);

    // 在 UserMapper.java 中添加更新头像的方法
    @Update("UPDATE user SET avatar = #{avatar} WHERE id = #{id}")
    void updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 受影响的行数
     */
    @Update("UPDATE user SET password = #{newPassword} WHERE id = #{userId}")
    int changePassword(Long userId, String newPassword);
}