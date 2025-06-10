package com.example.user.application.assembler;

import com.example.user.api.vo.UserVO;
import com.example.user.api.vo.UserProfileVO;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserProfileDTO;
import com.example.user.domain.model.User;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.UserRole;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

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
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "mobile", source = "mobile", qualifiedByName = "mobileToString")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStringList")
    UserDTO toDTO(User user);

    /**
     * UserDTO转User实体
     *
     * @param userDTO 用户DTO
     * @return 用户实体
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "longToUserId")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "mobile", source = "mobile", qualifiedByName = "stringToMobile")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "stringListToRoles")
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
    
    /**
     * Email转字符串
     */
    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }
    
    /**
     * 字符串转Email
     */
    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? new Email(email) : null;
    }
    
    /**
     * Mobile转字符串
     */
    @Named("mobileToString")
    default String mobileToString(Mobile mobile) {
        return mobile != null ? mobile.getValue() : null;
    }
    
    /**
     * 字符串转Mobile
     */
    @Named("stringToMobile")
    default Mobile stringToMobile(String mobile) {
        return mobile != null ? new Mobile(mobile) : null;
    }
    
    /**
     * 角色列表转字符串列表
     */
    @Named("rolesToStringList")
    default List<String> rolesToStringList(List<UserRole> roles) {
        return roles != null ? roles.stream()
                .map(UserRole::getName)
                .collect(Collectors.toList()) : null;
    }
    
    /**
     * 字符串列表转角色列表
     */
    @Named("stringListToRoles")
    default List<UserRole> stringListToRoles(List<String> roleNames) {
        return roleNames != null ? roleNames.stream()
                .map(name -> UserRole.create(name, name, null))
                .collect(Collectors.toList()) : null;
    }
    
    /**
     * 将Long转换为UserId
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
} 