package com.example.user.api.controller;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.common.result.Result;
import com.example.user.api.vo.UserPointsLogVO;
import com.example.user.application.dto.UserPointsLogDTO;
import com.example.user.application.service.MemberService;
import com.example.user.application.dto.PointsLogDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户积分记录控制器
 */
@Api(tags = "用户积分记录管理")
@RestController
@RequestMapping("/api/v1/points/logs")
@RequiredArgsConstructor
public class UserPointsLogController {

    private final MemberService memberService;

    @ApiOperation("获取用户积分记录")
    @GetMapping("/{userId}")
    public Result<PageResult<UserPointsLogVO>> getPointsLogs(
            @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        
        // 通过会员服务获取积分记录
        PageResult<PointsLogDTO> pageResult = memberService.getPointsLogs(userId, pageRequest);
        
        // 转换为VO
        List<UserPointsLogVO> userPointsLogVOList = pageResult.getList().stream()
                .map(this::convertToUserPointsLogVO)
                .collect(Collectors.toList());
        
        return Result.success(new PageResult<UserPointsLogVO>(pageResult.getPageNum(), pageResult.getPageSize(), 
                pageResult.getTotal(), userPointsLogVOList));
    }

    @ApiOperation("增加积分")
    @PostMapping("/{userId}/add")
    public Result<Boolean> addPoints(
            @PathVariable Long userId,
            @ApiParam("积分") @RequestParam Integer points,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        
        memberService.addPoints(userId, points, source, sourceId);
        return Result.success(true);
    }

    @ApiOperation("使用积分")
    @PostMapping("/{userId}/use")
    public Result<Boolean> usePoints(
            @PathVariable Long userId,
            @ApiParam("积分") @RequestParam Integer points,
            @ApiParam("来源") @RequestParam Integer source,
            @ApiParam("来源ID") @RequestParam(required = false) String sourceId,
            @ApiParam("描述") @RequestParam(required = false) String description) {
        
        memberService.usePoints(userId, points, source, sourceId);
        return Result.success(true);
    }

    /**
     * 将PointsLogDTO转换为UserPointsLogVO
     */
    private UserPointsLogVO convertToUserPointsLogVO(PointsLogDTO pointsLogDTO) {
        if (pointsLogDTO == null) {
            return null;
        }
        UserPointsLogVO userPointsLogVO = new UserPointsLogVO();
        BeanUtils.copyProperties(pointsLogDTO, userPointsLogVO);
        return userPointsLogVO;
    }
}