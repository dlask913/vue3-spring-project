package com.example.noticeboardservice.config.interceptor;

import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.service.MemberService;
import com.example.noticeboardservice.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

@Slf4j @Component
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberService memberServiceImpl;

    public LoginInterceptor(JwtTokenUtil jwtTokenUtil, MemberService memberServiceImpl) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.memberServiceImpl = memberServiceImpl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Requested URI: " + request.getRequestURI());
        log.info("method: " + request.getMethod());

        // 회원 가입 API 요청 예외
        String method = request.getMethod();
        if ("/member".equals(request.getRequestURI()) && ("POST".equals(method) || "OPTIONS".equals(method))) {
            return true;
        }

        String token = request.getHeader("Authorization").substring(7);
        String userEmail = jwtTokenUtil.getUsernameFromToken(token);
        Optional<MemberDto> memberDto = memberServiceImpl.findByEmail(userEmail);
        if (memberDto.isEmpty()) {
            log.warn("검증 실패 !");
            return false;
        }

        log.info("[REQUEST] [" + memberDto.get().getEmail() + "]" + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("[RESPONSE] [" + request.getMethod() + "]" + request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            log.error( ex.getMessage());
        }
    }
}
