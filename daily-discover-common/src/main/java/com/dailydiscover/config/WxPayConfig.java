package com.dailydiscover.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Data
//@Configuration
//@ConfigurationProperties(prefix = "wxpay")
public class WxPayConfig implements WXPayConfig {
    private String appId;
    private String mchId;
    private String key;
    private String notifyUrl;
    private String certPath;

    @Override
    public String getAppID() {
        return appId;
    }

    @Override
    public String getMchID() {
        return mchId;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public InputStream getCertStream() {
        return null; // 如需退款等操作，需要实现证书加载
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}