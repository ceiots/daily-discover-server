package com.example.user.application.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserAuthAssembler;
import com.example.user.application.dto.UserAuthDTO;
import com.example.user.application.service.UserAuthService;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserAuth;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserAuthDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户授权应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserAuthDomainService userAuthDomainService;
    private final UserAuthAssembler userAuthAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userAuthDomainService;
    }

    @Override
    public List<UserAuthDTO> getUserAuths(Long userId) {
        List<UserAuth> auths = userAuthDomainService.getUserAuths(new UserId(userId));
        return auths.stream()
                .map(userAuthAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserAuthDTO getUserAuth(String identityType, String identifier) {
        Optional<UserAuth> authOpt = userAuthDomainService.getUserAuth(identityType, identifier);
        return authOpt.map(userAuthAssembler::toDTO).orElse(null);
    }

    @Override
    @Transactional
    public UserAuthDTO addUserAuth(UserAuthDTO userAuthDTO) {
        UserAuth userAuth = userAuthAssembler.toDomain(userAuthDTO);
        UserAuth savedAuth = userAuthDomainService.addUserAuth(userAuth);
        return userAuthAssembler.toDTO(savedAuth);
    }

    @Override
    @Transactional
    public UserAuthDTO updateUserAuth(UserAuthDTO userAuthDTO) {
        UserAuth userAuth = userAuthAssembler.toDomain(userAuthDTO);
        UserAuth updatedAuth = userAuthDomainService.updateUserAuth(userAuth);
        return userAuthAssembler.toDTO(updatedAuth);
    }

    @Override
    @Transactional
    public boolean deleteUserAuth(Long userId, String identityType) {
        return userAuthDomainService.deleteUserAuth(new UserId(userId), identityType);
    }

    @Override
    public boolean verifyUserAuth(String identityType, String identifier, String credential) {
        return userAuthDomainService.verifyUserAuth(identityType, identifier, credential);
    }

    @Override
    @Transactional
    public boolean updateCredential(Long userId, String identityType, String credential) {
        return userAuthDomainService.updateCredential(new UserId(userId), identityType, credential);
    }

    @Override
    @Transactional
    public boolean markVerified(Long userId, String identityType) {
        return userAuthDomainService.markVerified(new UserId(userId), identityType);
    }

    @Override
    @Transactional
    public boolean disableAuth(Long userId, String identityType) {
        return userAuthDomainService.disableAuth(new UserId(userId), identityType);
    }

    @Override
    @Transactional
    public boolean enableAuth(Long userId, String identityType) {
        return userAuthDomainService.enableAuth(new UserId(userId), identityType);
    }

    @Override
    public boolean existsAuth(String identityType, String identifier) {
        return userAuthDomainService.existsAuth(identityType, identifier);
    }
}