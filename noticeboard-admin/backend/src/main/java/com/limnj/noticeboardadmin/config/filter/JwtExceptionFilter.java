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

        String exception = (String) request.getAttribute("jwt_exception");
        if (exception == null) return;
        setErrorResponse(response, exception);
    }

    public void setErrorResponse(HttpServletResponse response, String exception) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        final ExceptionDto responseError = ExceptionDto.builder()
                .status(HttpServletResponse.SC_FORBIDDEN)
                .message(exception) // prefix is TOKEN_*
                .build();
        response.getWriter().write(mapper.writeValueAsString(responseError));
    }
}
