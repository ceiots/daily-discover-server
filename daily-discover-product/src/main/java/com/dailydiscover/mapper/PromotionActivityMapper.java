package com.dailydiscover.mapper;

import com.dailydiscover.model.PromotionActivity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 促销活动表 Mapper
 */
@Mapper
public interface PromotionActivityMapper extends BaseMapper<PromotionActivity> {
    
    /**
     * 查询当前有效的促销活动
     */
    @Select("SELECT * FROM promotion_activities WHERE status = 'active' AND start_time <= NOW() AND end_time >= NOW() ORDER BY created_at DESC")
    List<PromotionActivity> findActiveActivities();
    
    /**
     * 根据活动类型查询活动
     */
    @Select("SELECT * FROM promotion_activities WHERE activity_type = #{activityType} AND status = 'active' ORDER BY start_time DESC")
    List<PromotionActivity> findByActivityType(@Param("activityType") String activityType);
    
    /**
     * 查询即将开始的促销活动
     */
    @Select("SELECT * FROM promotion_activities WHERE status = 'active' AND start_time > NOW() ORDER BY start_time ASC")
    List<PromotionActivity> findUpcomingActivities();
}