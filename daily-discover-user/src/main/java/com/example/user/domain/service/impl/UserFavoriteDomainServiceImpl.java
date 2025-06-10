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
import java.util.Optional;

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
        // 直接删除收藏
        return userFavoriteRepository.deleteByUserIdAndTypeAndTargetId(userId, type, targetId);
    }

    @Override
    public List<UserFavorite> getUserFavorites(UserId userId, Integer type, Integer limit) {
        // 查询用户收藏列表
        return userFavoriteRepository.findByUserIdAndType(userId, type);
    }

    @Override
    public PageResult<UserFavorite> getUserFavoritePage(UserId userId, Integer type, PageRequest pageRequest) {
        return userFavoriteRepository.findPage(userId, type, pageRequest);
    }

    @Override
    public boolean isFavorite(UserId userId, Integer type, Long targetId) {
        // 检查收藏是否存在
        return userFavoriteRepository.existsByUserIdAndTypeAndTargetId(userId, type, targetId);
    }

    @Override
    public Long countFavorites(Integer type, Long targetId) {
        // 统计收藏数量
        int count = userFavoriteRepository.countByTargetIdAndType(targetId, type);
        return (long) count;
    }

    @Override
    @Transactional
    public boolean updateFavoriteNote(UserId userId, Integer type, Long targetId, String note) {
        // 查找收藏并更新备注
        Optional<UserFavorite> favoriteOpt = userFavoriteRepository.findByUserIdAndTypeAndTargetId(userId, type, targetId);
        if (favoriteOpt.isEmpty()) {
            return false;
        }
        
        UserFavorite favorite = favoriteOpt.get();
        favorite.setNote(note);
        userFavoriteRepository.update(favorite);
        return true;
    }
}