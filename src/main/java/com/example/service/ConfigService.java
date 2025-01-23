package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.ConfigMapper;

@Service
public class ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    public String getImagePrefix() {
        return configMapper.getConfigValue("image_prefix");
    }
}