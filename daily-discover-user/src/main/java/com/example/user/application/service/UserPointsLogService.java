package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserPointsLogDTO;

import java.util.List;

/**
 * 用户积分记录应用服务接口
 */
public interface UserPointsLogService extends BaseApplicationService {

    /**
     * 获取用户积分记录
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 积分记录分页结果
     */
    PageResult<UserPointsLogDTO> getPointsLogs(Long userId, PageRequest pageRequest);

    /**
     * 获取用户积分记录列表
     *
     * @param userId 用户ID
     * @param type 类型
     * @param limit 限制数量
     * @return 积分记录列表
     */
    List<UserPointsLogDTO> getPointsLogs(Long userId, Integer type, Integer limit);

    /**
     * 增加积分
     *
     * @param userId 用户ID
     * @param points 积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param description 描述
     * @return 积分记录DTO
     */
    UserPointsLogDTO addPoints(Long userId, Integer points, Integer source, String sourceId, String description);

    /**
     * 使用积分
     *
     * @param userId 用户ID
     * @param points 积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param description 描述
     * @return 积分记录DTO
     */
    UserPointsLogDTO usePoints(Long userId, Integer points, Integer source, String sourceId, String description);

    /**
     * 调整积分
     *
     * @param userId 用户ID
     * @param points 积分
     * @param description 描述
     * @return 积分记录DTO
     */
    UserPointsLogDTO adjustPoints(Long userId, Integer points, String description);

    /**
     * 查询用户总积分
     *
     * @param userId 用户ID
     * @return 总积分
     */
    Integer getTotalPoints(Long userId);

    /**
     * 查询用户可用积分
     *
     * @param userId 用户ID
     * @return 可用积分
     */
    Integer getAvailablePoints(Long userId);

    /**
     * 检查积分是否足够
     *
     * @param userId 用户ID
     * @param points 积分
     * @return 是否足够
     */
    boolean isPointsEnough(Long userId, Integer points);
}