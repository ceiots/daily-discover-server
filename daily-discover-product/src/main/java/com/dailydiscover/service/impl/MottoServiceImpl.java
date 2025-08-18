package com.dailydiscover.service;

import com.dailydiscover.mapper.MottoMapper;
import com.dailydiscover.model.MottoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MottoServiceImpl implements MottoService {

    @Autowired
    private MottoMapper mottoMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public MottoEntity getTodayMotto() {
        String today = LocalDateTime.now().format(DATE_FORMATTER);
        MottoEntity motto = mottoMapper.findTodayMotto(today);
        
        // 如果今日没有格言，返回一个默认格言
        if (motto == null) {
            motto = new MottoEntity();
            motto.setId(1L);
            motto.setText("每一天都是一个新的开始，珍惜当下，拥抱未来。");
            motto.setDate(today);
            motto.setIsActive(true);
            motto.setCreatedAt(LocalDateTime.now());
            motto.setUpdatedAt(LocalDateTime.now());
        }
        
        return motto;
    }

    @Override
    public List<MottoEntity> getAllMottos() {
        return mottoMapper.findAllMottos();
    }

    @Override
    public MottoEntity getMottoById(Long id) {
        return mottoMapper.findById(id);
    }

    @Override
    @Transactional
    public MottoEntity createMotto(MottoEntity motto) {
        motto.setCreatedAt(LocalDateTime.now());
        motto.setUpdatedAt(LocalDateTime.now());
        motto.setIsActive(true);
        
        int result = mottoMapper.insert(motto);
        if (result > 0) {
            return motto;
        }
        return null;
    }

    @Override
    @Transactional
    public MottoEntity updateMotto(Long id, MottoEntity mottoDetails) {
        MottoEntity existingMotto = mottoMapper.findById(id);
        if (existingMotto == null) {
            return null;
        }
        
        existingMotto.setText(mottoDetails.getText());
        existingMotto.setDate(mottoDetails.getDate());
        existingMotto.setIsActive(mottoDetails.getIsActive());
        existingMotto.setUpdatedAt(LocalDateTime.now());
        
        int result = mottoMapper.update(existingMotto);
        if (result > 0) {
            return existingMotto;
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteMotto(Long id) {
        MottoEntity motto = mottoMapper.findById(id);
        if (motto != null) {
            // 使用软删除
            mottoMapper.softDeleteById(id);
        }
    }

    @Override
    public List<MottoEntity> getMottosByDate(String date) {
        return mottoMapper.findByDate(date);
    }

    @Override
    public List<MottoEntity> getActiveMottos() {
        return mottoMapper.findActiveMottos();
    }
}