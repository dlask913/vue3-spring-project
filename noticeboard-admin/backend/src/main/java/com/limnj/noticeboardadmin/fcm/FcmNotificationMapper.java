package com.limnj.noticeboardadmin.fcm;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FcmNotificationMapper {
    void saveFcmToken(FcmTokenRequestDto requestDto);
    FcmTokenResponseDto findFcmTokenByUserId(Long userId);
    List<FcmTokenResponseDto> findAllTokens();
}
