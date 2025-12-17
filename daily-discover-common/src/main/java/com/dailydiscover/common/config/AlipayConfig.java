package com.dailydiscover.common.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
//@Configuration
//@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String gatewayUrl;
    private String returnUrl;
    private String notifyUrl;
}