package com.limnj.noticeboardadmin.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("FCM 서비스의 Spring 트랜잭션 경계")
class FcmNotificationTransactionTest {
    private FcmNotificationMapper mapper;
    private DataSource dataSource;
    private FcmNotificationService service;

    @BeforeEach
    void setUp() {
        mapper = mock(FcmNotificationMapper.class);
        dataSource = mock(DataSource.class);
        // 실제 Spring 프록시와 트랜잭션 매니저를 적용하되 DB와 Firebase 연결은 대체한다.
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(new DataSourceTransactionManager(dataSource));
        interceptor.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        ProxyFactory factory = new ProxyFactory(new FcmNotificationService(mapper));
        factory.addAdvice(interceptor);
        service = (FcmNotificationService) factory.getProxy();
    }

    @ParameterizedTest(name = "전체 발송={0}")
    @ValueSource(booleans = {true, false})
    @DisplayName("외부 트랜잭션이 없는 발송 요청은 Firebase 호출 중 DB 트랜잭션을 열지 않는다")
    void sendsOutsideTransaction(boolean broadcast) throws Exception {
        FcmTokenResponseDto token = new FcmTokenResponseDto();
        token.token = "device";
        if (broadcast) {
            when(mapper.findAllTokens()).thenReturn(List.of(token));
        } else {
            when(mapper.findFcmTokenByUserId(1L)).thenReturn(List.of(token));
        }
        SendPushRequestDto request = new SendPushRequestDto();
        request.userId = 1L;
        request.title = "제목";
        request.body = "본문";
        FirebaseMessaging messaging = mock(FirebaseMessaging.class);
        SendResponse success = mock(SendResponse.class);
        when(success.isSuccessful()).thenReturn(true);
        BatchResponse batch = mock(BatchResponse.class);
        when(batch.getResponses()).thenReturn(List.of(success));
        when(messaging.sendEachForMulticast(any(MulticastMessage.class))).thenAnswer(invocation -> {
            assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
            return batch;
        });

        try (MockedStatic<FirebaseMessaging> firebase = mockStatic(FirebaseMessaging.class)) {
            firebase.when(FirebaseMessaging::getInstance).thenReturn(messaging);
            SendPushResponseDto result = broadcast
                    ? service.sendNotificationToAll(request) : service.sendNotificationToUser(request);
            assertThat(result.getSuccessCount()).isEqualTo(1);
            verifyNoInteractions(dataSource);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"register", "unbind", "unbindForUser"})
    @DisplayName("등록과 연결 해제는 기존처럼 트랜잭션 안에서 실행하고 커밋한다")
    void keepsTransactionsForWrites(String operation) throws Exception {
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);

        if (operation.equals("register")) {
            doAnswer(invocation -> {
                assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
                return null;
            }).when(mapper).saveFcmToken(any());
            service.updateFcmTokenForUser(FcmTokenRequestDto.builder().token("device").build());
            verify(mapper).saveFcmToken(any());
        } else if (operation.equals("unbind")) {
            doAnswer(invocation -> {
                assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
                return null;
            }).when(mapper).unbindFcmToken("device");
            service.unbindFcmToken("device");
            verify(mapper).unbindFcmToken("device");
        } else {
            doAnswer(invocation -> {
                assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
                return null;
            }).when(mapper).unbindFcmTokenForUser("admin", "device");
            service.unbindFcmTokenForUser("admin", "device");
            verify(mapper).unbindFcmTokenForUser("admin", "device");
        }

        verify(connection).commit();
        verify(connection).close();
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
    }
}
