package com.dailydiscover.service;

import com.dailydiscover.mapper.KnowledgeMapper;
import com.dailydiscover.model.KnowledgeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    
    @Autowired
    private KnowledgeMapper knowledgeMapper;
    
    @Override
    public List<KnowledgeEntity> getTodayKnowledge() {
        return knowledgeMapper.findTodayKnowledge();
    }
    
    @Override
    public KnowledgeEntity getKnowledgeById(Long id) {
        return knowledgeMapper.findById(id);
    }
    
    @Override
    public boolean incrementViewCount(Long id) {
        return knowledgeMapper.incrementViewCount(id) > 0;
    }
    
    @Override
    public boolean incrementLikeCount(Long id) {
        return knowledgeMapper.incrementLikeCount(id) > 0;
    }
    
    @Override
    public boolean incrementFavoriteCount(Long id) {
        return knowledgeMapper.incrementFavoriteCount(id) > 0;
    }
}