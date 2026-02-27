package com.limnj.noticeboardadmin.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor
public class FcmNotificationService {
    private final FcmNotificationMapper fcmNotificationMapper;
    public void saveFcmToken(FcmTokenRequestDto requestDto) {
        fcmNotificationMapper.saveFcmToken(requestDto);
    }
}
