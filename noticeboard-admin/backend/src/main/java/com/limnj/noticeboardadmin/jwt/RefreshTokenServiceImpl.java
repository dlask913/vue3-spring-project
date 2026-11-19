package com.limnj.noticeboardadmin.jwt;

import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final TokenMapper tokenMapper;
    private final JwtTokenUtil jwtTokenUtil;
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;

    @Override
    public String generateRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_VALIDITY / 1000))
                .build();
        tokenMapper.saveRefreshToken(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    public String generateNewAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BizException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        // Refresh Token은 UUID이므로 DB에 저장된 유효한 토큰으로 계정을 확인
        RefreshToken savedToken = tokenMapper.findValidRefreshTokenByToken(refreshToken)
                .orElseThrow(() -> new BizException(ErrorCode.REFRESH_TOKEN_INVALID));
        return jwtTokenUtil.generateToken(savedToken.getUsername());
    }

    @Override
    public void expireRefreshToken(String username) {
        tokenMapper.expireRefreshToken(username);
    }

}
