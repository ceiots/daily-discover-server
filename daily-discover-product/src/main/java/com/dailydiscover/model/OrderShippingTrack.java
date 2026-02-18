package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 物流跟踪记录表
 */
@Data
@TableName("order_shipping_tracks")
public class OrderShippingTrack {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("shipping_id")
    private Long shippingId;
    
    @TableField("track_time")
    private LocalDateTime trackTime;
    
    @TableField("track_description")
    private String trackDescription;
    
    @TableField("track_location")
    private String trackLocation;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}