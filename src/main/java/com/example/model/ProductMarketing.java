package com.example.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.ArrayList;

/**
 * 商品营销信息
 */
@Data
@TableName("product_marketing")
public class ProductMarketing implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 是否热门商品
     */
    @TableField("is_hot")
    private Boolean isHot;

    /**
     * 是否新品
     */
    @TableField("is_new")
    private Boolean isNew;

    /**
     * 是否推荐商品
     */
    @TableField("is_recommended")
    private Boolean isRecommended;

    /**
     * 商品标签列表
     */
    @TableField("labels")
    private List<String> labels;

    /**
     * 相关商品ID列表
     */
    @TableField("related_product_ids")
    private List<Long> relatedProductIds;

    /**
     * 是否在首页展示
     */
    @TableField("show_in_homepage")
    private Boolean showInHomepage;

    /**
     * 营销开始时间
     */
    @TableField("marketing_start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date marketingStartTime;

    /**
     * 营销结束时间
     */
    @TableField("marketing_end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date marketingEndTime;

    /**
     * 排序权重
     */
    @TableField("sort_weight")
    private Integer sortWeight;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    
    /**
     * 创建新的商品营销对象
     */
    public static ProductMarketing create(Long productId) {
        ProductMarketing marketing = new ProductMarketing();
        marketing.setProductId(productId);
        marketing.setIsHot(false);
        marketing.setIsNew(false);
        marketing.setIsRecommended(false);
        marketing.setShowInHomepage(false);
        marketing.setSortWeight(0);
        return marketing;
    }
    
    /**
     * 检查营销活动是否在有效期内
     */
    public boolean isMarketingActive() {
        if (marketingStartTime == null || marketingEndTime == null) {
            return true; // 如果未设置时间，视为永久有效
        }
        
        Date now = new Date();
        return now.after(marketingStartTime) && now.before(marketingEndTime);
    }
    
    /**
     * 设置为热门商品
     */
    public void markAsHot(boolean isHot) {
        this.isHot = isHot;
        if (isHot && this.sortWeight == null) {
            this.sortWeight = 100; // 默认给热门商品较高权重
        }
    }
    
    /**
     * 设置为新品
     */
    public void markAsNew(boolean isNew) {
        this.isNew = isNew;
        if (isNew && this.marketingEndTime == null) {
            // 默认新品标记30天有效
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            this.marketingEndTime = calendar.getTime();
        }
    }
    
    /**
     * 设置为推荐商品
     */
    public void markAsRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
        if (isRecommended && this.sortWeight == null) {
            this.sortWeight = 80; // 默认给推荐商品较高权重
        }
    }
    
    /**
     * 添加标签
     */
    public void addLabel(String label) {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        if (!labels.contains(label)) {
            labels.add(label);
        }
    }
} 