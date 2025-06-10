package com.example.user.domain.model.user;

import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户个人资料领域模型
 */
@Getter
public class UserProfile {

    /**
     * 主键ID
     */
    @Setter(AccessLevel.PACKAGE)
    private Long id;

    /**
     * 用户ID
     */
    private final UserId userId;

    /**
     * 昵称
     */
    @Setter
    private String nickname;

    /**
     * 真实姓名
     */
    @Setter
    private String realName;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @Setter
    private Integer gender;

    /**
     * 生日
     */
    @Setter
    private LocalDate birthday;

    /**
     * 头像URL
     */
    @Setter
    private String avatar;

    /**
     * 封面图片
     */
    @Setter
    private String coverImage;

    /**
     * 个人简介
     */
    @Setter
    private String bio;

    /**
     * 所在地区
     */
    @Setter
    private String region;

    /**
     * 职业
     */
    @Setter
    private String profession;

    /**
     * 兴趣爱好
     */
    @Setter
    private String hobbies;

    /**
     * 个人主页
     */
    @Setter
    private String homepage;

    /**
     * 创建时间
     */
    private final LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Setter
    private LocalDateTime updateTime;

    /**
     * 私有构造函数，通过工厂方法创建实例
     */
    private UserProfile(UserId userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
        this.gender = 0; // 默认未知
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
    }

    /**
     * 创建用户资料
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @return 用户资料
     */
    public static UserProfile create(UserId userId, String nickname) {
        return new UserProfile(userId, nickname);
    }

    /**
     * 更新资料
     */
    public void update() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 设置ID
     */
    public void setId(Long id) {
        this.id = id;
    }
} 