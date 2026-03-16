package com.limnj.noticeboardadmin.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    private void sendMultiCashPushMessage(SendPushRequestDto request, List<String> tokens) {
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

            List<SendResponse> responses = response.getResponses();
            List<String> invalidTokens = new ArrayList<>();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);
                if (!sendResponse.isSuccessful()) {
                    FirebaseMessagingException exception = sendResponse.getException();
                    MessagingErrorCode errorCode = exception.getMessagingErrorCode();

                    if (errorCode == MessagingErrorCode.UNREGISTERED
                            || errorCode == MessagingErrorCode.INVALID_ARGUMENT
                            || errorCode == MessagingErrorCode.SENDER_ID_MISMATCH) {
                        // 등록되지 않은 토큰 → DB 삭제
                        String invalidToken = tokens.get(i);
                        invalidTokens.add(invalidToken);
                        log.warn("Invalid FCM token detected: {}", invalidToken);
                    }
                }
            }

            if (!invalidTokens.isEmpty()){
                fcmNotificationMapper.deleteFcmTokens(invalidTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.warn("FCM push failed: {}", e.getMessage());
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
