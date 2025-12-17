package com.dailydiscover.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.user.entity.NotificationSetting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户通知设置Mapper接口
 * 
 */
@Mapper
public interface NotificationSettingMapper extends BaseMapper<NotificationSetting> {

    /**
     * 根据用户ID查询通知设置
     * 
     * @param userId 用户ID
     * @return 通知设置信息
     */
    NotificationSetting selectByUserId(Long userId);
}