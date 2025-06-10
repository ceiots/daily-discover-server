package com.example.user.api.controller;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.common.result.Result;
import com.example.user.api.vo.UserBehaviorVO;
import com.example.user.application.dto.UserBehaviorDTO;
import com.example.user.application.service.UserBehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户行为控制器
 */
@Api(tags = "用户行为管理")
@RestController
@RequestMapping("/api/v1/behaviors")
@RequiredArgsConstructor
public class UserBehaviorController {

    private final UserBehaviorService userBehaviorService;

    @ApiOperation("记录用户行为")
    @PostMapping
    public Result<UserBehaviorVO> recordBehavior(@RequestBody @Valid UserBehaviorVO userBehaviorVO) {
        UserBehaviorDTO userBehaviorDTO = convertToDTO(userBehaviorVO);
        UserBehaviorDTO recordedDTO = userBehaviorService.recordBehavior(userBehaviorDTO);
        return Result.success(convertToVO(recordedDTO));
    }

    @ApiOperation("获取用户行为列表")
    @GetMapping("/{userId}")
    public Result<List<UserBehaviorVO>> getUserBehaviors(
            @PathVariable Long userId,
            @ApiParam("行为类型") @RequestParam(required = false) Integer behaviorType,
            @ApiParam("目标类型") @RequestParam(required = false) Integer targetType,
            @ApiParam("限制数量") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<UserBehaviorDTO> behaviorDTOs = userBehaviorService.getUserBehaviors(userId, behaviorType, targetType, limit);
        List<UserBehaviorVO> behaviorVOs = behaviorDTOs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(behaviorVOs);
    }

    @ApiOperation("分页获取用户行为")
    @GetMapping("/{userId}/page")
    public Result<PageResult<UserBehaviorVO>> getUserBehaviorPage(
            @PathVariable Long userId,
            @ApiParam("行为类型") @RequestParam(required = false) Integer behaviorType,
            @ApiParam("目标类型") @RequestParam(required = false) Integer targetType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        PageResult<UserBehaviorDTO> pageResult = userBehaviorService.getUserBehaviorPage(
                userId, behaviorType, targetType, pageRequest);
        
        List<UserBehaviorVO> behaviorVOs = pageResult.getList().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(new PageResult<>(behaviorVOs, pageResult.getTotal(), 
                pageResult.getPages(), pageResult.getPageNum(), pageResult.getPageSize()));
    }

    @ApiOperation("统计目标对象行为")
    @GetMapping("/count")
    public Result<Long> countBehaviorByTarget(
            @ApiParam("目标ID") @RequestParam Long targetId,
            @ApiParam("目标类型") @RequestParam Integer targetType,
            @ApiParam("行为类型") @RequestParam Integer behaviorType) {
        
        Long count = userBehaviorService.countBehaviorByTarget(targetId, targetType, behaviorType);
        return Result.success(count);
    }

    @ApiOperation("检查用户是否有指定行为")
    @GetMapping("/check")
    public Result<Boolean> hasUserBehavior(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("目标ID") @RequestParam Long targetId,
            @ApiParam("目标类型") @RequestParam Integer targetType,
            @ApiParam("行为类型") @RequestParam Integer behaviorType) {
        
        boolean hasUserBehavior = userBehaviorService.hasUserBehavior(userId, targetId, targetType, behaviorType);
        return Result.success(hasUserBehavior);
    }

    @ApiOperation("删除用户行为")
    @DeleteMapping
    public Result<Boolean> deleteBehavior(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("目标ID") @RequestParam Long targetId,
            @ApiParam("目标类型") @RequestParam Integer targetType,
            @ApiParam("行为类型") @RequestParam Integer behaviorType) {
        
        boolean result = userBehaviorService.deleteBehavior(userId, targetId, targetType, behaviorType);
        return Result.success(result);
    }

    /**
     * 将VO转换为DTO
     */
    private UserBehaviorDTO convertToDTO(UserBehaviorVO userBehaviorVO) {
        if (userBehaviorVO == null) {
            return null;
        }
        UserBehaviorDTO userBehaviorDTO = new UserBehaviorDTO();
        BeanUtils.copyProperties(userBehaviorVO, userBehaviorDTO);
        return userBehaviorDTO;
    }

    /**
     * 将DTO转换为VO
     */
    private UserBehaviorVO convertToVO(UserBehaviorDTO userBehaviorDTO) {
        if (userBehaviorDTO == null) {
            return null;
        }
        UserBehaviorVO userBehaviorVO = new UserBehaviorVO();
        BeanUtils.copyProperties(userBehaviorDTO, userBehaviorVO);
        return userBehaviorVO;
    }
}