package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.dto.UserCollectionResponse;
import com.dailydiscover.entity.UserCollection;
import com.dailydiscover.mapper.UserCollectionMapper;
import com.dailydiscover.service.UserCollectionService;
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
        log.info("添加收藏: 用户ID={}, 内容类型={}, 内容ID={}", 
                userCollection.getUserId(), userCollection.getItemType(), userCollection.getItemId());
        
        // 检查是否已收藏
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userCollection.getUserId())
                   .eq("item_type", userCollection.getItemType())
                   .eq("item_id", userCollection.getItemId());
        
        UserCollection existingCollection = userCollectionMapper.selectOne(queryWrapper);
        if (existingCollection != null) {
            throw new RuntimeException("该内容已被收藏");
        }
        
        // 设置创建时间
        userCollection.setCreatedAt(LocalDateTime.now());
        
        userCollectionMapper.insert(userCollection);
        
        // 创建响应
        UserCollectionResponse response = new UserCollectionResponse();
        BeanUtils.copyProperties(userCollection, response);
        
        return response;
    }

    @Override
    public List<UserCollectionResponse> getCollectionsByUserId(Long userId) {
        log.info("获取用户收藏列表: 用户ID={}", userId);
        
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("created_at");
        
        List<UserCollection> collections = userCollectionMapper.selectList(queryWrapper);
        
        return collections.stream()
                .map(collection -> {
                    UserCollectionResponse response = new UserCollectionResponse();
                    BeanUtils.copyProperties(collection, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteCollection(Long id) {
        log.info("删除收藏: ID={}", id);
        
        UserCollection collection = userCollectionMapper.selectById(id);
        if (collection == null) {
            throw new RuntimeException("收藏记录不存在");
        }
        
        int result = userCollectionMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public boolean isItemCollected(Long userId, String itemType, String itemId) {
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("item_type", itemType)
                   .eq("item_id", itemId);
        
        UserCollection collection = userCollectionMapper.selectOne(queryWrapper);
        return collection != null;
    }

    @Override
    public int countCollections(Long userId) {
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        Long count = userCollectionMapper.selectCount(queryWrapper);
        return count != null ? count.intValue() : 0;
    }

    @Override
    @Transactional
    public boolean clearCollections(Long userId) {
        log.info("清空用户收藏: 用户ID={}", userId);
        
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        int result = userCollectionMapper.delete(queryWrapper);
        return result > 0;
    }
}