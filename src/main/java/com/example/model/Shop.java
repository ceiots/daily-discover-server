package com.example.model;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.config.ImageConfig;
import lombok.Data;

@Data
@TableName(value = "shop", autoResultMap = true)
public class Shop {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("shop_name")
    private String shopName;
    
    @TableField("shop_logo")
    private String shopLogo;
    
    @TableField("shop_description")
    private String shopDescription;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("contact_phone")
    private String contactPhone;
    
    @TableField("contact_email")
    private String contactEmail;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("status")
    private Integer status;
    
    // 图片URL处理
    public String getShopLogo() {
        if (shopLogo != null && !shopLogo.startsWith("http")) {
            return ImageConfig.getImagePrefix() + shopLogo;
        }
        return shopLogo;
    }
} 