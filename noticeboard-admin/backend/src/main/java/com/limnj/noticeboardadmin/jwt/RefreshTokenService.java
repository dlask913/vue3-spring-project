package com.limnj.noticeboardadmin.jwt;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public interface RefreshTokenService {
    String generateRefreshToken(String username);
    String generateNewAccessToken(String refreshToken);
    void expireRefreshToken(String username);
    Optional<String> revokeRefreshToken(String refreshToken);
}
