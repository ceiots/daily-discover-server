package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.mapper.UserMapper;
import com.example.model.User;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String email, String password) {
        // 对密码进行加密
        User user = new User();
        /* user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRegistrationTime(new Date());
        userMapper.insert(user); */
    }


    public User login(String phoneNumber, String password) {
        return userMapper.findByPhoneNumberAndPassword(phoneNumber, password);
    }

    public void register(User user) {
        userMapper.registerUser(user);
    }

    public User findUserByPhoneNumber(String phoneNumber) {
        return userMapper.findByPhoneNumber(phoneNumber);
    }
}