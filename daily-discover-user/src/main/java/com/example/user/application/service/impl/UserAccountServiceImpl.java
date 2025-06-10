package com.example.user.application.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.common.result.ResultCode;
import com.example.user.application.assembler.UserAccountAssembler;
import com.example.user.application.dto.UserAccountDTO;
import com.example.user.application.dto.UserAccountLogDTO;
import com.example.user.application.service.UserAccountService;
import com.example.user.domain.model.UserAccount;
import com.example.user.domain.model.UserAccountLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.repository.UserAccountLogRepository;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserAccountDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户账户应用服务实现类
 * 通过组合方式注入领域服务，实现业务流程编排
 */
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountDomainService userAccountDomainService;
    private final UserAccountLogRepository userAccountLogRepository;
    private final UserAccountAssembler userAccountAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userAccountDomainService;
    }

    @Override
    public UserAccountDTO getUserAccount(Long userId) {
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO createUserAccount(Long userId) {
        // 检查账户是否已存在
        Optional<UserAccount> existingAccount = userAccountDomainService.getUserAccount(new UserId(userId));
        if (existingAccount.isPresent()) {
            throw new BusinessException(ResultCode.ACCOUNT_EXISTS);
        }
        
        // 创建账户
        UserAccount account = userAccountDomainService.createUserAccount(new UserId(userId));
        
        return userAccountAssembler.toDTO(account);
    }

    @Override
    @Transactional
    public UserAccountDTO increaseBalance(Long userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.INVALID_AMOUNT);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.increaseBalance(new UserId(userId), amount, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO decreaseBalance(Long userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.INVALID_AMOUNT);
        }
        
        // 检查余额是否足够
        if (!userAccountDomainService.isBalanceEnough(new UserId(userId), amount)) {
            throw new BusinessException(ResultCode.BALANCE_NOT_ENOUGH);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.decreaseBalance(new UserId(userId), amount, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO freezeAmount(Long userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.INVALID_AMOUNT);
        }
        
        // 检查余额是否足够
        if (!userAccountDomainService.isBalanceEnough(new UserId(userId), amount)) {
            throw new BusinessException(ResultCode.BALANCE_NOT_ENOUGH);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.freezeAmount(new UserId(userId), amount, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO unfreezeAmount(Long userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.INVALID_AMOUNT);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.unfreezeAmount(new UserId(userId), amount, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO decreaseFromFrozen(Long userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.INVALID_AMOUNT);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.decreaseFromFrozen(new UserId(userId), amount, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO increasePoints(Long userId, Integer points, Integer source, String sourceId, String remark) {
        // 参数校验
        if (points == null || points <= 0) {
            throw new BusinessException(ResultCode.INVALID_POINTS);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.increasePoints(new UserId(userId), points, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO decreasePoints(Long userId, Integer points, Integer source, String sourceId, String remark) {
        // 参数校验
        if (points == null || points <= 0) {
            throw new BusinessException(ResultCode.INVALID_POINTS);
        }
        
        // 检查积分是否足够
        if (!userAccountDomainService.isPointsEnough(new UserId(userId), points)) {
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.decreasePoints(new UserId(userId), points, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO increaseGrowthValue(Long userId, Integer growthValue, Integer source, String sourceId, String remark) {
        // 参数校验
        if (growthValue == null || growthValue <= 0) {
            throw new BusinessException(ResultCode.INVALID_GROWTH_VALUE);
        }
        
        // 调用领域服务
        UserAccountLog log = userAccountDomainService.increaseGrowthValue(new UserId(userId), growthValue, source, sourceId, remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO freezeAccount(Long userId, String remark) {
        // 调用领域服务
        userAccountDomainService.freezeAccount(new UserId(userId), remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    @Transactional
    public UserAccountDTO unfreezeAccount(Long userId, String remark) {
        // 调用领域服务
        userAccountDomainService.unfreezeAccount(new UserId(userId), remark);
        
        // 获取更新后的账户
        Optional<UserAccount> accountOpt = userAccountDomainService.getUserAccount(new UserId(userId));
        if (accountOpt.isEmpty()) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }
        
        return userAccountAssembler.toDTO(accountOpt.get());
    }

    @Override
    public boolean isBalanceEnough(Long userId, BigDecimal amount) {
        return userAccountDomainService.isBalanceEnough(new UserId(userId), amount);
    }

    @Override
    public boolean isPointsEnough(Long userId, Integer points) {
        return userAccountDomainService.isPointsEnough(new UserId(userId), points);
    }

    @Override
    public PageResult<UserAccountLogDTO> getAccountLogs(Long userId, PageRequest pageRequest) {
        PageResult<UserAccountLog> pageResult = userAccountLogRepository.findPageByUserId(new UserId(userId), pageRequest);
        
        List<UserAccountLogDTO> logDTOs = pageResult.getList().stream()
                .map(userAccountAssembler::toLogDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(logDTOs, pageResult.getTotal(), pageResult.getPages(), pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    @Override
    public List<UserAccountLogDTO> getAccountLogs(Long userId, Integer type, Integer source, Integer limit) {
        List<UserAccountLog> logs = userAccountLogRepository.findByUserIdAndTypeAndSource(new UserId(userId), type, source, limit);
        
        return logs.stream()
                .map(userAccountAssembler::toLogDTO)
                .collect(Collectors.toList());
    }
} 