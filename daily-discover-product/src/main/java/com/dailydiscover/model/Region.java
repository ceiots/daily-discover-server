package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 地区表（国家标准行政区划）
 */
@Data
@TableName("regions")
public class Region {
    
    @TableId(value = "region_id", type = IdType.INPUT)
    private String regionId;
    
    @TableField("region_name")
    private String regionName;
    
    @TableField("region_parent_id")
    private String regionParentId;
    
    @TableField("region_level")
    private Integer regionLevel;
    
    @TableField("region_code")
    private String regionCode;
}