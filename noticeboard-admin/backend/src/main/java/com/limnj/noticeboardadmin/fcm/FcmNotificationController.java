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

    @PostMapping("/push/all")
    public void sendPushToAll(@RequestBody SendPushRequestDto requestDto){
        fcmNotificationService.sendNotificationToAll(requestDto);
    }

    @PostMapping("/push/user")
    public void sendPushToUser(@RequestBody SendPushRequestDto requestDto) {
        fcmNotificationService.sendNotificationToUser(requestDto);
    }
}
