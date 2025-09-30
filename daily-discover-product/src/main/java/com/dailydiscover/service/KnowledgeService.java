package com.dailydiscover.service;

import com.dailydiscover.model.KnowledgeEntity;
import java.util.List;

public interface KnowledgeService {
    
    // 获取今日知识
    List<KnowledgeEntity> getTodayKnowledge();
    
    // 根据ID获取知识详情
    KnowledgeEntity getKnowledgeById(Long id);
    
    // 增加浏览量
    boolean incrementViewCount(Long id);
    
    // 增加点赞数
    boolean incrementLikeCount(Long id);
    
    // 增加收藏数
    boolean incrementFavoriteCount(Long id);
}