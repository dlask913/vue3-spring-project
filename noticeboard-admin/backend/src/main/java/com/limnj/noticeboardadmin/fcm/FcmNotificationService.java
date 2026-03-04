package com.limnj.noticeboardadmin.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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

    public void sendPush(PushSendRequest request){
        FcmTokenResponseDto findFcmToken = fcmNotificationMapper.findFcmTokenByUserId(request.getUserId());
        try {
            sendNotification(findFcmToken.getToken(), request.getTitle(), request.getBody());
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}
