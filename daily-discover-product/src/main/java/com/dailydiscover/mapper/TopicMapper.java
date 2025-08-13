package com.dailydiscover.mapper;

import com.dailydiscover.model.Topic;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TopicMapper {
    
    // 查找所有话题
    @Select("SELECT * FROM topics")
    List<Topic> findAll();
    
    // 根据id查找话题
    @Select("SELECT * FROM topics WHERE id = #{id}")
    Topic findById(Long id);
    
    // 查找所有活跃话题按热度降序
    @Select("SELECT * FROM topics WHERE is_active = true ORDER BY heat DESC")
    List<Topic> findByIsActiveTrueOrderByHeatDesc();
    
    // 根据趋势查找话题
    @Select("SELECT * FROM topics WHERE trend = #{trend} AND is_active = true")
    List<Topic> findByTrendAndIsActiveTrue(@Param("trend") String trend);
    
    // 搜索话题（按标题或描述）
    @Select("SELECT * FROM topics WHERE is_active = true AND (title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))")
    List<Topic> searchActiveTopics(@Param("keyword") String keyword);
    
    // 插入话题
    @Insert("INSERT INTO topics (title, heat, trend, icon, description, related_product_id, is_active, created_at, updated_at) " +
            "VALUES (#{title}, #{heat}, #{trend}, #{icon}, #{description}, #{relatedProduct.id}, #{isActive}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Topic topic);
    
    // 更新话题
    @Update("UPDATE topics SET title = #{title}, heat = #{heat}, trend = #{trend}, icon = #{icon}, " +
            "description = #{description}, related_product_id = #{relatedProduct.id}, " +
            "is_active = #{isActive}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Topic topic);
    
    // 删除话题
    @Delete("DELETE FROM topics WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}