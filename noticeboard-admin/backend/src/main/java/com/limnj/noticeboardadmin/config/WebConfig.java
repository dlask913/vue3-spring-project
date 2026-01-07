package com.limnj.noticeboardadmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /templates/** 로 시작하는 URL로 접근 시, 내부 static/templates/ 경로에서 리소스 제공
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/static/templates/")
                .setCachePeriod(3600);
    }
}