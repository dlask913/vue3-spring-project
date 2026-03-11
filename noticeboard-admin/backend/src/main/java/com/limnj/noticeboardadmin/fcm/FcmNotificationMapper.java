package com.limnj.noticeboardadmin.fcm;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FcmNotificationMapper {
    void saveFcmToken(FcmTokenRequestDto requestDto);
    List<FcmTokenResponseDto> findFcmTokenByUserId(Long userId);
    boolean existFcmTokenByUserIdAndToken(@Param("userId") Long userId, @Param("fcmToken") String fcmToken);
    List<FcmTokenResponseDto> findAllTokens();
    void unbindFcmToken(String fcmToken);
}
