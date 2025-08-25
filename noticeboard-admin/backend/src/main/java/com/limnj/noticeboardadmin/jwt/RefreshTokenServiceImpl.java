package com.limnj.noticeboardadmin.jwt;

import com.limnj.noticeboardadmin.exception.RefreshTokenInvalidException;
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
    public String generateNewAccessToken(RefreshTokenDto requestDto) {
        if(!validateRefreshToken(requestDto)){
            throw new RefreshTokenInvalidException("Refresh Token 이 일치하지 않습니다.");
        }
        return jwtTokenUtil.generateToken(requestDto.getUsername());
    }

    public boolean validateRefreshToken(RefreshTokenDto requestDto) {
        RefreshToken refreshToken = tokenMapper.findRefreshTokenByUsername(requestDto.getUsername()).orElseThrow(
                () -> new RefreshTokenInvalidException("해당 User 의 Refresh Token 이 존재하지 않습니다.")
        );
        return refreshToken.getToken().equals(requestDto.getRefreshToken());
    }
}
