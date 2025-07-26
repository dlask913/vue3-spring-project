package com.limnj.noticeboardadmin.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limnj.noticeboardadmin.exception.ExceptionDto;
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
        chain.doFilter(request, response); // go to next filter
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
