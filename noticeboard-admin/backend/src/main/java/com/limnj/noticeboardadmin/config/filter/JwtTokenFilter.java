package com.limnj.noticeboardadmin.config.filter;

import com.limnj.noticeboardadmin.member.MemberService;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final MemberService memberServiceImpl;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtTokenFilter(MemberService memberServiceImpl, JwtTokenUtil jwtTokenUtil) {
        this.memberServiceImpl = memberServiceImpl;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        if (StringUtils.startsWith(requestTokenHeader, "Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                log.info("[REQUEST] [" + username + "]" + request.getRequestURI());

                if (StringUtils.isNoneEmpty(username) && null == SecurityContextHolder.getContext().getAuthentication()) {
                    UserDetails userDetails = memberServiceImpl.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext()
                                .setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.warn("토큰이 만료되었습니다.");
                request.setAttribute("jwt_exception", "TOKEN_EXPIRED");
            } catch (MalformedJwtException | UnsupportedJwtException e) {
                log.warn("잘못된 토큰입니다.");
                request.setAttribute("jwt_exception", "TOKEN_MALFORMED");
            } catch (SecurityException | SignatureException e) {
                log.warn("유효하지 않은 토큰입니다.");
                request.setAttribute("jwt_exception", "TOKEN_INVALID_SIGNATURE");
            } catch (Exception e) {
                log.warn("인증 실패 : {}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.warn("JWT Token does not begin with Bearer String");
        }

        filterChain.doFilter(request,response);
    }
}
