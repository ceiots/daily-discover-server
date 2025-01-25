package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.service.OllamaService;


@RestController
@RequestMapping("/ai")
public class AiController {

     @Autowired
    private OllamaService ollamaService;

    @PostMapping(value = "/generate-article", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateArticle(@RequestBody String prompt) {
        return ollamaService.generateArticle(prompt);
    }
}
