package com.example.user.api.controller;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.common.result.Result;
import com.example.user.api.vo.UserAccountLogVO;
import com.example.user.api.vo.UserAccountVO;
import com.example.user.application.dto.UserAccountDTO;
import com.example.user.application.dto.UserAccountLogDTO;
import com.example.user.application.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户账户控制器
 */
@Api(tags = "用户账户管理")
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @ApiOperation("获取用户账户信息")
    @GetMapping("/{userId}")
    public Result<UserAccountVO> getUserAccount(@PathVariable Long userId) {
        UserAccountDTO userAccountDTO = userAccountService.getUserAccount(userId);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("充值")
    @PostMapping("/{userId}/recharge")
    public Result<UserAccountVO> recharge(
            @PathVariable Long userId,
            @ApiParam("金额") @RequestParam BigDecimal amount,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        UserAccountDTO userAccountDTO = userAccountService.recharge(userId, amount, source, sourceId, description);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("消费")
    @PostMapping("/{userId}/consume")
    public Result<UserAccountVO> consume(
            @PathVariable Long userId,
            @ApiParam("金额") @RequestParam BigDecimal amount,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        UserAccountDTO userAccountDTO = userAccountService.consume(userId, amount, source, sourceId, description);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("退款")
    @PostMapping("/{userId}/refund")
    public Result<UserAccountVO> refund(
            @PathVariable Long userId,
            @ApiParam("金额") @RequestParam BigDecimal amount,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        UserAccountDTO userAccountDTO = userAccountService.refund(userId, amount, source, sourceId, description);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("提现")
    @PostMapping("/{userId}/withdraw")
    public Result<UserAccountVO> withdraw(
            @PathVariable Long userId,
            @ApiParam("金额") @RequestParam BigDecimal amount,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        UserAccountDTO userAccountDTO = userAccountService.withdraw(userId, amount, source, sourceId, description);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("冻结金额")
    @PostMapping("/{userId}/freeze")
    public Result<UserAccountVO> freezeAmount(
            @PathVariable Long userId,
            @ApiParam("金额") @RequestParam BigDecimal amount,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        UserAccountDTO userAccountDTO = userAccountService.freezeAmount(userId, amount, source, sourceId, description);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("解冻金额")
    @PostMapping("/{userId}/unfreeze")
    public Result<UserAccountVO> unfreezeAmount(
            @PathVariable Long userId,
            @ApiParam("金额") @RequestParam BigDecimal amount,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        UserAccountDTO userAccountDTO = userAccountService.unfreezeAmount(userId, amount, source, sourceId, description);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("调整账户余额")
    @PostMapping("/{userId}/adjust")
    public Result<UserAccountVO> adjustBalance(
            @PathVariable Long userId,
            @ApiParam("金额") @RequestParam BigDecimal amount,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        UserAccountDTO userAccountDTO = userAccountService.adjustBalance(userId, amount, description);
        UserAccountVO userAccountVO = convertToUserAccountVO(userAccountDTO);
        return Result.success(userAccountVO);
    }

    @ApiOperation("获取账户日志")
    @GetMapping("/{userId}/logs")
    public Result<PageResult<UserAccountLogVO>> getAccountLogs(
            @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("类型") @RequestParam(required = false) Integer type,
            @ApiParam("来源") @RequestParam(required = false) Integer source) {
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        PageResult<UserAccountLogDTO> pageResult = userAccountService.getAccountLogs(userId, pageRequest, type, source);
        
        List<UserAccountLogVO> userAccountLogVOList = pageResult.getList().stream()
                .map(this::convertToUserAccountLogVO)
                .collect(Collectors.toList());
        
        return Result.success(new PageResult<>(userAccountLogVOList, pageResult.getTotal(), pageResult.getPages(), pageResult.getPageNum(), pageResult.getPageSize()));
    }

    /**
     * 将UserAccountDTO转换为UserAccountVO
     */
    private UserAccountVO convertToUserAccountVO(UserAccountDTO userAccountDTO) {
        if (userAccountDTO == null) {
            return null;
        }
        UserAccountVO userAccountVO = new UserAccountVO();
        BeanUtils.copyProperties(userAccountDTO, userAccountVO);
        return userAccountVO;
    }

    /**
     * 将UserAccountLogDTO转换为UserAccountLogVO
     */
    private UserAccountLogVO convertToUserAccountLogVO(UserAccountLogDTO userAccountLogDTO) {
        if (userAccountLogDTO == null) {
            return null;
        }
        UserAccountLogVO userAccountLogVO = new UserAccountLogVO();
        BeanUtils.copyProperties(userAccountLogDTO, userAccountLogVO);
        return userAccountLogVO;
    }
} 