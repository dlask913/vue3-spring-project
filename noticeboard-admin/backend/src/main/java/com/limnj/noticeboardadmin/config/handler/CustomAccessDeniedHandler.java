package com.limnj.noticeboardadmin.config.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.limnj.noticeboardadmin.exception.ExceptionDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 토큰은 유효하나 hasAuthority 조건이 충족되지 않는 경우 → 권한 없음 403 발생
     * @param request
     * @param response
     * @param accessDeniedException
     * @throws IOException
     */

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("접근 권한이 없습니다. " + accessDeniedException.getMessage());

        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 전송

        final ExceptionDto responseError = ExceptionDto.builder()
                .status(HttpServletResponse.SC_FORBIDDEN)
                .message("ACCESS_DENIED")
                .build();
        response.getWriter().write(mapper.writeValueAsString(responseError));
    }
}