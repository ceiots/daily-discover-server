package com.dailydiscover.controller;

import com.dailydiscover.model.KnowledgeEntity;
import com.dailydiscover.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@CrossOrigin(origins = "*")
public class KnowledgeController {
    
    @Autowired
    private KnowledgeService knowledgeService;
    
    /**
     * 获取今日知识
     */
    @GetMapping("/today")
    public ResponseEntity<List<KnowledgeEntity>> getTodayKnowledge() {
        try {
            List<KnowledgeEntity> knowledgeList = knowledgeService.getTodayKnowledge();
            return ResponseEntity.ok(knowledgeList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 根据ID获取知识详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeEntity> getKnowledgeById(@PathVariable Long id) {
        try {
            KnowledgeEntity knowledge = knowledgeService.getKnowledgeById(id);
            if (knowledge != null) {
                return ResponseEntity.ok(knowledge);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 增加浏览量
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        try {
            boolean success = knowledgeService.incrementViewCount(id);
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 增加点赞数
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> incrementLikeCount(@PathVariable Long id) {
        try {
            boolean success = knowledgeService.incrementLikeCount(id);
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 增加收藏数
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> incrementFavoriteCount(@PathVariable Long id) {
        try {
            boolean success = knowledgeService.incrementFavoriteCount(id);
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}