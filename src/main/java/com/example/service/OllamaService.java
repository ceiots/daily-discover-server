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
        jsonBody.put("model", "qwen3:4b");
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
     * 流式聊天API，支持回调处理
     * @param prompt 用户问题
     * @param onNext 每次接收到数据的回调
     * @param onError 错误处理回调
     * @param onComplete 完成时的回调
     */
    public void streamChat(String prompt, Consumer<String> onNext, Consumer<Throwable> onError, Runnable onComplete) {
        log.info("Starting stream chat with prompt: {}", prompt);
        
        // 构造请求体
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "qwen3:4b");
        jsonBody.put("prompt", prompt);
        jsonBody.put("stream", true);
        
        // 使用本地回退响应
        if (ollamaApiUrl == null || ollamaApiUrl.isEmpty()) {
            log.warn("Ollama API URL not configured, using fallback response");
            onNext.accept(generateFallbackResponse(prompt));
            onComplete.run();
            return;
        }
        
        try {
            StringBuilder completeResponse = new StringBuilder();
            
            // 发送 POST 请求
            webClient.post()
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
                                try {
                                    JSONObject jsonResponse = new JSONObject(line);
                                    // 获取原始响应内容
                                    String content = jsonResponse.optString("response", "");
                                    // 确保不包含event:message前缀
                                    if (content.contains("event:message")) {
                                        content = content.replaceAll("event:message[^\\s]*", "");
                                    }
                                    System.out.println(line + " content: " + content);
                                    return content;
                                } catch (Exception e) {
                                    log.warn("Failed to parse streaming response JSON: {}", line);
                                    return "";
                                }
                            })
                            .filter(text -> !text.isEmpty());
                })
                .subscribe(
                    chunk -> {
                        try {
                            completeResponse.append(chunk);
                            // 确保不包含event:message前缀
                            String cleanChunk = chunk;
                            if (cleanChunk.contains("event:message")) {
                                cleanChunk = cleanChunk.replaceAll("event:message[^\\s]*", "");
                            }
                            onNext.accept(cleanChunk);
                        } catch (Exception e) {
                            log.error("Error processing streaming response chunk", e);
                            onError.accept(e);
                        }
                    },
                    error -> {
                        log.error("Error during streaming API call", error);
                        if (completeResponse.length() > 0) {
                            // 如果已经有部分响应，则发送完整响应
                            onNext.accept(completeResponse.toString());
                            onComplete.run();
                        } else {
                            // 否则发送回退响应
                            onNext.accept(generateFallbackResponse(prompt));
                            onComplete.run();
                        }
                    },
                    () -> {
                        log.info("Streaming chat completed");
                        onComplete.run();
                    }
                );
        } catch (Exception e) {
            log.error("Error initiating streaming API call", e);
            onNext.accept(generateFallbackResponse(prompt));
            onComplete.run();
        }
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

    /**
     * 创建SSE流式聊天响应
     * @param prompt 用户问题
     * @param emitter SSE发射器
     */
    public void streamChatSSE(String prompt, SseEmitter emitter) {
        log.info("Starting SSE stream chat with prompt: {}", prompt);
        
        // 构造请求体
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "qwen3:4b");
        jsonBody.put("prompt", prompt);
        jsonBody.put("stream", true);
        
        // 检查API URL配置
        if (ollamaApiUrl == null || ollamaApiUrl.isEmpty()) {
            log.warn("Ollama API URL not configured, using default URL");
            ollamaApiUrl = "http://localhost:11434/api"; // 设置默认API地址
        }
        
        System.out.println("使用大模型API: " + ollamaApiUrl);
        
        try {
            // 发送 POST 请求
            webClient.post()
                .uri(ollamaApiUrl+"/generate")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, ollamaApiKey != null ? "Bearer " + ollamaApiKey : "")
                .bodyValue(jsonBody.toString())
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(response -> {
                    System.out.println("收到API响应: " + response);
                    return Flux.fromIterable(Arrays.asList(response.split("\n")))
                            .filter(line -> !line.trim().isEmpty())
                            .map(line -> {
                                try {
                                    JSONObject jsonResponse = new JSONObject(line);
                                    // 获取原始响应内容
                                    String content = jsonResponse.optString("response", "");
                                    // 确保不包含event:message前缀
                                    if (content.contains("event:message")) {
                                        content = content.replaceAll("event:message[^\\s]*", "");
                                    }
                                    
                                    // 去除<think>标签
                                    if (content.contains("<think>")) {
                                        content = content.replaceAll("<think>.*?</think>", "");
                                    }
                                    
                                    /* System.out.println("处理后的内容: " + content); */
                                    
                                    // 为每个字符创建单独的SSE事件
                                    JSONObject result = new JSONObject();
                                    result.put("data", content);
                                    return result.toString();
                                } catch (Exception e) {
                                    log.warn("Failed to parse streaming response JSON: {}", line);
                                    // 尝试直接使用行数据
                                    if (!line.trim().isEmpty()) {
                                        JSONObject result = new JSONObject();
                                        String cleanLine = line;
                                        // 确保不包含event:message前缀
                                        if (cleanLine.contains("event:message")) {
                                            cleanLine = cleanLine.replaceAll("event:message[^\\s]*", "");
                                        }
                                        // 去除<think>标签
                                        if (cleanLine.contains("<think>")) {
                                            cleanLine = cleanLine.replaceAll("<think>.*?</think>", "");
                                        }
                                        result.put("data", cleanLine);
                                        return result.toString();
                                    }
                                    return "";
                                }
                            })
                            .filter(text -> !text.isEmpty());
                })
                .subscribe(
                    chunk -> {
                        try {
                            // 发送每个chunk作为SSE事件
                            emitter.send(chunk, MediaType.APPLICATION_JSON);
                        } catch (Exception e) {
                            log.error("Error sending SSE chunk", e);
                            emitter.completeWithError(e);
                        }
                    },
                    error -> {
                        log.error("Error during streaming SSE API call", error);
                        
                        // 发送错误通知
                        try {
                            JSONObject errorJson = new JSONObject();
                            errorJson.put("error", "连接AI服务失败: " + error.getMessage());
                            emitter.send(errorJson.toString(), MediaType.APPLICATION_JSON);
                            emitter.send(SseEmitter.event().name("complete").data("[DONE]"));
                            emitter.complete();
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    },
                    () -> {
                        try {
                            log.info("SSE streaming chat completed");
                            // 发送完成事件
                            emitter.send(SseEmitter.event().name("complete").data("[DONE]"));
                            emitter.complete();
                        } catch (Exception e) {
                            log.error("Error completing SSE stream", e);
                            emitter.completeWithError(e);
                        }
                    }
                );
        } catch (Exception e) {
            log.error("Error initiating streaming SSE API call", e);
            
            // 发送错误通知
            try {
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", "启动AI服务连接失败: " + e.getMessage());
                emitter.send(errorJson.toString(), MediaType.APPLICATION_JSON);
                emitter.send(SseEmitter.event().name("complete").data("[DONE]"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        }
    }

    /**
     * 使用响应式WebClient进行流式聊天，支持更高效的数据传输
     * 
     * @param prompt 用户提问
     * @param onNext 处理每个文本块的回调
     * @param onError 处理错误的回调
     * @param onComplete 处理完成的回调
     */
    public void streamChatWithReactor(String prompt, Consumer<String> onNext, Consumer<Throwable> onError, Runnable onComplete) {
        log.info("开始使用WebClient进行流式聊天");
        
        try {
            // 创建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen3:4b");
            requestBody.put("prompt", prompt);
            requestBody.put("stream", true);
            // 设置小块输出
            requestBody.put("chunk_size", 10);
            
            
            log.info("使用大模型API: {}", ollamaApiUrl);
            
            // 发送请求并处理流式响应
            webClient.post()
                    .uri(ollamaApiUrl + "/generate")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, ollamaApiKey != null ? "Bearer " + ollamaApiKey : "")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    // 使用boundedElastic线程池处理阻塞操作
                    .publishOn(Schedulers.boundedElastic())
                    // 禁用预取，确保每个数据块都被单独处理
                    .limitRate(1, 0)
                    // 添加小延迟，确保数据流平滑
                    .delayElements(Duration.ofMillis(5))
                    .subscribe(
                        chunk -> {
                            try {
                                // 解析JSON响应
                                JSONObject jsonResponse = new JSONObject(chunk);
                                
                                // 提取生成的文本
                                if (jsonResponse.has("response")) {
                                    String text = jsonResponse.getString("response");
                                    
                                    // 调用回调处理文本块，立即发送每个字符
                                    if (text != null && !text.isEmpty()) {
                                        log.debug("接收到文本块: {}", text);
                                        onNext.accept(text);
                                    }
                                }
                                
                                // 检查是否完成
                                if (jsonResponse.has("done") && jsonResponse.getBoolean("done")) {
                                    onComplete.run();
                                }
                            } catch (Exception e) {
                                log.error("处理流式响应块失败: {}", e.getMessage());
                            }
                        },
                        error -> {
                            log.error("流式聊天出错: {}", error.getMessage());
                            onError.accept(error);
                        },
                        () -> {
                            log.info("流式聊天完成");
                            onComplete.run();
                        }
                    );
        } catch (Exception e) {
            log.error("初始化WebClient流式聊天失败: {}", e.getMessage());
            onError.accept(e);
        }
    }

    public void streamChatWithWebSocket(String prompt, Consumer<String> onNext, 
                                       Consumer<Throwable> onError, Runnable onComplete) {
        log.info("开始使用WebSocket进行流式聊天");
        
        try {
            // 创建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen3:4b");
            requestBody.put("prompt", prompt);
            requestBody.put("stream", true);
            // 设置小块输出
            requestBody.put("chunk_size", 5); // 更小的块大小
            
            // 发送请求并处理流式响应
            webClient.post()
                    .uri(ollamaApiUrl + "/generate")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, ollamaApiKey != null ? "Bearer " + ollamaApiKey : "")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    // 禁用预取，确保每个数据块都被单独处理
                    .limitRate(1, 0)
                    .subscribe(
                        chunk -> {
                            try {
                                // 解析JSON响应
                                JSONObject jsonResponse = new JSONObject(chunk);
                                
                                // 提取生成的文本
                                if (jsonResponse.has("response")) {
                                    String text = jsonResponse.getString("response");
                                    
                                    // 立即通过WebSocket发送每个字符
                                    if (text != null && !text.isEmpty()) {
                                        onNext.accept(text);
                                    }
                                }
                                
                                // 检查是否完成
                                if (jsonResponse.has("done") && jsonResponse.getBoolean("done")) {
                                    onComplete.run();
                                }
                            } catch (Exception e) {
                                log.error("处理流式响应块失败: {}", e.getMessage());
                            }
                        },
                        error -> {
                            log.error("流式聊天出错: {}", error.getMessage());
                            onError.accept(error);
                        },
                        () -> {
                            log.info("流式聊天完成");
                            onComplete.run();
                        }
                    );
        } catch (Exception e) {
            log.error("初始化WebSocket流式聊天失败: {}", e.getMessage());
            onError.accept(e);
        }
    }
}
