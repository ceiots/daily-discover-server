package com.dailydiscover.content.repository;

import com.dailydiscover.content.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findByIdAndStatus(Long id, String status);

    @Query("SELECT c FROM Content c WHERE c.status = :status AND c.publishAt <= :now AND (c.expireAt IS NULL OR c.expireAt >= :now) ORDER BY c.priority DESC, c.publishAt DESC")
    List<Content> findPublishedContents(@Param("status") String status, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Content c WHERE c.status = :status AND c.publishAt <= :now AND (c.expireAt IS NULL OR c.expireAt >= :now) ORDER BY c.priority DESC, c.publishAt DESC")
    List<Content> findPublishedContentsPaged(@Param("status") String status, @Param("now") LocalDateTime now, org.springframework.data.domain.Pageable pageable);

    List<Content> findByStatusOrderByPriorityDescPublishAtDesc(String status);

    @Query("SELECT c FROM Content c JOIN c.scenes s WHERE s.id = :sceneId AND c.status = :status AND c.publishAt <= :now AND (c.expireAt IS NULL OR c.expireAt >= :now) ORDER BY c.priority DESC, c.publishAt DESC")
    List<Content> findBySceneIdAndStatus(@Param("sceneId") Long sceneId, @Param("status") String status, @Param("now") LocalDateTime now);
}