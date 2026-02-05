package com.dailydiscover.service;

import com.dailydiscover.model.UserReview;
import java.util.List;

public interface UserReviewService {
    UserReview findById(Long id);
    List<UserReview> findByProductId(Long productId);
    List<UserReview> findByUserId(Long userId);
    void save(UserReview userReview);
    void update(UserReview userReview);
    void incrementHelpfulCount(Long id);
    void incrementLikeCount(Long id);
    void incrementReplyCount(Long id);
}