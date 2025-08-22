package com.limnj.noticeboardadmin.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final TokenMapper tokenMapper;
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;

    @Override
    public RefreshToken jenerateRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_VALIDITY / 1000))
                .build();
        tokenMapper.saveRefreshToken(refreshToken);
        return refreshToken;
    }
}
