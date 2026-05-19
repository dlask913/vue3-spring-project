package com.limnj.noticeboardadmin.jwt;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final RefreshTokenService tokenServiceImpl;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/refresh")
    @Operation(summary = "access 토큰 발급 API")
    public ResponseEntity<String> getNewAccessToken(@CookieValue(name = "refreshToken") String refreshToken){
        String newAccessToken = tokenServiceImpl.generateNewAccessToken(refreshToken);
        return ResponseEntity.ok().body(newAccessToken);
    }
}
