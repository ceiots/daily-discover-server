package com.example.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.model.AIContent;
import com.example.model.AIResponse;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private RestTemplate restTemplate; // 注入 RestTemplate

    @PostMapping("/generate")
    public List<AIContent> generateContent(@RequestBody String keyword) {
        String apiKey = "sk-271e72eb9797403a980c169e89c07416"; // DeepSeek API Key
        String url = "https://api.deepseek.com/v1/chat/completions"; 

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 构造请求体
        String jsonBody = String.format(
            "{\"model\": \"deepseek-chat\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"%s\"}], \"stream\": false}",
            keyword
        );

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        try {
            // 发送 POST 请求
            ResponseEntity<AIResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, AIResponse.class);
            System.out.println("Response: " + response.getBody());

            // 检查响应体和数据是否为 null
            if (response.getBody() != null && response.getBody().getData() != null) {
                return Arrays.asList(response.getBody().getData());
            } else {
                System.err.println("Received null response or data from API");
                return Collections.emptyList(); // 返回空列表
            }
        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
            return Collections.emptyList(); // 返回空列表而不是抛出异常
        }
    }
} 