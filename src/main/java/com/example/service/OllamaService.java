package com.example.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;
import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class OllamaService {

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.api.key}")
    private String ollamaApiKey;

    @Value("${ollama.model}")
    private String ollamaModel;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OllamaService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(ollamaApiUrl).build();
        this.objectMapper = objectMapper;
    }
    

    /**
     * 生成文本（非流式），用于生成推荐话题等短文本内容
     */
    public String generateText(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            // 设置较小的温度值，使输出更加确定性
            requestBody.put("temperature", 0.3);
            // 设置较小的top_p值，减少随机性
            requestBody.put("top_p", 0.7);
            // 设置较小的chunk_size，确保输出格式正确
            requestBody.put("options", Map.of("chunk_size", 10));
            
            log.info("调用Ollama生成文本，提示词: {}", prompt);
            
            String response = webClient.post()
                .uri(ollamaApiUrl + "/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            String generatedText = (String) responseMap.get("response");
            
            log.info("Ollama生成文本成功，: {}", generatedText);
            
            return generatedText;
        } catch (Exception e) {
            log.error("调用Ollama生成文本失败", e);
            return "";
        }
    }

    /**
     * 使用WebSocket进行流式聊天
     */
    public void streamChatWithWebSocket(String prompt, Consumer<String> onTextReceived, 
                                     Consumer<Throwable> onError, Runnable onComplete) {
        try {
            // 准备请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", true);
            
            // 设置较小的chunk_size，确保小数据块输出
            Map<String, Object> options = new HashMap<>();
            options.put("chunk_size", 100);
            requestBody.put("options", options);
            
            log.info("调用Ollama流式聊天，提示词: {}", prompt);
            
            // 发送请求并处理流式响应
            webClient.post()
                .uri(ollamaApiUrl + "/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(chunk -> {
                    try {
                        // 解析JSON响应
                        Map<String, Object> responseMap = objectMapper.readValue(chunk, Map.class);
                        String text = (String) responseMap.get("response");
                        
                        if (text != null && !text.isEmpty()) {
                            // 调用回调函数处理文本块
                            onTextReceived.accept(text);
                        }
                        
                        // 检查是否完成
                        Boolean done = (Boolean) responseMap.get("done");
                        if (Boolean.TRUE.equals(done)) {
                            return Mono.empty();
                        }
                        
                        return Mono.just(chunk);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .limitRate(1, 0)  // 禁用预取，确保每个数据块单独处理
                .delayElements(Duration.ofMillis(10))  // 添加小延迟确保数据流平滑
                .doOnComplete(onComplete)
                .doOnError(onError)
                .subscribe(
                    chunk -> {}, // 已在flatMap中处理
                    throwable -> {
                        log.error("流式聊天出错", throwable);
                        onError.accept(throwable);
                    },
                    () -> {
                        log.info("流式聊天完成");
                        onComplete.run();
                    }
                );
        } catch (Exception e) {
            log.error("初始化流式聊天失败", e);
            onError.accept(e);
        }
    }
    
    /**
     * 获取默认Ollama模型名称
     */
    public String getDefaultModel() {
        return this.ollamaModel;
    }
    
    /**
     * 获取Ollama API URL
     */
    public String getOllamaApiUrl() {
        return this.ollamaApiUrl;
    }
    
    /**
     * 获取WebClient实例
     */
    public WebClient getWebClient() {
        return this.webClient;
    }
}
