package com.limnj.noticeboardadmin.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FCM 알림 서비스")
class FcmNotificationServiceTest {

    @Mock
    private FcmNotificationMapper mapper;

    @Mock
    private FirebaseMessaging messaging;

    @InjectMocks
    private FcmNotificationService service;

    @Test
    @DisplayName("익명 기기의 신규 토큰은 사용자 ID 없이 저장 요청한다")
    void savesAnonymousToken() {
        FcmTokenRequestDto request = tokenRequest(null, "anonymous-device");

        service.updateFcmTokenForUser(request);

        verify(mapper).existFcmTokenByUserIdAndToken(null, "anonymous-device");
        verify(mapper).saveFcmToken(request);
    }

    @Test
    @DisplayName("로그인한 사용자의 신규 토큰은 사용자 ID와 함께 저장 요청한다")
    void savesTokenForAuthenticatedUser() {
        FcmTokenRequestDto request = tokenRequest(1L, "user-device");

        service.updateFcmTokenForUser(request);

        verify(mapper).existFcmTokenByUserIdAndToken(1L, "user-device");
        verify(mapper).saveFcmToken(request);
    }

    @Test
    @DisplayName("같은 사용자와 토큰이 이미 연결되어 있으면 중복 저장하지 않는다")
    void skipsAlreadyBoundToken() {
        FcmTokenRequestDto request = tokenRequest(1L, "existing-device");
        when(mapper.existFcmTokenByUserIdAndToken(1L, "existing-device")).thenReturn(true);

        service.updateFcmTokenForUser(request);

        verify(mapper, never()).saveFcmToken(any());
    }

    @Test
    @DisplayName("같은 기기로 다른 사용자가 로그인하면 새 사용자로 연결 변경을 요청한다")
    void requestsRebindingForAnotherUser() {
        when(mapper.existFcmTokenByUserIdAndToken(1L, "shared-device")).thenReturn(true);
        when(mapper.existFcmTokenByUserIdAndToken(2L, "shared-device")).thenReturn(false);
        FcmTokenRequestDto nextUser = tokenRequest(2L, "shared-device");

        service.updateFcmTokenForUser(tokenRequest(1L, "shared-device"));
        service.updateFcmTokenForUser(nextUser);

        // 실제 연결 변경은 매퍼의 token 기준 upsert가 담당한다.
        verify(mapper).saveFcmToken(nextUser);
        verify(mapper, times(1)).saveFcmToken(any());
    }

    @Test
    @DisplayName("로그아웃한 기기는 토큰 삭제 대신 사용자 연결 해제를 요청한다")
    void unbindsOnlyRequestedDevice() {
        service.unbindFcmToken("logout-device");

        verify(mapper).unbindFcmToken("logout-device");
        verify(mapper, never()).deleteFcmTokens(any());
    }

    @Test
    @DisplayName("전체 알림 대상 토큰이 없으면 Firebase를 호출하지 않는다")
    void skipsBroadcastWhenNoTokensExist() {
        when(mapper.findAllTokens()).thenReturn(List.of());

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            SendPushResponseDto result = service.sendNotificationToAll(pushRequest(null));

            assertThat(result.getSuccessCount()).isZero();
            assertThat(result.getFailureCount()).isZero();
            assertThat(result.getErrorCounts()).isEmpty();
            firebase.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("개별 알림 대상 사용자의 토큰이 없으면 Firebase를 호출하지 않는다")
    void skipsUserNotificationWhenNoTokensExist() {
        when(mapper.findFcmTokenByUserId(1L)).thenReturn(List.of());

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            service.sendNotificationToUser(pushRequest(1L));

            verify(mapper).findFcmTokenByUserId(1L);
            firebase.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("전체 알림은 익명 기기와 로그인한 기기에 제목과 내용을 전달한다")
    void broadcastsToAnonymousAndSignedInDevices() throws Exception {
        when(mapper.findAllTokens()).thenReturn(List.of(
                registeredToken(null, "anonymous-device"), registeredToken(1L, "user-device")));
        stubResponses(successfulResponse(), successfulResponse());

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(messaging);

            SendPushResponseDto result = service.sendNotificationToAll(pushRequest(null));

            assertThat(result.getSuccessCount()).isEqualTo(2);
            assertThat(result.getFailureCount()).isZero();
            assertThat(result.getErrorCounts()).isEmpty();
            assertSentMessage(List.of("anonymous-device", "user-device"));
            verify(mapper, never()).deleteFcmTokens(any());
        }
    }

    @Test
    @DisplayName("개별 알림은 요청한 사용자의 여러 기기에 전달한다")
    void sendsToAllDevicesOfRequestedUser() throws Exception {
        when(mapper.findFcmTokenByUserId(1L)).thenReturn(List.of(
                registeredToken(1L, "desktop"), registeredToken(1L, "mobile")));
        stubResponses(successfulResponse(), successfulResponse());

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(messaging);

            service.sendNotificationToUser(pushRequest(1L));

            assertSentMessage(List.of("desktop", "mobile"));
            verify(mapper, never()).findAllTokens();
            verify(mapper, never()).deleteFcmTokens(any());
        }
    }

    @Test
    @DisplayName("혼합 발송 결과에서 등록 해제된 토큰만 삭제하고 인자 오류·성공·일시 실패 토큰은 유지한다")
    void removesOnlyUnregisteredTokensFromMixedResults() throws Exception {
        when(mapper.findAllTokens()).thenReturn(List.of(
                registeredToken(1L, "healthy"), registeredToken(2L, "expired"),
                registeredToken(3L, "temporary-failure"), registeredToken(4L, "invalid-argument")));
        stubResponses(successfulResponse(), failedResponse(MessagingErrorCode.UNREGISTERED),
                failedResponse(MessagingErrorCode.UNAVAILABLE), failedResponse(MessagingErrorCode.INVALID_ARGUMENT));

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(messaging);

            SendPushResponseDto result = service.sendNotificationToAll(pushRequest(null));

            assertThat(result.getSuccessCount()).isEqualTo(1);
            assertThat(result.getFailureCount()).isEqualTo(3);
            assertThat(result.getErrorCounts()).containsOnly(
                    org.assertj.core.api.Assertions.entry("UNREGISTERED", 1),
                    org.assertj.core.api.Assertions.entry("UNAVAILABLE", 1),
                    org.assertj.core.api.Assertions.entry("INVALID_ARGUMENT", 1));
            verify(mapper).deleteFcmTokens(List.of("expired"));
        }
    }

    @ParameterizedTest(name = "전체 발송={0}")
    @ValueSource(booleans = {true, false})
    @DisplayName("INVALID_ARGUMENT는 메시지 오류일 수 있으므로 전체·개별 발송에서 토큰을 삭제하지 않는다")
    void preservesTokensWhenArgumentsAreInvalid(boolean broadcast) throws Exception {
        List<FcmTokenResponseDto> tokens = List.of(
                registeredToken(1L, "desktop"), registeredToken(1L, "mobile"));
        if (broadcast) {
            when(mapper.findAllTokens()).thenReturn(tokens);
        } else {
            when(mapper.findFcmTokenByUserId(1L)).thenReturn(tokens);
        }
        // FCM이 공통 메시지의 문제로 모든 수신 대상에 같은 오류를 반환하는 경우도 보존한다.
        stubResponses(failedResponse(MessagingErrorCode.INVALID_ARGUMENT),
                failedResponse(MessagingErrorCode.INVALID_ARGUMENT));

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(messaging);

            SendPushResponseDto result = broadcast
                    ? service.sendNotificationToAll(pushRequest(null))
                    : service.sendNotificationToUser(pushRequest(1L));

            assertThat(result.getSuccessCount()).isZero();
            assertThat(result.getFailureCount()).isEqualTo(2);
            assertThat(result.getErrorCounts()).containsOnly(
                    org.assertj.core.api.Assertions.entry("INVALID_ARGUMENT", 2));

            assertSentMessage(List.of("desktop", "mobile"));
            verify(mapper, never()).deleteFcmTokens(any());
        }
    }

    @Test
    @DisplayName("Firebase 호출 자체가 실패하면 예외를 처리하고 저장된 토큰을 삭제하지 않는다")
    void preservesTokensWhenFirebaseCallFails() throws Exception {
        when(mapper.findAllTokens()).thenReturn(List.of(registeredToken(1L, "device")));
        when(messaging.sendEachForMulticast(any(MulticastMessage.class)))
                .thenThrow(mock(FirebaseMessagingException.class));

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(messaging);

            SendPushResponseDto result = service.sendNotificationToAll(pushRequest(null));
            assertThat(result.getSuccessCount()).isZero();
            assertThat(result.getFailureCount()).isEqualTo(1);
            assertThat(result.getErrorCounts()).containsOnly(
                    org.assertj.core.api.Assertions.entry("UNKNOWN", 1));

            verify(messaging).sendEachForMulticast(any(MulticastMessage.class));
            verify(mapper, never()).deleteFcmTokens(any());
        }
    }

    private FcmTokenRequestDto tokenRequest(Long userId, String token) {
        return FcmTokenRequestDto.builder().userId(userId).token(token).deviceType("WEB").build();
    }

    private FcmTokenResponseDto registeredToken(Long userId, String token) {
        FcmTokenResponseDto response = new FcmTokenResponseDto();
        response.userId = userId;
        response.token = token;
        return response;
    }

    private SendPushRequestDto pushRequest(Long userId) {
        SendPushRequestDto request = new SendPushRequestDto();
        request.userId = userId;
        request.title = "새 공지";
        request.body = "공지 내용을 확인해 주세요.";
        return request;
    }

    private SendResponse successfulResponse() {
        SendResponse response = mock(SendResponse.class);
        when(response.isSuccessful()).thenReturn(true);
        return response;
    }

    private SendResponse failedResponse(MessagingErrorCode errorCode) {
        SendResponse response = mock(SendResponse.class);
        FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
        when(response.getException()).thenReturn(exception);
        when(exception.getMessagingErrorCode()).thenReturn(errorCode);
        return response;
    }

    private void stubResponses(SendResponse... responses) throws FirebaseMessagingException {
        BatchResponse batch = mock(BatchResponse.class);
        when(batch.getResponses()).thenReturn(List.of(responses));
        when(messaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(batch);
    }

    private void assertSentMessage(List<String> expectedTokens) throws FirebaseMessagingException {
        ArgumentCaptor<MulticastMessage> captor = ArgumentCaptor.forClass(MulticastMessage.class);
        verify(messaging).sendEachForMulticast(captor.capture());
        // Firebase SDK 9.2.0은 조회 메서드를 제공하지 않아 캡처한 메시지의 필드를 확인한다.
        assertThat(captor.getValue()).extracting("tokens").isEqualTo(expectedTokens);
        assertThat(captor.getValue()).extracting("notification.title", "notification.body")
                .containsExactly("새 공지", "공지 내용을 확인해 주세요.");
    }
}
