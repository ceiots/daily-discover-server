package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.entity.UserPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户发布内容Mapper接口
 */
@Mapper
public interface UserPostMapper extends BaseMapper<UserPost> {

    /**
     * 删除用户的所有发布内容
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 统计用户发布内容数量
     * 
     * @param userId 用户ID
     * @return 发布内容数量
     */
    int countByUserId(Long userId);

    /**
     * 更新点赞数
     * 
     * @param postId 发布内容ID
     * @param likesCount 点赞数
     * @return 影响行数
     */
    int updateLikesCount(Long postId, Integer likesCount);

    /**
     * 更新评论数
     * 
     * @param postId 发布内容ID
     * @param commentsCount 评论数
     * @return 影响行数
     */
    int updateCommentsCount(Long postId, Integer commentsCount);
}