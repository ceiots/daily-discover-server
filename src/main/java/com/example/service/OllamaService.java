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

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OllamaService {

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.api.key}")
    private String ollamaApiKey;

    private final WebClient webClient;

    public OllamaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(ollamaApiUrl).build();
    }

    /**
     * 生成文章的流式响应
     */
    public SseEmitter generateArticle(String prompt) {
        // 构造请求体
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "deepseek-r1");
        jsonBody.put("prompt", prompt);
        jsonBody.put("stream", true);

        SseEmitter emitter = new SseEmitter();

        try {
            // 发送 POST 请求
            Flux<String> responseFlux = webClient.post()
                .uri(ollamaApiUrl+"/generate")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ollamaApiKey)
                .bodyValue(jsonBody.toString())
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(response -> {
                    return Flux.fromIterable(Arrays.asList(response.split("\n")))
                            .filter(line -> !line.trim().isEmpty())
                            .map(line -> {
                                // 解析每一行 JSON 数据
                                // 假设每一行是一个 JSON 对象，并且包含 "response" 字段
                                // 你可以使用 Jackson 或其他 JSON 库来解析
                                // 这里简单返回原始字符串
                                return line;
                            });
                });

            // 异步处理逻辑
            responseFlux.subscribe(
                data -> {
                    try {
                        emitter.send(data); // 发送响应
                    } catch (IOException | IllegalStateException e) {
                        emitter.completeWithError(e);
                    }
                },
                error -> emitter.completeWithError(error),
                () -> emitter.complete()
            );

            return emitter;
        } catch (Exception e) {
            log.error("Error during API call: ", e);
            emitter.completeWithError(e);
            return emitter;
        }
    }
    
    /**
     * 非流式聊天，直接返回完整响应
     */
    public CompletableFuture<String> asyncChat(String prompt) {
        log.info("Sending chat request to Ollama: {}", prompt);
        
        // 构造请求体
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "deepseek-r1");
        jsonBody.put("prompt", prompt);
        jsonBody.put("stream", false);
        
        // 使用本地回退响应
        if (ollamaApiUrl == null || ollamaApiUrl.isEmpty()) {
            log.warn("Ollama API URL not configured, using fallback response");
            return CompletableFuture.completedFuture(generateFallbackResponse(prompt));
        }
        
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            // 发送 POST 请求
            webClient.post()
                .uri(ollamaApiUrl+"/generate")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ollamaApiKey)
                .bodyValue(jsonBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String result = jsonResponse.optString("response", "");
                            log.info("Received response from Ollama: {}", result.substring(0, Math.min(50, result.length())));
                            future.complete(result);
                        } catch (Exception e) {
                            log.error("Error parsing response", e);
                            future.complete(generateFallbackResponse(prompt));
                        }
                    },
                    error -> {
                        log.error("Error calling Ollama API", error);
                        future.complete(generateFallbackResponse(prompt));
                    }
                );
        } catch (Exception e) {
            log.error("Error during API call", e);
            future.complete(generateFallbackResponse(prompt));
        }
        
        return future;
    }
    
    /**
     * 生成本地回退响应（当Ollama服务不可用时）
     */
    private String generateFallbackResponse(String prompt) {
        String promptLower = prompt.toLowerCase();
        
        // 关键词匹配，提供基本回复
        if (promptLower.contains("推荐") || promptLower.contains("好物")) {
            return "我为您精选了今日好物，您可以在\"每日发现\"区域查看更多推荐商品。这些商品都是根据最新趋势和品质精选的。";
        } else if (promptLower.contains("天气")) {
            return "今天天气晴朗，气温在18-25°C之间，非常适合户外活动。建议您适当增减衣物，注意防晒。";
        } else if (promptLower.contains("健康") || promptLower.contains("饮食")) {
            return "建议您保持均衡饮食，多摄入蔬果，每天喝足够的水，保持适度运动，这对身体健康非常有益。";
        } else if (promptLower.contains("效率") || promptLower.contains("工作")) {
            return "提高工作效率可以尝试番茄工作法，设定明确目标，减少干扰，定期休息，确保充足睡眠。";
        }
        
        // 通用回复
        return "感谢您的提问。您对" + prompt + "的问题很有价值。我们会不断改进服务，为您提供更准确的信息。您还有其他问题吗？";
    }
}
