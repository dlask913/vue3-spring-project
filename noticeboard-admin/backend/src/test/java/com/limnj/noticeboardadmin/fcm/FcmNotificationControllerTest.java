package com.limnj.noticeboardadmin.fcm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@DisplayName("FCM 기기 등록 요청 처리")
class FcmNotificationControllerTest {

    @Mock
    private FcmNotificationService service;

    @InjectMocks
    private FcmNotificationController controller;

    @ParameterizedTest
    @ValueSource(strings = {
            "{}", "{\"title\":\" \",\"body\":\"내용\"}",
            "{\"title\":\"제목\",\"body\":\"\"}", "{\"title\":\"제목\"}"
    })
    @DisplayName("제목이나 본문이 누락되거나 공백이면 발송 전에 400을 반환한다")
    void rejectsMissingContent(String body) throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        for (String path : new String[]{"/push/all", "/push/user"}) {
            mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "0", "-1"})
    @DisplayName("개별 발송 대상 ID가 없거나 양수가 아니면 발송하지 않는다")
    void rejectsInvalidUserId(String userId) throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mvc.perform(post("/push/user").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":%s,"title":"제목","body":"내용"}
                                """.formatted(userId)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/push/all", "/push/user"})
    @DisplayName("발송 결과의 성공·실패 건수와 오류 코드별 건수를 응답한다")
    void returnsSendResult(String path) throws Exception {
        var result = new SendPushResponseDto(2, 1, Map.of("INVALID_ARGUMENT", 1));
        if (path.equals("/push/all")) {
            when(service.sendNotificationToAll(any())).thenReturn(result);
        } else {
            when(service.sendNotificationToUser(any())).thenReturn(result);
        }
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":1,"title":"제목","body":"내용"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.failureCount").value(1))
                .andExpect(jsonPath("$.errorCounts.INVALID_ARGUMENT").value(1));
    }

    @Test
    @DisplayName("한글의 UTF-8 바이트 수를 기준으로 4000바이트 경계를 검사한다")
    void validatesUtf8SizeBoundary() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        when(service.sendNotificationToAll(any()))
                .thenReturn(new SendPushResponseDto(0, 0, Map.of()));
        // 제목 1바이트 + 본문 3999바이트는 허용한다. 한 글자를 더하면 거절한다.
        String body = "가".repeat(1333);
        mvc.perform(post("/push/all").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"a\",\"body\":\"" + body + "\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/push/all").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"aa\",\"body\":\"" + body + "\"}"))
                .andExpect(status().isBadRequest());
        verify(service).sendNotificationToAll(any());
    }

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
