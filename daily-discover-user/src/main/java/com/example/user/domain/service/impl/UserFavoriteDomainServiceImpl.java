package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserFavorite;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.repository.UserFavoriteRepository;
import com.example.user.domain.service.UserFavoriteDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户收藏领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFavoriteDomainServiceImpl extends BaseDomainServiceImpl implements UserFavoriteDomainService {

    private final UserFavoriteRepository userFavoriteRepository;

    @Override
    @Transactional
    public UserFavorite addFavorite(UserFavorite userFavorite) {
        // 检查是否已收藏
        if (isFavorite(userFavorite.getUserId(), userFavorite.getType(), userFavorite.getTargetId())) {
            throw new BusinessException("已经收藏过了");
        }
        
        return userFavoriteRepository.save(userFavorite);
    }

    @Override
    @Transactional
    public boolean cancelFavorite(UserId userId, Integer type, Long targetId) {
        return userFavoriteRepository.remove(userId, type, targetId);
    }

    @Override
    public List<UserFavorite> getUserFavorites(UserId userId, Integer type, Integer limit) {
        return userFavoriteRepository.findByUserIdAndType(userId, type, limit);
    }

    @Override
    public PageResult<UserFavorite> getUserFavoritePage(UserId userId, Integer type, PageRequest pageRequest) {
        return userFavoriteRepository.findPage(userId, type, pageRequest);
    }

    @Override
    public boolean isFavorite(UserId userId, Integer type, Long targetId) {
        return userFavoriteRepository.exists(userId, type, targetId);
    }

    @Override
    public Long countFavorites(Integer type, Long targetId) {
        return userFavoriteRepository.countByTypeAndTargetId(type, targetId);
    }

    @Override
    @Transactional
    public boolean updateFavoriteNote(UserId userId, Integer type, Long targetId, String note) {
        return userFavoriteRepository.updateNote(userId, type, targetId, note);
    }
}