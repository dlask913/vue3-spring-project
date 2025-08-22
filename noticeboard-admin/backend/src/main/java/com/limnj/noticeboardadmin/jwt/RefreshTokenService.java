package com.limnj.noticeboardadmin.jwt;

import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    RefreshToken jenerateRefreshToken(String username);
}
