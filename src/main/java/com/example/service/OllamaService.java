package com.example.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.Arrays;

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
            System.err.println("Error during API call: " + e.getMessage());
            emitter.completeWithError(e);
            return emitter;
        }
    }
}
