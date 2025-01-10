package com.example.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.entity.User;
import com.example.service.UserService;

@Controller
@RequestMapping("/dailyDiscover/users")
public class UserController  {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users"; // 对应的 Thymeleaf 模板文件名
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            System.out.println("Received user: " + user);
            userService.registerUser(user.getEmail(), user.getPassword());
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("注册失败");
        }
    }
}