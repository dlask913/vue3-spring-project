package com.limnj.noticeboardadmin.jwt;

import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    String generateRefreshToken(String username);
    String generateNewAccessToken(String refreshToken);
    void expireRefreshToken(String username);
}
