package com.limnj.noticeboardadmin.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import com.limnj.noticeboardadmin.exception.ExceptionDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response); // go to next filter
        } catch (BizException e) { // 여기서 직접 처리
            log.error("BizException in JwtExceptionFilter: {}", e.getMessage());
            setErrorResponse(response, e.getErrorCode());
        }
    }

    public void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        final ExceptionDto responseError = ExceptionDto.builder()
                .status(errorCode.getStatus())
                .errorCode(errorCode.name())
                .message(errorCode.getDescription())
                .build();
        response.getWriter().write(mapper.writeValueAsString(responseError));
    }
}
