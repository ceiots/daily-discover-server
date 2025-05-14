package com.example.mapper;

import com.example.model.Content;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 内容Mapper接口
 */
@Mapper
public interface ContentMapper {
    
    /**
     * 插入内容
     */
    @Insert("INSERT INTO content (user_id, title, content, images, tags, status, created_at, updated_at, view_count, like_count, comment_count) " +
            "VALUES (#{userId}, #{title}, #{content}, #{images}, #{tags}, #{status}, #{createdAt}, #{updatedAt}, 0, 0, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Content content);
    
    /**
     * 更新内容
     */
    @Update("UPDATE content SET title = #{title}, content = #{content}, images = #{images}, " +
            "tags = #{tags}, status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Content content);
    
    /**
     * 根据ID查询内容
     */
    @Select("SELECT * FROM content WHERE id = #{id}")
    Content findById(Long id);
    
    /**
     * 根据用户ID查询内容列表
     */
    @Select("SELECT * FROM content WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Content> findByUserId(Long userId);
    
    /**
     * 根据用户ID和状态查询内容列表
     */
    @Select("SELECT * FROM content WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<Content> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 查询所有已发布的内容
     */
    @Select("SELECT * FROM content WHERE status = 1 ORDER BY created_at DESC")
    List<Content> findAllPublished();
    
    /**
     * 删除内容
     */
    @Delete("DELETE FROM content WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 增加浏览次数
     */
    @Update("UPDATE content SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);
    
    /**
     * 增加点赞次数
     */
    @Update("UPDATE content SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);
    
    /**
     * 增加评论次数
     */
    @Update("UPDATE content SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(Long id);
} 