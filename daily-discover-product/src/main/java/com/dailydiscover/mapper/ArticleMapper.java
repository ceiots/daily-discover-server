package com.dailydiscover.mapper;

import com.dailydiscover.model.Article;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ArticleMapper {
    
    // 查找所有文章
    @Select("SELECT * FROM articles")
    List<Article> findAll();
    
    // 根据id查找文章
    @Select("SELECT * FROM articles WHERE id = #{id}")
    Article findById(Long id);
    
    // 查找所有活跃文章
    @Select("SELECT * FROM articles WHERE is_active = true")
    List<Article> findByIsActiveTrue();
    
    // 根据分类查找文章
    @Select("SELECT * FROM articles WHERE category = #{category} AND is_active = true")
    List<Article> findByCategoryAndIsActiveTrue(@Param("category") String category);
    
    // 搜索文章（按标题、副标题或内容）
    @Select("SELECT * FROM articles WHERE is_active = true AND (title LIKE CONCAT('%', #{keyword}, '%') OR subtitle LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%'))")
    List<Article> searchActiveArticles(@Param("keyword") String keyword);
    
    // 插入文章
    @Insert("INSERT INTO articles (title, subtitle, author, read_time, category, image, content, tags, is_active, created_at, updated_at) " +
            "VALUES (#{title}, #{subtitle}, #{author}, #{readTime}, #{category}, #{image}, #{content}, #{tags,typeHandler=com.dailydiscover.config.StringListTypeHandler}, #{isActive}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Article article);
    
    // 更新文章
    @Update("UPDATE articles SET title = #{title}, subtitle = #{subtitle}, author = #{author}, read_time = #{readTime}, " +
            "category = #{category}, image = #{image}, content = #{content}, tags = #{tags,typeHandler=com.dailydiscover.config.StringListTypeHandler}, " +
            "is_active = #{isActive}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Article article);
    
    // 删除文章
    @Delete("DELETE FROM articles WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}