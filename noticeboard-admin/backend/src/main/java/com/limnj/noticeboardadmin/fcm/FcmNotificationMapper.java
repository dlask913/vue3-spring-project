package com.limnj.noticeboardadmin.fcm;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FcmNotificationMapper {
    void saveFcmToken(FcmTokenRequestDto requestDto);
}
