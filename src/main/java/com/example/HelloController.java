package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello, World! my";
    }

    @GetMapping("/api/hello")
    public String helloApi() {
        return "Hello from API!";
    }
}