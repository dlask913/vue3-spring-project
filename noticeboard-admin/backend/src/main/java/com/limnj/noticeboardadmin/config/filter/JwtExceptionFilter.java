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
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String body = switch (exception) {
            case "EXPIRED" -> """
                {"code":"TOKEN_EXPIRED","message":"Access token expired"}
                """;
            case "MALFORMED" -> """
                {"code":"INVALID_TOKEN","message":"Malformed JWT"}
                """;
            case "INVALID_SIGNATURE" -> """
                {"code":"INVALID_SIGNATURE","message":"Invalid JWT signature"}
                """;
            default -> """
                {"code":"AUTH_ERROR","message":"Authentication failed"}
                """;
        };
        response.getWriter().write(body);
    }
}
