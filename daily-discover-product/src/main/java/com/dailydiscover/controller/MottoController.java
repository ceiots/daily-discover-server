package com.dailydiscover.controller;

import com.dailydiscover.common.ApiResponse;
import com.dailydiscover.model.MottoEntity;
import com.dailydiscover.service.MottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/mottos")
public class MottoController {

    @Autowired
    private MottoService mottoService;
    
    // 获取今日格言
    @GetMapping("/today")
    public ApiResponse<Map<String, Object>> getTodayMotto() {
        MottoEntity motto = mottoService.getTodayMotto();
        Map<String, Object> mottoData = new HashMap<>();
        mottoData.put("id", motto.getId());
        mottoData.put("text", motto.getText());
        mottoData.put("author", "每日发现");
        mottoData.put("date", motto.getDate());
        
        return ApiResponse.success(mottoData);
    }
    
    // 获取所有格言
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAllMottos() {
        List<MottoEntity> mottos = mottoService.getAllMottos();
        List<Map<String, Object>> result = mottos.stream()
            .map(motto -> {
                Map<String, Object> mottoData = new HashMap<>();
                mottoData.put("id", motto.getId());
                mottoData.put("text", motto.getText());
                mottoData.put("author", "每日发现");
                mottoData.put("date", motto.getDate());
                return mottoData;
            })
            .collect(Collectors.toList());
        
        return ApiResponse.success(result);
    }
    
    // 根据ID获取格言
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getMottoById(@PathVariable Long id) {
        MottoEntity motto = mottoService.getMottoById(id);
        if (motto != null) {
            Map<String, Object> mottoData = new HashMap<>();
            mottoData.put("id", motto.getId());
            mottoData.put("text", motto.getText());
            mottoData.put("author", "每日发现");
            mottoData.put("date", motto.getDate());
            return ApiResponse.success(mottoData);
        }
        return ApiResponse.error(404, "格言不存在");
    }
    
    // 创建格言
    @PostMapping
    public ApiResponse<Map<String, Object>> createMotto(@RequestBody Map<String, Object> mottoData) {
        MottoEntity motto = new MottoEntity();
        motto.setText((String) mottoData.get("content"));
        motto.setDate((String) mottoData.get("date"));
        
        MottoEntity createdMotto = mottoService.createMotto(motto);
        if (createdMotto != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", createdMotto.getId());
            result.put("text", createdMotto.getText());
            result.put("author", "每日发现");
            result.put("date", createdMotto.getDate());
            return ApiResponse.success(result);
        }
        return ApiResponse.error(500, "创建格言失败");
    }
}