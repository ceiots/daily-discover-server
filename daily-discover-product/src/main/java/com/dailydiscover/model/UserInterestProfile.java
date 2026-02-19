package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_interest_profiles")
public class UserInterestProfile {
    
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    @TableField("interest_tags")
    private String interestTags;
    
    @TableField("behavior_patterns")
    private String behaviorPatterns;
    
    @TableField("discovery_preferences")
    private String discoveryPreferences;
    
    @TableField("trending_interests")
    private String trendingInterests;
    
    @TableField("last_updated")
    private LocalDateTime lastUpdated;
    
    @TableField("profile_version")
    private Integer profileVersion;
}
    
    @TableField("profile_version")
    private Integer profileVersion;
}