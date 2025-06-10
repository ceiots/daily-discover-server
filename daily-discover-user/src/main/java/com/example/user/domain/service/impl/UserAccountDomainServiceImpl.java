package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.user.UserAccount;
import com.example.user.domain.model.user.UserAccountLog;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.valueobject.Password;
import com.example.user.domain.repository.UserAccountRepository;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserAccountDomainService;
import com.example.user.infrastructure.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 用户账户领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountDomainServiceImpl implements UserAccountDomainService, BaseDomainService {

    private final UserAccountRepository userAccountRepository;
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$");

    @Override
    public Optional<UserAccount> getUserAccount(UserId userId) {
        return userAccountRepository.findByUserId(userId.getValue());
    }

    @Override
    @Transactional
    public UserAccount createUserAccount(UserId userId) {
        // 检查账户是否已存在
        Optional<UserAccount> existingAccount = userAccountRepository.findByUserId(userId.getValue());
        if (existingAccount.isPresent()) {
            return existingAccount.get();
        }

        // 创建新账户
        UserAccount userAccount = UserAccount.create(userId);
        return userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional
    public UserAccountLog increaseBalance(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("增加金额必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrCreate(userId);

        // 记录变动前金额
        BigDecimal beforeAmount = userAccount.getBalance();

        // 增加余额
        userAccount.addBalance(amount);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createIncomeLog(
                userId, amount, beforeAmount, userAccount.getBalance(), source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public UserAccountLog decreaseBalance(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("扣减金额必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrThrow(userId);

        // 检查余额是否足够
        if (userAccount.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("账户余额不足");
        }

        // 记录变动前金额
        BigDecimal beforeAmount = userAccount.getBalance();

        // 扣减余额
        userAccount.subtractBalance(amount);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createExpenseLog(
                userId, amount, beforeAmount, userAccount.getBalance(), source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public UserAccountLog freezeAmount(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("冻结金额必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrThrow(userId);

        // 检查可用余额是否足够
        if (userAccount.getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException("可用余额不足");
        }

        // 记录变动前金额
        BigDecimal beforeAmount = userAccount.getFreezeAmount();

        // 冻结金额
        userAccount.freezeAmount(amount);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createFreezeLog(
                userId, amount, beforeAmount, userAccount.getFreezeAmount(), source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public UserAccountLog unfreezeAmount(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("解冻金额必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrThrow(userId);

        // 检查冻结金额是否足够
        if (userAccount.getFreezeAmount().compareTo(amount) < 0) {
            throw new BusinessException("冻结金额不足");
        }

        // 记录变动前金额
        BigDecimal beforeAmount = userAccount.getFreezeAmount();

        // 解冻金额
        userAccount.unfreezeAmount(amount);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createUnfreezeLog(
                userId, amount, beforeAmount, userAccount.getFreezeAmount(), source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public UserAccountLog decreaseFromFrozen(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("扣减金额必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrThrow(userId);

        // 检查冻结金额是否足够
        if (userAccount.getFreezeAmount().compareTo(amount) < 0) {
            throw new BusinessException("冻结金额不足");
        }

        // 记录变动前金额
        BigDecimal beforeAmount = userAccount.getFreezeAmount();

        // 从冻结金额中扣减
        userAccount.unfreezeAndSubtract(amount);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createExpenseLog(
                userId, amount, beforeAmount, userAccount.getFreezeAmount(), source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public UserAccountLog increasePoints(UserId userId, Integer points, Integer source, String sourceId, String remark) {
        if (points <= 0) {
            throw new BusinessException("增加积分必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrCreate(userId);

        // 记录变动前积分
        Integer beforePoints = userAccount.getPoints();

        // 增加积分
        userAccount.addPoints(points);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createPointsLog(userId, points, source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public UserAccountLog decreasePoints(UserId userId, Integer points, Integer source, String sourceId, String remark) {
        if (points <= 0) {
            throw new BusinessException("扣减积分必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrThrow(userId);

        // 检查积分是否足够
        if (userAccount.getPoints() < points) {
            throw new BusinessException("账户积分不足");
        }

        // 记录变动前积分
        Integer beforePoints = userAccount.getPoints();

        // 扣减积分
        userAccount.subtractPoints(points);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createPointsLog(userId, -points, source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public UserAccountLog increaseGrowthValue(UserId userId, Integer growthValue, Integer source, String sourceId, String remark) {
        if (growthValue <= 0) {
            throw new BusinessException("增加成长值必须大于0");
        }

        // 获取用户账户
        UserAccount userAccount = getUserAccountOrCreate(userId);

        // 记录变动前成长值
        Integer beforeGrowth = userAccount.getGrowthValue();

        // 增加成长值
        userAccount.addGrowthValue(growthValue);
        userAccountRepository.update(userAccount);

        // 创建账户流水记录
        UserAccountLog accountLog = UserAccountLog.createGrowthLog(userId, growthValue, source, sourceId, remark);
        return userAccountRepository.saveAccountLog(accountLog);
    }

    @Override
    @Transactional
    public void freezeAccount(UserId userId, String remark) {
        // 获取用户账户
        UserAccount userAccount = getUserAccountOrThrow(userId);

        // 冻结账户
        userAccount.freeze();
        userAccountRepository.update(userAccount);
    }

    @Override
    @Transactional
    public void unfreezeAccount(UserId userId, String remark) {
        // 获取用户账户
        UserAccount userAccount = getUserAccountOrThrow(userId);

        // 解冻账户
        userAccount.unfreeze();
        userAccountRepository.update(userAccount);
    }

    @Override
    public boolean isBalanceEnough(UserId userId, BigDecimal amount) {
        Optional<UserAccount> userAccountOpt = userAccountRepository.findByUserId(userId.getValue());
        return userAccountOpt.map(account -> account.getBalance().compareTo(amount) >= 0).orElse(false);
    }

    @Override
    public boolean isPointsEnough(UserId userId, Integer points) {
        Optional<UserAccount> userAccountOpt = userAccountRepository.findByUserId(userId.getValue());
        return userAccountOpt.map(account -> account.getPoints() >= points).orElse(false);
    }

    @Override
    public List<UserAccountLog> getAccountLogs(Long userId, Integer type, Integer limit) {
        return userAccountRepository.findAccountLogs(userId, type, limit);
    }

    @Override
    public List<UserPointsLog> getPointsLogs(Long userId, Integer type, Integer limit) {
        return userAccountRepository.findPointsLogs(userId, type, limit);
    }

    @Override
    public PageResult<UserAccountLog> getAccountLogsByUserId(UserId userId, PageRequest pageRequest) {
        // 这里需要在UserAccountRepository中添加相应的方法
        // 暂时返回空结果
        return new PageResult<>();
    }

    @Override
    public List<UserAccountLog> getAccountLogsByUserIdAndTypeAndSource(UserId userId, Integer type, Integer source, Integer limit) {
        // 这里需要在UserAccountRepository中添加相应的方法
        return userAccountRepository.findAccountLogs(userId.getValue(), type, limit);
    }

    /**
     * 获取用户账户，如果不存在则创建
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    private UserAccount getUserAccountOrCreate(UserId userId) {
        return getUserAccount(userId).orElseGet(() -> createUserAccount(userId));
    }

    /**
     * 获取用户账户，如果不存在则抛出异常
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    private UserAccount getUserAccountOrThrow(UserId userId) {
        return getUserAccount(userId)
                .orElseThrow(() -> new BusinessException("用户账户不存在"));
    }
    
    // BaseDomainService 接口实现
    @Override
    public boolean verifyPassword(String plainPassword, String encodedPassword, String salt) {
        return PASSWORD_ENCODER.matches(plainPassword + salt, encodedPassword);
    }

    @Override
    public String encryptPassword(String plainPassword, String salt) {
        return PASSWORD_ENCODER.encode(plainPassword + salt);
    }

    @Override
    public String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Override
    public boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    @Override
    public String generateVerifyCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
} 