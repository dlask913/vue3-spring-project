package com.limnj.noticeboardadmin.jwt;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService tokenServiceImpl;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/refresh")
    @Operation(summary = "access 토큰 발급 API")
    public ResponseEntity<String> getNewAccessToken(@RequestBody RefreshTokenDto requestDto){
        String newAccessToken = "잘못된 토큰 정보입니다.";
        if(tokenServiceImpl.validateRefreshToken(requestDto)){
            newAccessToken = jwtTokenUtil.generateToken(requestDto.getUsername()).getAccessToken();
        }
        return ResponseEntity.ok().body(newAccessToken);
    }
}
