package com.example.noticeboardservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessLogInterceptor accessLogInterceptor;

    @Value("${upload-path}")
    private String uploadPath;

    @Value("${default-img-path}")
    private String defaultImgPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // 해당 url로 시작하는 경우
            .addResourceLocations(uploadPath); // uploadPath 에 설정한 폴더를 기준으로 파일을 읽어옴

        registry.addResourceHandler("/image/**") // default img 접속 경로
            .addResourceLocations(defaultImgPath); // defaultImgPath 에 설정한 폴더를 기준으로 파일을 읽어옴

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLogInterceptor)
                .addPathPatterns("/**") // 전체 요청에 대해 적용
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/image/**",
                        "/favicon.ico",
                        "/error"
                ); // 정적 리소스 제외
    }
}
