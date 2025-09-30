package com.dailydiscover.mapper;

import com.dailydiscover.model.KnowledgeEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KnowledgeMapper {
    
    @Select("SELECT * FROM daily_knowledge WHERE is_active = true ORDER BY created_at DESC")
    List<KnowledgeEntity> findTodayKnowledge();
    
    @Select("SELECT * FROM daily_knowledge WHERE is_active = true AND DATE(created_at) = CURDATE() ORDER BY created_at DESC")
    List<KnowledgeEntity> findKnowledgeByDate();
    
    @Select("SELECT * FROM daily_knowledge WHERE id = #{id}")
    KnowledgeEntity findById(Long id);
    
    @Insert("INSERT INTO daily_knowledge (title, category, concept, detail, application, difficulty, read_time, tags, icon, color_scheme, image_url, is_active, view_count, like_count, favorite_count, created_at, updated_at) " +
            "VALUES (#{title}, #{category}, #{concept}, #{detail}, #{application}, #{difficulty}, #{readTime}, #{tags}, #{icon}, #{colorScheme}, #{imageUrl}, #{isActive}, #{viewCount}, #{likeCount}, #{favoriteCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KnowledgeEntity knowledge);
    
    @Update("UPDATE daily_knowledge SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);
    
    @Update("UPDATE daily_knowledge SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);
    
    @Update("UPDATE daily_knowledge SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(Long id);
}