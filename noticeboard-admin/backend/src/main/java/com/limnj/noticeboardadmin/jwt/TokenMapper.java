package com.limnj.noticeboardadmin.jwt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface TokenMapper {
    void saveRefreshToken(RefreshToken refreshToken);
    Optional<RefreshToken> findValidRefreshTokenByToken(@Param("token") String token);
    void expireRefreshToken(String username);
    Optional<String> deleteRefreshTokenByToken(@Param("token") String token);
}
