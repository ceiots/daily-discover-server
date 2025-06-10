package com.example.user.domain.model;

import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户行为领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBehavior implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Setter
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

    /**
     * 行为类型:1-浏览,2-点赞,3-收藏,4-评论,5-分享,6-购买,7-搜索
     */
    private Integer behaviorType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 目标类型:1-内容,2-商品,3-用户,4-话题,5-评论
     */
    private Integer targetType;

    /**
     * 行为时间
     */
    private LocalDateTime behaviorTime;

    /**
     * 设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC
     */
    private Integer deviceType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 行为数据
     */
    private String behaviorData;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建用户行为
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param deviceType 设备类型
     * @param deviceId 设备ID
     * @param ip IP地址
     * @return 用户行为
     */
    public static UserBehavior create(UserId userId, Integer behaviorType, Long targetId, Integer targetType,
                                      Integer deviceType, String deviceId, String ip) {
        UserBehavior behavior = new UserBehavior();
        behavior.userId = userId;
        behavior.behaviorType = behaviorType;
        behavior.targetId = targetId;
        behavior.targetType = targetType;
        behavior.behaviorTime = LocalDateTime.now();
        behavior.deviceType = deviceType;
        behavior.deviceId = deviceId;
        behavior.ip = ip;
        behavior.createTime = LocalDateTime.now();
        return behavior;
    }

    /**
     * 设置行为数据
     *
     * @param behaviorData 行为数据
     */
    public void setBehaviorData(String behaviorData) {
        this.behaviorData = behaviorData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBehavior that = (UserBehavior) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}