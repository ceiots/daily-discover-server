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
            
            LogTracer.traceMethod("UserCollectionService.addCollection", "收藏添加成功", response);
            LogTracer.tracePerformance("UserCollectionService.addCollection", startTime, System.currentTimeMillis());
            
            return response;
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionService.addCollection", "添加收藏失败", e);
            throw e;
        }
    }

    @Override
    public List<UserCollectionResponse> getCollectionsByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionService.getCollectionsByUserId", "开始获取用户收藏列表", userId);
        
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
            
            LogTracer.traceMethod("UserCollectionService.getCollectionsByUserId", "获取收藏列表成功", responses.size());
            LogTracer.tracePerformance("UserCollectionService.getCollectionsByUserId", startTime, System.currentTimeMillis());
            
            return responses;
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionService.getCollectionsByUserId", "获取收藏列表失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean deleteCollection(Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionService.deleteCollection", "开始删除收藏", id);
        
        try {
            UserCollection collection = userCollectionMapper.selectById(id);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_collection WHERE id = ?", new Object[]{id}, collection);
            
            if (collection == null) {
                throw new RuntimeException("收藏记录不存在");
            }
            
            int result = userCollectionMapper.deleteById(id);
            LogTracer.traceDatabaseQuery("DELETE FROM user_collection WHERE id = ?", new Object[]{id}, result);
            
            boolean success = result > 0;
            LogTracer.traceMethod("UserCollectionService.deleteCollection", "删除收藏完成", success);
            LogTracer.tracePerformance("UserCollectionService.deleteCollection", startTime, System.currentTimeMillis());
            
            return success;
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionService.deleteCollection", "删除收藏失败", e);
            throw e;
        }
    }

    @Override
    public boolean isItemCollected(Long userId, String itemType, String itemId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionService.isItemCollected", "开始检查收藏状态", 
                new Object[]{userId, itemType, itemId});
        
        try {
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("item_type", itemType)
                       .eq("item_id", itemId);
            
            UserCollection collection = userCollectionMapper.selectOne(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM user_collection WHERE user_id = ? AND item_type = ? AND item_id = ?", 
                new Object[]{userId, itemType, itemId}, collection != null);
            
            boolean isCollected = collection != null;
            LogTracer.traceMethod("UserCollectionService.isItemCollected", "检查收藏状态完成", isCollected);
            LogTracer.tracePerformance("UserCollectionService.isItemCollected", startTime, System.currentTimeMillis());
            
            return isCollected;
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionService.isItemCollected", "检查收藏状态失败", e);
            throw e;
        }
    }

    @Override
    public int countCollections(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionService.countCollections", "开始统计收藏数量", userId);
        
        try {
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            Long count = userCollectionMapper.selectCount(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT COUNT(*) FROM user_collection WHERE user_id = ?", new Object[]{userId}, count);
            
            int result = count != null ? count.intValue() : 0;
            LogTracer.traceMethod("UserCollectionService.countCollections", "统计收藏数量完成", result);
            LogTracer.tracePerformance("UserCollectionService.countCollections", startTime, System.currentTimeMillis());
            
            return result;
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionService.countCollections", "统计收藏数量失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean clearCollections(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserCollectionService.clearCollections", "开始清空用户收藏", userId);
        
        try {
            QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            int result = userCollectionMapper.delete(queryWrapper);
            LogTracer.traceDatabaseQuery("DELETE FROM user_collection WHERE user_id = ?", new Object[]{userId}, result);
            
            boolean success = result > 0;
            LogTracer.traceMethod("UserCollectionService.clearCollections", "清空收藏完成", success);
            LogTracer.tracePerformance("UserCollectionService.clearCollections", startTime, System.currentTimeMillis());
            
            return success;
        } catch (Exception e) {
            LogTracer.traceException("UserCollectionService.clearCollections", "清空收藏失败", e);
            throw e;
        }
    }
}