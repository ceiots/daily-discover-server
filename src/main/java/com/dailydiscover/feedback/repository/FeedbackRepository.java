package com.dailydiscover.feedback.repository;

import com.dailydiscover.feedback.domain.Feedback;
import com.dailydiscover.content.domain.Content;
import com.dailydiscover.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUserIdAndContentId(Long userId, Long contentId);

    @Query("SELECT f FROM Feedback f WHERE f.user.id = :userId AND f.content.id IN :contentIds")
    List<Feedback> findByUserIdAndContentIds(@Param("userId") Long userId, @Param("contentIds") List<Long> contentIds);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.content.id = :contentId AND f.feedbackType = :type")
    long countByContentIdAndType(@Param("contentId") Long contentId, @Param("type") String type);

    Optional<Feedback> findByUserIdAndContentIdAndFeedbackType(Long userId, Long contentId, String feedbackType);

    boolean existsByUserIdAndContentIdAndFeedbackType(Long userId, Long contentId, String feedbackType);
}