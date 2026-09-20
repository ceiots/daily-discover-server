package com.dailydiscover.common.config;

import com.dailydiscover.common.util.AnonymousUserResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${daily-discover.anonymous-header:X-Anonymous-Id}")
    private String anonymousHeader;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AnonymousUserResolver(anonymousHeader));
    }
}
