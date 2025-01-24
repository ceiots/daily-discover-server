package com.example.controller;

import java.util.Arrays;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private WebClient webClient;

    @PostMapping(value = "/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateContent(@RequestBody String keyword) {
        String apiKey = "sk-271e72eb9797403a980c169e89c07416"; // DeepSeek API Key
        String url = "https://api.deepseek.com/v1/chat/completions";

        // 构造请求体
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "deepseek-chat");
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", keyword);
        jsonBody.put("messages", Arrays.asList(systemMessage, userMessage));
        jsonBody.put("stream", true);

        SseEmitter emitter = new SseEmitter();

        try {
            // 发送 POST 请求
            Flux<String> responseFlux = webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(jsonBody.toString())
                .retrieve()
                .bodyToFlux(String.class);

            // 异步处理逻辑
            responseFlux.subscribe(
                data -> {
                    try {
                        emitter.send(data); // 发送响应
                    } catch (Exception e) {
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
