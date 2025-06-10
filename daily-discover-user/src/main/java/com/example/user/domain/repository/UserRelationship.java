package com.example.user.domain.repository;

import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户关系领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRelationship implements Serializable {
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
     * 关联用户ID
     */
    private UserId relatedUserId;

    /**
     * 关系类型:1-好友,2-黑名单,3-特别关注
     */
    private Integer relationType;

    /**
     * 状态:0-解除,1-有效
     */
    private Integer status;

    /**
     * 备注
     */
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
     * 创建用户关系
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @return 用户关系
     */
    public static UserRelationship create(UserId userId, UserId relatedUserId, Integer relationType) {
        UserRelationship relationship = new UserRelationship();
        relationship.userId = userId;
        relationship.relatedUserId = relatedUserId;
        relationship.relationType = relationType;
        relationship.status = 1;
        relationship.createTime = LocalDateTime.now();
        relationship.updateTime = LocalDateTime.now();
        return relationship;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 解除关系
     */
    public void dissolve() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 恢复关系
     */
    public void restore() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否有效关系
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否好友关系
     *
     * @return 是否好友
     */
    public boolean isFriend() {
        return isValid() && this.relationType != null && this.relationType == 1;
    }

    /**
     * 是否黑名单关系
     *
     * @return 是否黑名单
     */
    public boolean isBlacklist() {
        return isValid() && this.relationType != null && this.relationType == 2;
    }

    /**
     * 是否特别关注关系
     *
     * @return 是否特别关注
     */
    public boolean isSpecialFollow() {
        return isValid() && this.relationType != null && this.relationType == 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRelationship that = (UserRelationship) o;
        return Objects.equals(id, that.id) || 
               (Objects.equals(userId, that.userId) && 
                Objects.equals(relatedUserId, that.relatedUserId) && 
                Objects.equals(relationType, that.relationType));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, relatedUserId, relationType);
    }
}