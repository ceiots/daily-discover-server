package com.example.task;

import com.example.config.ImageConfig;
import com.example.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImageConfigUpdater {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ImageConfig imageConfig;

    @Scheduled(fixedRate = 60000) // 每分钟更新一次
    public void updateImagePrefix() {
        String newPrefix = configService.getImagePrefix();
        imageConfig.setImagePrefix(newPrefix);
    }
}