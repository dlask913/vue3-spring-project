package com.example.noticeboardservice.config.interceptor;

import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.service.MemberService;
import com.example.noticeboardservice.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

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
        if (("/member".equals(request.getRequestURI()) && "POST".equals(method))
                || ((request.getRequestURI()).startsWith("/notice") && "GET".equals(method))
                || ((request.getRequestURI()).startsWith("/comment") && "GET".equals(method))
                || ((request.getRequestURI()).startsWith("/heart") && "GET".equals(method))
                || "OPTIONS".equals(method)) {
            return true;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        log.info("requestTokenHeader: {}",requestTokenHeader);
        if (StringUtils.startsWith(requestTokenHeader, "Bearer ")) {
            String token = requestTokenHeader.substring(7);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            Optional<MemberDto> memberDto = memberServiceImpl.findByEmail(userEmail);
            if (memberDto.isEmpty()) {
                log.warn("검증 실패 !");
                return false;
            }
            log.info("[REQUEST] [" + memberDto.get().getEmail() + "]" + request.getRequestURI());
        } else {
            log.warn("인증 실패 !");
            return false;
        }
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
