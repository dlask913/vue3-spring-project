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

@Service
@RequiredArgsConstructor @Slf4j
public class FcmNotificationService {
    private static final int MAX_MULTICAST_TOKENS = 500;
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

        return sendMulticastPushMessage(request, tokens);
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

        return sendMulticastPushMessage(request, tokens);
    }

    private SendPushResponseDto sendMulticastPushMessage(SendPushRequestDto request, List<String> tokens) {
        int successCount = 0;
        int failureCount = 0;
        Map<String, Integer> errorCounts = new HashMap<>();

        // FCM은 한 번에 최대 500개 토큰만 허용하므로 각 묶음의 결과를 합산한다
        for (int start = 0; start < tokens.size(); start += MAX_MULTICAST_TOKENS) {
            List<String> batchTokens = tokens.subList(start, Math.min(start + MAX_MULTICAST_TOKENS, tokens.size()));
            SendPushResponseDto result = sendBatch(request, batchTokens);
            successCount += result.getSuccessCount();
            failureCount += result.getFailureCount();
            result.getErrorCounts().forEach((code, count) -> errorCounts.merge(code, count, Integer::sum));
        }

        return new SendPushResponseDto(successCount, failureCount, errorCounts);
    }

    private SendPushResponseDto sendBatch(SendPushRequestDto request, List<String> tokens) {
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

                    if (errorCode == MessagingErrorCode.UNREGISTERED) {
                        // 등록되지 않은 토큰 → DB 삭제
                        String invalidToken = tokens.get(i);
                        invalidTokens.add(invalidToken);
                        log.warn("Invalid FCM token detected; removing its registration");
                    } else if (errorCode == MessagingErrorCode.SENDER_ID_MISMATCH) {
                        // FCM 발신자 불일치 오류 발생 시 토큰 유지
                        log.warn("FCM returned SENDER_ID_MISMATCH; retaining token registration and checking Firebase project configuration is required");
                    } else if (errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                        // 메시지 오류일 수도 있으므로 사전 검증을 통과했어도 토큰을 유지
                        log.warn("FCM returned INVALID_ARGUMENT; retaining token registration and checking message payload is required");
                    }
                }
            }

            if (!invalidTokens.isEmpty()){
                // 현재 묶음에서 등록 해제된 토큰들을 하나의 DELETE 문으로 삭제
                fcmNotificationMapper.deleteFcmTokens(invalidTokens);
            }
            return new SendPushResponseDto(successCount, responses.size() - successCount, errorCounts);
        } catch (FirebaseMessagingException e) {
            // 현재 묶음만 실패 처리
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

    @Transactional
    public void updateFcmTokenForUser(FcmTokenRequestDto fcmTokenRequestDto) {
        if(fcmNotificationMapper
                .existFcmTokenByUserIdAndToken(fcmTokenRequestDto.getUserId(), fcmTokenRequestDto.getToken())){
            return;
        }
        fcmNotificationMapper.saveFcmToken(fcmTokenRequestDto);
    }

    @Transactional
    public void unbindFcmToken(String fcmToken){
        fcmNotificationMapper.unbindFcmToken(fcmToken);
    }

    @Transactional
    public void unbindFcmTokenForUser(String username, String fcmToken) {
        fcmNotificationMapper.unbindFcmTokenForUser(username, fcmToken);
    }
}
