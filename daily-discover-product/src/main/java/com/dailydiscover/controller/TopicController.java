package com.dailydiscover.controller;

import com.dailydiscover.model.Product;
import com.dailydiscover.model.Topic;
import com.dailydiscover.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/topics")
public class TopicController {
    
    @Autowired
    private TopicService topicService;
    
    // 获取热点话题
    @GetMapping("/hot")
    public List<Map<String, Object>> getHotTopics() {
        List<Topic> topics = topicService.getHotTopics();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Topic topic : topics) {
            Map<String, Object> topicData = new HashMap<>();
            topicData.put("id", topic.getId());
            topicData.put("title", topic.getTitle());
            topicData.put("heat", topic.getHeat());
            topicData.put("trend", topic.getTrend());
            topicData.put("icon", topic.getIcon());
            topicData.put("description", topic.getDescription());
            
            // 获取相关产品
            Product relatedProduct = topic.getRelatedProduct();
            if (relatedProduct == null) {
                relatedProduct = topicService.getRandomRelatedProduct();
            }
            
            if (relatedProduct != null) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("title", relatedProduct.getTitle());
                productData.put("price", relatedProduct.getPrice());
                productData.put("image", relatedProduct.getImageUrl());
                topicData.put("relatedProduct", productData);
            } else {
                // 如果没有相关产品，创建一个默认的产品数据
                Map<String, Object> defaultProduct = new HashMap<>();
                defaultProduct.put("title", "暂无相关产品");
                defaultProduct.put("price", "¥0");
                defaultProduct.put("image", "https://via.placeholder.com/50x50/cccccc/ffffff?text=暂无图片");
                topicData.put("relatedProduct", defaultProduct);
            }
            
            result.add(topicData);
        }
        
        return result;
    }
    
    // 根据ID获取话题详情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTopicById(@PathVariable Long id) {
        Topic topic = topicService.getTopicById(id);
        if (topic != null) {
            Map<String, Object> topicData = new HashMap<>();
            Topic t = topic;
            topicData.put("id", t.getId());
            topicData.put("title", t.getTitle());
            topicData.put("heat", t.getHeat());
            topicData.put("trend", t.getTrend());
            topicData.put("icon", t.getIcon());
            topicData.put("description", t.getDescription());
            
            // 获取相关产品
            Product relatedProduct = t.getRelatedProduct();
            if (relatedProduct != null) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("id", relatedProduct.getId());
                productData.put("title", relatedProduct.getTitle());
                productData.put("price", relatedProduct.getPrice());
                productData.put("image", relatedProduct.getImageUrl());
                productData.put("tag", relatedProduct.getTag());
                topicData.put("relatedProduct", productData);
            }
            
            return ResponseEntity.ok(topicData);
        }
        return ResponseEntity.notFound().build();
    }
    
    // 创建新话题
    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        Topic newTopic = topicService.createTopic(topic);
        return ResponseEntity.ok(newTopic);
    }
    
    // 更新话题
    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestBody Topic topicDetails) {
        try {
            Topic updatedTopic = topicService.updateTopic(id, topicDetails);
            return ResponseEntity.ok(updatedTopic);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 删除话题
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        try {
            topicService.deleteTopic(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 根据趋势获取话题
    @GetMapping("/trend/{trend}")
    public List<Topic> getTopicsByTrend(@PathVariable String trend) {
        return topicService.getTopicsByTrend(trend);
    }
    
    // 搜索话题
    @GetMapping("/search")
    public List<Topic> searchTopics(@RequestParam String keyword) {
        return topicService.searchTopics(keyword);
    }
}