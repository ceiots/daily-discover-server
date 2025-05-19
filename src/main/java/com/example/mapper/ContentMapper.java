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
    @Insert("INSERT INTO content (user_id, title, content, images, tags, status, created_at, updated_at, " +
            "view_count, like_count, comment_count, audit_status, audit_remark) " +
            "VALUES (#{userId}, #{title}, #{content}, #{images}, #{tags}, #{status}, #{createdAt}, #{updatedAt}, " +
            "#{viewCount}, #{likeCount}, #{commentCount}, #{auditStatus}, #{auditRemark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Content content);
    
    /**
     * 更新内容
     */
    @Update("UPDATE content SET title = #{title}, content = #{content}, images = #{images}, " +
            "tags = #{tags}, status = #{status}, updated_at = #{updatedAt}, view_count = #{viewCount}, " +
            "like_count = #{likeCount}, comment_count = #{commentCount}, audit_status = #{auditStatus}, " +
            "audit_remark = #{auditRemark} WHERE id = #{id}")
    void update(Content content);
    
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
    List<Content> findByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 查询所有已发布的内容
     */
    @Select("SELECT * FROM content WHERE status = 1 ORDER BY created_at DESC")
    List<Content> findAllPublished();
    
    /**
     * 查询所有已发布且审核通过的内容
     */
    @Select("SELECT * FROM content WHERE status = 1 AND audit_status = 1 ORDER BY created_at DESC")
    List<Content> findPublishedAndApproved();

    /**
     * 查询所有待审核的内容
     */
    @Select("SELECT * FROM content WHERE audit_status = 0 ORDER BY created_at DESC")
    List<Content> findPendingAudit();
    
    /**
     * 删除内容
     */
    @Delete("DELETE FROM content WHERE id = #{id}")
    void deleteById(Long id);
    
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
    
    @Select("SELECT * FROM content WHERE user_id = #{userId} AND audit_status = #{auditStatus} ORDER BY created_at DESC")
    List<Content> findByUserIdAndAuditStatus(Long userId, Integer auditStatus);
    
    @Select("SELECT * FROM content WHERE user_id = #{userId} AND status = #{status} AND audit_status = #{auditStatus} ORDER BY created_at DESC")
    List<Content> findByUserIdAndStatusAndAuditStatus(Long userId, Integer status, Integer auditStatus);

    @Select("SELECT * FROM content WHERE status = #{status} ORDER BY created_at DESC")
    List<Content> findByStatus(Integer status);
    
    @Select("SELECT * FROM content WHERE status = #{status} AND audit_status = #{auditStatus} ORDER BY created_at DESC")
    List<Content> findByStatusAndAuditStatus(Integer status, Integer auditStatus);
    
    @Select("SELECT * FROM content WHERE audit_status = #{auditStatus} ORDER BY created_at DESC")
    List<Content> findByAuditStatus(Integer auditStatus);
} 