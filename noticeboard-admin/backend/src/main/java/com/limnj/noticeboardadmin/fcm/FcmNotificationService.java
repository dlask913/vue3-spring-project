package com.limnj.noticeboardadmin.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class FcmNotificationService {
    private final FcmNotificationMapper fcmNotificationMapper;
    public void saveFcmToken(FcmTokenRequestDto requestDto) {
        fcmNotificationMapper.saveFcmToken(requestDto);
    }

    /**
     * 개인 알림
     * @param request
     */
    public void sendPushToUser(PushSendRequestDto request){
        FcmTokenResponseDto findFcmToken = fcmNotificationMapper.findFcmTokenByUserId(request.getUserId());

        Message message = Message.builder()
                .setToken(findFcmToken.getToken())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 전체 알림
     * @param request
     */
    public void sendNoticePush(PushSendRequestDto request){
        List<String> tokens = fcmNotificationMapper.findAllTokens()
                .stream()
                .map(FcmTokenResponseDto::getToken)
                .toList();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();
        try {
            BatchResponse response =
                    FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("response.getSuccessCount(): {} ", response.getSuccessCount());
            log.warn("response.getFailureCount(): {}", response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
