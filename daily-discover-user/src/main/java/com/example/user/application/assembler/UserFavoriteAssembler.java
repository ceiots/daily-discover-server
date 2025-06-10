package com.example.user.application.assembler;

import com.example.user.application.dto.UserFavoriteDTO;
import com.example.user.domain.model.UserFavorite;
import com.example.user.domain.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户收藏数据转换器
 */
@Mapper(componentModel = "spring", uses = {})
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
    default UserFavorite toDomain(UserFavoriteDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // 使用UserFavorite.create方法创建对象
        UserFavorite favorite = UserFavorite.create(
            longToUserId(dto.getUserId()),
            dto.getType(),
            dto.getTargetId()
        );
        
        // 设置其他属性
        if (dto.getId() != null) {
            favorite.setId(dto.getId());
        }
        if (dto.getNote() != null) {
            favorite.setNote(dto.getNote());
        }
        
        return favorite;
    }

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
    
    /**
     * 将Long转换为UserId
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
}