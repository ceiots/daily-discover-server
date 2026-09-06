package com.dailydiscover.behavior.repository;

import com.dailydiscover.behavior.domain.Behavior;
import com.dailydiscover.content.domain.Content;
import com.dailydiscover.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BehaviorRepository extends JpaRepository<Behavior, Long> {

    List<Behavior> findByUserIdAndContentId(Long userId, Long contentId);

    List<Behavior> findByUserIdAndBehaviorType(Long userId, String behaviorType);

    @Query("SELECT b FROM Behavior b WHERE b.user.id = :userId AND b.content.id IN :contentIds AND b.behaviorType = :type")
    List<Behavior> findByUserIdAndContentIdsAndType(@Param("userId") Long userId, @Param("contentIds") List<Long> contentIds, @Param("type") String type);

    @Query("SELECT COUNT(b) FROM Behavior b WHERE b.content.id = :contentId AND b.behaviorType = :type")
    long countByContentIdAndType(@Param("contentId") Long contentId, @Param("type") String type);

    @Query("SELECT b FROM Behavior b WHERE b.user.id = :userId AND b.createdAt >= :since ORDER BY b.createdAt DESC")
    List<Behavior> findRecentByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    boolean existsByUserIdAndContentIdAndBehaviorType(Long userId, Long contentId, String behaviorType);
}