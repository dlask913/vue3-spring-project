package com.limnj.noticeboardadmin.fcm;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmNotificationController {
    private final FcmNotificationService fcmNotificationService;

    @PostMapping("/fcm/token")
    public void saveFcmToken(@RequestBody FcmTokenRequestDto requestDto) {
        fcmNotificationService.updateFcmTokenForUser(
                FcmTokenRequestDto.builder()
                        .token(requestDto.getToken())
                        .deviceType(requestDto.getDeviceType())
                        .build()
        );
    }

    @PostMapping("/push/all")
    public SendPushResponseDto sendPushToAll(@Valid @RequestBody SendPushRequestDto requestDto){
        return fcmNotificationService.sendNotificationToAll(requestDto);
    }

    @PostMapping("/push/user")
    public SendPushResponseDto sendPushToUser(
            @Validated(SendPushRequestDto.UserPush.class) @RequestBody SendPushRequestDto requestDto) {
        return fcmNotificationService.sendNotificationToUser(requestDto);
    }
}
