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
 * 用户收藏领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFavorite implements Serializable {
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
     * 收藏类型:1-商品,2-店铺,3-内容,4-活动
     */
    private Integer type;

    /**
     * 收藏对象ID
     */
    private Long targetId;

    /**
     * 收藏时间
     */
    private LocalDateTime collectTime;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建用户收藏
     *
     * @param userId 用户ID
     * @param type 收藏类型
     * @param targetId 收藏对象ID
     * @return 用户收藏
     */
    public static UserFavorite create(UserId userId, Integer type, Long targetId) {
        UserFavorite favorite = new UserFavorite();
        favorite.userId = userId;
        favorite.type = type;
        favorite.targetId = targetId;
        favorite.collectTime = LocalDateTime.now();
        favorite.createTime = LocalDateTime.now();
        return favorite;
    }

    /**
     * 设置备注
     *
     * @param note 备注
     */
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFavorite that = (UserFavorite) o;
        return Objects.equals(id, that.id) || 
               (Objects.equals(userId, that.userId) && 
                Objects.equals(type, that.type) && 
                Objects.equals(targetId, that.targetId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type, targetId);
    }
}