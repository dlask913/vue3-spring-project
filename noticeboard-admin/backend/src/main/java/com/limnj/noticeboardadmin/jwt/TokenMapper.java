package com.limnj.noticeboardadmin.jwt;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper {
    void saveRefreshToken(RefreshToken refreshToken);
}
