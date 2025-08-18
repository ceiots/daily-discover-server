package com.dailydiscover.controller;

import com.dailydiscover.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/mottos")
public class MottoController {
    
    // 获取今日格言
    @GetMapping("/today")
    public ApiResponse<Map<String, Object>> getTodayMotto() {
        // 模拟返回今日格言数据
        Map<String, Object> mottoData = new HashMap<>();
        mottoData.put("id", 1L);
        mottoData.put("content", "每一天都是一个新的开始，珍惜当下，创造未来。");
        mottoData.put("author", "佚名");
        mottoData.put("date", java.time.LocalDate.now().toString());
        
        return ApiResponse.success(mottoData);
    }
    
    // 获取所有格言
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAllMottos() {
        // 模拟返回所有格言数据
        List<Map<String, Object>> mottos = Arrays.asList(
            createMotto(1L, "每一天都是一个新的开始，珍惜当下，创造未来。", "佚名"),
            createMotto(2L, "成功不是终点，失败不是终结，继续前进的勇气才是最重要的。", "温斯顿·丘吉尔"),
            createMotto(3L, "生活就像骑自行车，想保持平衡就得往前走。", "阿尔伯特·爱因斯坦")
        );
        
        return ApiResponse.success(mottos);
    }
    
    // 根据ID获取格言
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getMottoById(@PathVariable Long id) {
        // 模拟根据ID获取格言
        Map<String, Object> motto = createMotto(id, "每一天都是一个新的开始，珍惜当下，创造未来。", "佚名");
        return ApiResponse.success(motto);
    }
    
    // 创建格言
    @PostMapping
    public ApiResponse<Map<String, Object>> createMotto(@RequestBody Map<String, Object> mottoData) {
        // 模拟创建格言
        mottoData.put("id", System.currentTimeMillis());
        mottoData.put("createdAt", java.time.LocalDateTime.now().toString());
        return ApiResponse.success(mottoData);
    }
    
    // 辅助方法：创建格言数据
    private Map<String, Object> createMotto(Long id, String content, String author) {
        Map<String, Object> motto = new HashMap<>();
        motto.put("id", id);
        motto.put("content", content);
        motto.put("author", author);
        motto.put("date", java.time.LocalDate.now().toString());
        return motto;
    }
}