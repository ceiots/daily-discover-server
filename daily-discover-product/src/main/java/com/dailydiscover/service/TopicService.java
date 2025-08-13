package com.dailydiscover.service;

import com.dailydiscover.model.Product;
import com.dailydiscover.model.Topic;
import com.dailydiscover.mapper.TopicMapper;
import com.dailydiscover.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TopicService {
    
    @Autowired
    private TopicMapper topicMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    // 获取所有热点话题
    public List<Topic> getHotTopics() {
        return topicMapper.findByIsActiveTrueOrderByHeatDesc();
    }
    
    // 根据ID获取话题
    public Topic getTopicById(Long id) {
        return topicMapper.findById(id);
    }
    
    // 创建新话题
    public Topic createTopic(Topic topic) {
        topicMapper.insert(topic);
        return topic;
    }
    
    // 更新话题
    public Topic updateTopic(Long id, Topic topicDetails) {
        Topic topic = topicMapper.findById(id);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }
        
        topic.setTitle(topicDetails.getTitle());
        topic.setHeat(topicDetails.getHeat());
        topic.setTrend(topicDetails.getTrend());
        topic.setIcon(topicDetails.getIcon());
        topic.setDescription(topicDetails.getDescription());
        topic.setRelatedProduct(topicDetails.getRelatedProduct());
        topic.setUpdatedAt(java.time.LocalDateTime.now());
        
        topicMapper.update(topic);
        return topic;
    }
    
    // 删除话题
    public void deleteTopic(Long id) {
        Topic topic = topicMapper.findById(id);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }
        topic.setIsActive(false);
        topic.setUpdatedAt(java.time.LocalDateTime.now());
        topicMapper.update(topic);
    }
    
    // 获取话题的随机相关产品
    public Product getRandomRelatedProduct() {
        List<Product> allProducts = productMapper.findAllByIsActiveTrue();
        if (allProducts.isEmpty()) {
            return null;
        }
        
        Random random = new Random();
        return allProducts.get(random.nextInt(allProducts.size()));
    }
    
    // 根据趋势获取话题
    public List<Topic> getTopicsByTrend(String trend) {
        return topicMapper.findByTrendAndIsActiveTrue(trend);
    }
    
    // 搜索话题
    public List<Topic> searchTopics(String keyword) {
        return topicMapper.searchActiveTopics(keyword);
    }
}