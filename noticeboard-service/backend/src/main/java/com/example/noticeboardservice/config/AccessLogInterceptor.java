package com.example.noticeboardservice.config;

import com.example.noticeboardservice.dto.AccessLogDto;
import com.example.noticeboardservice.mapper.AccessLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AccessLogInterceptor implements HandlerInterceptor {
    private final AccessLogMapper accessLogMapper;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            AccessLogDto log = AccessLogDto.builder()
                    .method(request.getMethod())
                    .uri(request.getRequestURI())
                    .clientIp(request.getRemoteAddr())
                    .createdAt(LocalDateTime.now())
                    .build();

            accessLogMapper.insertAccessLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
