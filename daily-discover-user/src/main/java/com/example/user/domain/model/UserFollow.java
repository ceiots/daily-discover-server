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
 * 用户关注领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow implements Serializable {
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
     * 被关注用户ID
     */
    private UserId followUserId;

    /**
     * 状态:0-取消,1-有效
     */
    private Integer status;

    /**
     * 备注
     */
    @Setter
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建用户关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 用户关注
     */
    public static UserFollow create(UserId userId, UserId followUserId) {
        return create(userId, followUserId, null);
    }

    /**
     * 创建用户关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @param remark 备注
     * @return 用户关注
     */
    public static UserFollow create(UserId userId, UserId followUserId, String remark) {
        UserFollow userFollow = new UserFollow();
        userFollow.userId = userId;
        userFollow.followUserId = followUserId;
        userFollow.status = 1;
        userFollow.remark = remark;
        userFollow.createTime = LocalDateTime.now();
        userFollow.updateTime = LocalDateTime.now();
        return userFollow;
    }

    /**
     * 取消关注
     */
    public void cancel() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 恢复关注
     */
    public void restore() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否有效关注
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return this.status != null && this.status == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFollow that = (UserFollow) o;
        return Objects.equals(id, that.id) || 
               (Objects.equals(userId, that.userId) && Objects.equals(followUserId, that.followUserId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, followUserId);
    }
}