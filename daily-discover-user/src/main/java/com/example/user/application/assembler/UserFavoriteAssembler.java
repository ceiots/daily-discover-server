package com.example.user.application.assembler;

import com.example.user.application.dto.UserFavoriteDTO;
import com.example.user.domain.model.user.UserFavorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户收藏数据转换器
 */
@Mapper(componentModel = "spring")
public interface UserFavoriteAssembler {

    UserFavoriteAssembler INSTANCE = Mappers.getMapper(UserFavoriteAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userFavorite 用户收藏领域模型
     * @return 用户收藏DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserFavoriteDTO toDTO(UserFavorite userFavorite);

    /**
     * 将DTO转换为领域模型
     *
     * @param userFavoriteDTO 用户收藏DTO
     * @return 用户收藏领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    UserFavorite toDomain(UserFavoriteDTO userFavoriteDTO);

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userFavoriteList 用户收藏领域模型列表
     * @return 用户收藏DTO列表
     */
    List<UserFavoriteDTO> toDTO(List<UserFavorite> userFavoriteList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userFavoriteDTOList 用户收藏DTO列表
     * @return 用户收藏领域模型列表
     */
    List<UserFavorite> toDomain(List<UserFavoriteDTO> userFavoriteDTOList);
}