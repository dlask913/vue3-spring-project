package com.limnj.noticeboardadmin.auth;

import com.limnj.noticeboardadmin.member.AdminMemberRequestDto;
import com.limnj.noticeboardadmin.member.MemberMapper;
import com.limnj.noticeboardadmin.util.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EmailVerificationServiceTest {

    @Autowired
    @Qualifier("concurrentEmailVerificationServiceImpl")
    private EmailVerificationService emailVerificationService;

    @Autowired
    private MemberMapper memberMapper;

    private EmailService emailService; // 실제 이메일 발송은 mock

    @Test
    @DisplayName("이메일 인증 코드 검증 성공")
    void verifyCode_success() throws Exception {
        // given
        String email = "user@example.com";
        saveMember(email);
        emailVerificationService.sendVerificationCode(email);

        // 내부 저장소(codeStore) 접근
        Map<String, String> codeStore = getPrivateField(emailVerificationService, "codeStore");
        String storedCode = codeStore.get(email);

        // when
        boolean result = emailVerificationService.verifyCode(email, storedCode);

        // then
        assertThat(result).isTrue();
    }

    private void saveMember(String email) {
        AdminMemberRequestDto requestDto = AdminMemberRequestDto.builder()
                .username("username")
                .email(email)
                .password("paaword")
                .build();
        memberMapper.saveAdminMember(requestDto);
    }

    // private 필드 접근용 유틸
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }
}