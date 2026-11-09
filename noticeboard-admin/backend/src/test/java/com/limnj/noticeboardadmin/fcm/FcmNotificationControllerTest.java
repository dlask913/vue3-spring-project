package com.limnj.noticeboardadmin.fcm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("FCM 기기 등록 요청 처리")
class FcmNotificationControllerTest {

    @Mock
    private FcmNotificationService service;

    @InjectMocks
    private FcmNotificationController controller;

    @ParameterizedTest(name = "요청 사용자 ID가 {0}이어도 익명 등록으로 전달한다")
    @NullSource
    @ValueSource(longs = 42L)
    @DisplayName("사용자 ID는 무시하고 기기 토큰과 기기 종류만 서비스에 전달한다")
    void ignoresClientUserId(Long requestedUserId) throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        String body = """
                {"userId": %s, "token": "browser-token", "deviceType": "WEB"}
                """.formatted(requestedUserId);

        mvc.perform(post("/fcm/token").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<FcmTokenRequestDto> captor = ArgumentCaptor.forClass(FcmTokenRequestDto.class);
        verify(service).updateFcmTokenForUser(captor.capture());
        assertThat(captor.getValue().getUserId()).isNull();
        assertThat(captor.getValue().getToken()).isEqualTo("browser-token");
        assertThat(captor.getValue().getDeviceType()).isEqualTo("WEB");
    }
}
