package com.limnj.noticeboardadmin.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmNotificationController {
    private final FcmNotificationService fcmNotificationService;

    @PostMapping("/fcm/token")
    public void saveFcmToken(@RequestBody FcmTokenRequestDto requestDto) {
        fcmNotificationService.saveFcmToken(requestDto);
    }

}
