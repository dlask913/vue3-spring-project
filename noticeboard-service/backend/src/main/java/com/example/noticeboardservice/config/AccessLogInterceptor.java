package com.example.noticeboardservice.config;

import com.example.noticeboardservice.dto.common.AccessLogDto;
import com.example.noticeboardservice.mapper.common.AccessLogMapper;
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
            AccessLogDto accessLog = AccessLogDto.builder()
                    .method(request.getMethod())
                    .uri(request.getRequestURI())
                    .clientIp(getClientIP(request))
                    .createdAt(LocalDateTime.now())
                    .build();
            accessLogMapper.insertAccessLog(accessLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
