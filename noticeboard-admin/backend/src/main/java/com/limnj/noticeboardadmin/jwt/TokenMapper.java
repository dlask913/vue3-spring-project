package com.limnj.noticeboardadmin.jwt;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TokenMapper {
    void saveRefreshToken(@Param("username") String username, @Param("refreshToken") String refreshToken);
}
