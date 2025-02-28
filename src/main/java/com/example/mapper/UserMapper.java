package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.model.User;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE phone_number = #{phone_number} AND password = #{password}")
    User findByPhoneNumberAndPassword(@Param("phone_number") String phone_number, @Param("password") String password);

    @Insert("INSERT INTO users (phone_number, password, nickname) VALUES (#{phoneNumber}, #{password}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerUser(User user);

    @Select("SELECT * FROM users WHERE phone_number = #{phoneNumber}")
    User findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    void updatePassword(@Param("phoneNumber") String phoneNumber, @Param("password") String password);
}