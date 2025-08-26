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

    @PostMapping("/refresh")
    @Operation(summary = "access 토큰 발급 API")
    public ResponseEntity<String> getNewAccessToken(@RequestBody RefreshTokenRequestDto requestDto){
        String newAccessToken = tokenServiceImpl.generateNewAccessToken(requestDto);
        return ResponseEntity.ok().body(newAccessToken);
    }
}
