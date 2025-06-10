package com.example.user.application.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserFavoriteAssembler;
import com.example.user.application.dto.UserFavoriteDTO;
import com.example.user.application.service.UserFavoriteService;
import com.example.user.domain.model.UserFavorite;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserFavoriteDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收藏应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteDomainService userFavoriteDomainService;
    private final UserFavoriteAssembler userFavoriteAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userFavoriteDomainService;
    }

    @Override
    @Transactional
    public UserFavoriteDTO addFavorite(UserFavoriteDTO userFavoriteDTO) {
        UserFavorite userFavorite = userFavoriteAssembler.toDomain(userFavoriteDTO);
        UserFavorite savedFavorite = userFavoriteDomainService.addFavorite(userFavorite);
        return userFavoriteAssembler.toDTO(savedFavorite);
    }

    @Override
    @Transactional
    public boolean cancelFavorite(Long userId, Integer type, Long targetId) {
        return userFavoriteDomainService.cancelFavorite(new UserId(userId), type, targetId);
    }

    @Override
    public List<UserFavoriteDTO> getUserFavorites(Long userId, Integer type, Integer limit) {
        List<UserFavorite> favorites = userFavoriteDomainService.getUserFavorites(new UserId(userId), type, limit);
        return favorites.stream()
                .map(userFavoriteAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserFavoriteDTO> getUserFavoritePage(Long userId, Integer type, PageRequest pageRequest) {
        PageResult<UserFavorite> pageResult = userFavoriteDomainService.getUserFavoritePage(
                new UserId(userId), type, pageRequest);
        
        List<UserFavoriteDTO> favoriteDTOs = pageResult.getList().stream()
                .map(userFavoriteAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(favoriteDTOs, pageResult.getTotal(), pageResult.getPages(), 
                pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    @Override
    public boolean isFavorite(Long userId, Integer type, Long targetId) {
        return userFavoriteDomainService.isFavorite(new UserId(userId), type, targetId);
    }

    @Override
    public Long countFavorites(Integer type, Long targetId) {
        return userFavoriteDomainService.countFavorites(type, targetId);
    }

    @Override
    @Transactional
    public boolean updateFavoriteNote(Long userId, Integer type, Long targetId, String note) {
        return userFavoriteDomainService.updateFavoriteNote(new UserId(userId), type, targetId, note);
    }
}