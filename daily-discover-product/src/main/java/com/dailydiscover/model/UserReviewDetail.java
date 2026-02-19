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
    
    @TableField("review_content")
    private String reviewContent;
    
    @TableField("review_images")
    private String reviewImages;
    
    @TableField("pros")
    private String pros;
    
    @TableField("cons")
    private String cons;
    
    @TableField("usage_experience")
    private String usageExperience;
}