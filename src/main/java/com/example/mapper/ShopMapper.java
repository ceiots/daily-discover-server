package com.example.mapper;

import com.example.model.Shop;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShopMapper {

    @Insert("INSERT INTO shop (shop_name, shop_logo, shop_description, user_id, contact_phone, contact_email, status) " +
            "VALUES (#{shopName}, #{shopLogo}, #{shopDescription}, #{userId}, #{contactPhone}, #{contactEmail}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Shop shop);

    @Select("SELECT * FROM shop WHERE id = #{id}")
    Shop findById(Long id);

    @Select("SELECT * FROM shop WHERE user_id = #{userId}")
    List<Shop> findByUserId(Long userId);

    @Select("SELECT * FROM shop")
    List<Shop> findAll();

    @Update("UPDATE shop SET shop_name = #{shopName}, shop_logo = #{shopLogo}, " +
            "shop_description = #{shopDescription}, contact_phone = #{contactPhone}, " +
            "contact_email = #{contactEmail}, status = #{status} " +
            "WHERE id = #{id}")
    void update(Shop shop);

    @Delete("DELETE FROM shop WHERE id = #{id}")
    void deleteById(Long id);
    
    @Select("SELECT * FROM shop WHERE shop_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Shop> searchByName(String keyword);
    
    @Update("UPDATE shop SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 