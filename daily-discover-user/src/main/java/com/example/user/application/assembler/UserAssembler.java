package com.example.user.application.assembler;

import com.example.user.api.vo.UserVO;
import com.example.user.api.vo.UserProfileVO;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserProfileDTO;
import com.example.user.domain.model.user.UserProfile;
import com.example.user.domain.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * 用户数据转换器
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserAssembler {

    /**
     * User实体转UserDTO
     *
     * @param user 用户实体
     * @return 用户DTO
     */
    @Mapping(target = "id", source = "id.value")
    UserDTO toDTO(User user);

    /**
     * UserDTO转User实体
     *
     * @param userDTO 用户DTO
     * @return 用户实体
     */
    @Mapping(target = "id.value", source = "id")
    User toEntity(UserDTO userDTO);

    /**
     * UserProfile实体转UserProfileDTO
     *
     * @param userProfile 用户详情实体
     * @return 用户详情DTO
     */
    @Mapping(target = "userId", source = "userId.value")
    UserProfileDTO toProfileDTO(UserProfile userProfile);

    /**
     * UserProfileDTO转UserProfile实体
     *
     * @param userProfileDTO 用户详情DTO
     * @return 用户详情实体
     */
    @Mapping(target = "userId.value", source = "userId")
    UserProfile toProfileEntity(UserProfileDTO userProfileDTO);

    /**
     * 更新UserProfile实体
     *
     * @param userProfile    用户详情实体
     * @param userProfileDTO 用户详情DTO
     */
    @Mapping(target = "userId", ignore = true)
    void updateProfileFromDTO(@MappingTarget UserProfile userProfile, UserProfileDTO userProfileDTO);

    /**
     * UserDTO转UserVO
     *
     * @param userDTO 用户DTO
     * @return 用户VO
     */
    UserVO toVO(UserDTO userDTO);

    /**
     * UserProfileDTO转UserProfileVO
     *
     * @param userProfileDTO 用户详情DTO
     * @return 用户详情VO
     */
    UserProfileVO toProfileVO(UserProfileDTO userProfileDTO);
} 