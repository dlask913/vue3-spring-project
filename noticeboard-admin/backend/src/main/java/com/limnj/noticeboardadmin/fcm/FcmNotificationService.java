package com.limnj.noticeboardadmin.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class FcmNotificationService {
    private final FcmNotificationMapper fcmNotificationMapper;

    /**
     * 개인 알림
     * @param request
     */
    public SendPushResponseDto sendNotificationToUser(SendPushRequestDto request){
        List<String> tokens = fcmNotificationMapper.findFcmTokenByUserId(request.getUserId())
                .stream()
                .map(FcmTokenResponseDto::getToken)
                .toList();

        return sendMultiCashPushMessage(request, tokens);
    }

    /**
     * 전체 알림
     * @param request
     */
    public SendPushResponseDto sendNotificationToAll(SendPushRequestDto request){
        List<String> tokens = fcmNotificationMapper.findAllTokens()
                .stream()
                .map(FcmTokenResponseDto::getToken)
                .toList();

        return sendMultiCashPushMessage(request, tokens);
    }

    private SendPushResponseDto sendMultiCashPushMessage(SendPushRequestDto request, List<String> tokens) {
        if(tokens.isEmpty()) return new SendPushResponseDto(0, 0, Map.of());

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
            Map<String, Integer> errorCounts = new HashMap<>();
            int successCount = 0;

            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);
                if (sendResponse.isSuccessful()) {
                    successCount++;
                } else {
                    FirebaseMessagingException exception = sendResponse.getException();
                    MessagingErrorCode errorCode = exception.getMessagingErrorCode();
                    errorCounts.merge(errorCodeName(exception), 1, Integer::sum);

                    if (errorCode == MessagingErrorCode.UNREGISTERED
                            || errorCode == MessagingErrorCode.SENDER_ID_MISMATCH) {
                        // 등록되지 않은 토큰 → DB 삭제
                        String invalidToken = tokens.get(i);
                        invalidTokens.add(invalidToken);
                        log.warn("Invalid FCM token detected; removing its registration");
                    } else if (errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                        // 메시지 오류일 수도 있으므로 사전 검증을 통과했어도 토큰을 유지
                        log.warn("FCM returned INVALID_ARGUMENT; retaining token registration and checking message payload is required");
                    }
                }
            }

            if (!invalidTokens.isEmpty()){
                fcmNotificationMapper.deleteFcmTokens(invalidTokens);
            }
            return new SendPushResponseDto(successCount, responses.size() - successCount, errorCounts);
        } catch (FirebaseMessagingException e) {
            String errorCode = errorCodeName(e);
            log.warn("FCM push failed: code={}, targetCount={}", errorCode, tokens.size());
            return new SendPushResponseDto(0, tokens.size(), Map.of(errorCode, tokens.size()));
        }
    }

    private String errorCodeName(FirebaseMessagingException exception) {
        if (exception.getMessagingErrorCode() != null) return exception.getMessagingErrorCode().name();
        if (exception.getErrorCode() != null) return exception.getErrorCode().name();
        return "UNKNOWN";
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

    public void unbindFcmTokenForUser(String username, String fcmToken) {
        fcmNotificationMapper.unbindFcmTokenForUser(username, fcmToken);
    }
}
