package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 用户评价详情表
 */
@Data
@TableName("user_review_details")
public class UserReviewDetail {
    
    @TableId(value = "review_id", type = IdType.INPUT)
    private Long reviewId;
    
    @TableField("user_avatar")
    private String userAvatar;
    
    @TableField("comment")
    private String comment;
    
    @TableField("image_urls")
    private String imageUrls;
    
    @TableField("video_url")
    private String videoUrl;
    
    @TableField("moderation_notes")
    private String moderationNotes;
}