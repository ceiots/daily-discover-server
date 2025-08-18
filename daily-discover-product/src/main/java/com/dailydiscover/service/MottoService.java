package com.dailydiscover.service;

import com.dailydiscover.model.MottoEntity;
import java.util.List;
import java.util.Map;

public interface MottoService {
    
    // 获取今日格言
    MottoEntity getTodayMotto();
    
    // 获取所有格言
    List<MottoEntity> getAllMottos();
    
    // 根据ID获取格言
    MottoEntity getMottoById(Long id);
    
    // 创建格言
    MottoEntity createMotto(MottoEntity motto);
    
    // 更新格言
    MottoEntity updateMotto(Long id, MottoEntity mottoDetails);
    
    // 删除格言
    void deleteMotto(Long id);
    
    // 根据日期获取格言
    List<MottoEntity> getMottosByDate(String date);
    
    // 获取活跃格言
    List<MottoEntity> getActiveMottos();
}