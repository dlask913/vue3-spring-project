package com.example.noticeboardservice.config.filter;

import com.example.noticeboardservice.dto.ExceptionDto;
import com.example.noticeboardservice.exception.TokenExpiredException;
import com.example.noticeboardservice.exception.TokenInvalidException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        } catch (TokenExpiredException e) { // 토큰 만료된 경우
            setErrorResponse(e.getStatus(), response, e.getMessage());
        } catch (TokenInvalidException e) { // 토큰 정보 잘못된 경우
            setErrorResponse(e.getStatus(), response, e.getMessage());
        } // todo: Exception Test Code
    }

    public void setErrorResponse(int status, HttpServletResponse response, String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");

        final ExceptionDto responseError = ExceptionDto.builder()
                .status(status)
                .message(message)
                .build();
        response.getWriter().write(mapper.writeValueAsString(responseError));
    }
}
