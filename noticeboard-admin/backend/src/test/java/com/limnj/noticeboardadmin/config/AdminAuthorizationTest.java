package com.limnj.noticeboardadmin.config;

import com.limnj.noticeboardadmin.auth.AuthController;
import com.limnj.noticeboardadmin.auth.EmailVerificationService;
import com.limnj.noticeboardadmin.auth.QrVerificationService;
import com.limnj.noticeboardadmin.config.handler.CustomAccessDeniedHandler;
import com.limnj.noticeboardadmin.fcm.FcmNotificationController;
import com.limnj.noticeboardadmin.fcm.FcmNotificationService;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import com.limnj.noticeboardadmin.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, FcmNotificationController.class})
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class})
class AdminAuthorizationTest {
    @Autowired MockMvc mvc;
    @MockBean MemberService members;
    @MockBean JwtTokenUtil jwt;
    @MockBean(name = "concurrentEmailVerificationServiceImpl") EmailVerificationService emailVerification;
    @MockBean QrVerificationService qrVerification;
    @MockBean FcmNotificationService notifications;

    @Test
    void enrollmentRequiresLogin() throws Exception {
        mvc.perform(post("/qr-register")).andExpect(status().isUnauthorized());
    }

    @Test
    void onlyAdminMayBroadcast() throws Exception {
        mvc.perform(post("/push/all").with(user("manager").roles("MANAGER")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/push/all").with(user("admin").roles("ADMIN"))
                        .contentType("application/json").content("{}"))
                .andExpect(status().isOk());
        verify(notifications).sendNotificationToAll(any());
    }

    @Test
    void unlistedRoutesAndMethodsAreDeniedEvenForAdmins() throws Exception {
        mvc.perform(get("/unlisted").with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/push/all").with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }
}
