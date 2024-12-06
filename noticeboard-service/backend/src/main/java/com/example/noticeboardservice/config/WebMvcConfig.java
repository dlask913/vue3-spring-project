package com.example.noticeboardservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload-path}")
    String uploadPath;
    @Value("${default-img-path}")
    String defaultImgPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // 해당 url로 시작하는 경우
            .addResourceLocations(uploadPath); // uploadPath 에 설정한 폴더를 기준으로 파일을 읽어옴

        registry.addResourceHandler("/image/**") // default img 접속 경로
            .addResourceLocations(defaultImgPath); // defaultImgPath 에 설정한 폴더를 기준으로 파일을 읽어옴

    }
}
