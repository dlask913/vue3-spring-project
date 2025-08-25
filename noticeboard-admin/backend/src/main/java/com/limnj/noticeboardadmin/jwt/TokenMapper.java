package com.limnj.noticeboardadmin.jwt;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface TokenMapper {
    void saveRefreshToken(RefreshToken refreshToken);
    Optional<RefreshToken> findRefreshTokenByUsername(String username);
}
