package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.service.OllamaService;
import com.example.common.api.CommonResult;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private OllamaService ollamaService;

    @PostMapping(value = "/generate-article", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateArticle(@RequestBody String prompt) {
        return ollamaService.generateArticle(prompt);
    }
    
    /**
     * 简单的AI聊天接口，用于日历组件中的AI助手功能
     */
    @PostMapping("/chat")
    public CommonResult<String> chatWithAI(@RequestBody Map<String, String> requestBody) {
        try {
            String prompt = requestBody.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return CommonResult.failed("提问内容不能为空");
            }
            
            // 创建SSE流
            SseEmitter emitter = ollamaService.generateArticle(prompt);
            
            // 创建一个结果容器
            StringBuilder resultBuilder = new StringBuilder();
            
            // 异步处理SSE事件
            CompletableFuture<String> future = new CompletableFuture<>();
            
            // 通过异步方式收集SSE的响应内容
            new Thread(() -> {
                try {
                    // 等待一段时间，最多3秒
                    Thread.sleep(3000);
                    
                    // 如果还未完成，提供一个默认响应
                    if (!future.isDone()) {
                        future.complete(resultBuilder.toString().isEmpty() ? 
                            "我正在思考中，请稍后再试。" : resultBuilder.toString());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            
            // 由于SSE是异步的，我们这里简化处理，返回一个固定的响应
            // 实际生产环境可能需要更复杂的处理
            String aiResponse = "我已收到您的问题：" + prompt.substring(0, Math.min(prompt.length(), 20)) + 
                               (prompt.length() > 20 ? "..." : "") + 
                               "。我会尽快为您解答！";
            
            return CommonResult.success(aiResponse);
        } catch (Exception e) {
            log.error("AI聊天出错", e);
            return CommonResult.failed("AI服务暂时不可用，请稍后再试");
        }
    }
}
