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

    /**
     * 개인 알림
     * @param request
     */
    public void sendNotificationToUser(SendPushRequestDto request){
        List<String> tokens = fcmNotificationMapper.findFcmTokenByUserId(request.getUserId())
                .stream()
                .map(FcmTokenResponseDto::getToken)
                .toList();

        sendMultiCashPushMessage(request, tokens);
    }

    /**
     * 전체 알림
     * @param request
     */
    public void sendNotificationToAll(SendPushRequestDto request){
        List<String> tokens = fcmNotificationMapper.findAllTokens()
                .stream()
                .map(FcmTokenResponseDto::getToken)
                .toList();

        sendMultiCashPushMessage(request, tokens);
    }

    private static void sendMultiCashPushMessage(SendPushRequestDto request, List<String> tokens) {
        if(tokens.isEmpty()) return;

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

    public void updateFcmTokenForUser(FcmTokenRequestDto fcmTokenRequestDto) {
        if(fcmNotificationMapper
                .existFcmTokenByUserIdAndToken(fcmTokenRequestDto.getUserId(), fcmTokenRequestDto.getToken())){
            return;
        }
        fcmNotificationMapper.saveFcmToken(fcmTokenRequestDto);
    }

    public void unbindFcmToken(String fcmToken){
        fcmNotificationMapper.unbindFcmToken(fcmToken);
    }
}
