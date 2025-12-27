package com.dailydiscover.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.user.dto.UserCollectionResponse;
import com.dailydiscover.user.entity.UserCollection;
import com.dailydiscover.user.mapper.UserCollectionMapper;
import com.dailydiscover.user.service.UserCollectionService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCollectionServiceImpl implements UserCollectionService {

    private final UserCollectionMapper userCollectionMapper;

    @Override
    @Transactional
    public UserCollectionResponse addCollection(UserCollection userCollection) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userCollection, null);
        
        try {
            // 检查是否已收藏
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userCollection.getUserId())
                       .eq("item_type", userCollection.getItemType())
                       .eq("item_id", userCollection.getItemId());
            
            UserCollection existingCollection = userCollectionMapper.selectOne(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_collection WHERE user_id = ? AND item_type = ? AND item_id = ?", 
                new Object[]{userCollection.getUserId(), userCollection.getItemType(), userCollection.getItemId()}, existingCollection);
            
            if (existingCollection != null) {
                throw new RuntimeException("该内容已被收藏");
            }
            
            // 设置创建时间
            userCollection.setCreatedAt(LocalDateTime.now());
            
            int insertResult = userCollectionMapper.insert(userCollection);
            LogTracer.traceDatabaseQuery("INSERT INTO user_collection", new Object[]{userCollection}, insertResult);
            
            // 创建响应
            UserCollectionResponse response = new UserCollectionResponse();
            BeanUtils.copyProperties(userCollection, response);
            
            LogTracer.traceBusinessMethod(userCollection, response);
            LogTracer.traceBusinessPerformance(startTime);
            
            return response;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    public List<UserCollectionResponse> getCollectionsByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .orderByDesc("created_at");
            
            List<UserCollection> collections = userCollectionMapper.selectList(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_collection WHERE user_id = ? ORDER BY created_at DESC", 
                new Object[]{userId}, collections.size());
            
            List<UserCollectionResponse> responses = collections.stream()
                    .map(collection -> {
                        UserCollectionResponse response = new UserCollectionResponse();
                        BeanUtils.copyProperties(collection, response);
                        return response;
                    })
                    .collect(Collectors.toList());
            
            LogTracer.traceBusinessMethod(userId, responses.size());
            LogTracer.traceBusinessPerformance(startTime);
            
            return responses;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean deleteCollection(Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(id, null);
        
        try {
            UserCollection collection = userCollectionMapper.selectById(id);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_collection WHERE id = ?", new Object[]{id}, collection);
            
            if (collection == null) {
                throw new RuntimeException("收藏记录不存在");
            }
            
            int result = userCollectionMapper.deleteById(id);
            LogTracer.traceDatabaseQuery("DELETE FROM user_collection WHERE id = ?", new Object[]{id}, result);
            
            boolean success = result > 0;
            LogTracer.traceBusinessMethod(id, success);
            LogTracer.traceBusinessPerformance(startTime);
            
            return success;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    public boolean isItemCollected(Long userId, String itemType, String itemId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(new Object[]{userId, itemType, itemId}, null);
        
        try {
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("item_type", itemType)
                       .eq("item_id", itemId);
            
            UserCollection collection = userCollectionMapper.selectOne(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_collection WHERE user_id = ? AND item_type = ? AND item_id = ?", 
                new Object[]{userId, itemType, itemId}, collection != null);
            
            boolean isCollected = collection != null;
            LogTracer.traceBusinessMethod(new Object[]{userId, itemType, itemId}, isCollected);
            LogTracer.traceBusinessPerformance(startTime);
            
            return isCollected;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    public int countCollections(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod("开始统计收藏数量", userId);
        
        try {
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            Long count = userCollectionMapper.selectCount(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT COUNT(*) FROM user_collection WHERE user_id = ?", new Object[]{userId}, count);
            
            int result = count != null ? count.intValue() : 0;
            LogTracer.traceBusinessMethod(userId, result);
            LogTracer.traceBusinessPerformance(startTime);
            
            return result;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean clearCollections(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            int result = userCollectionMapper.delete(queryWrapper);
            LogTracer.traceDatabaseQuery("DELETE FROM user_collection WHERE user_id = ?", new Object[]{userId}, result);
            
            boolean success = result > 0;
            LogTracer.traceBusinessMethod(userId, success);
            LogTracer.traceBusinessPerformance(startTime);
            
            return success;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }
}