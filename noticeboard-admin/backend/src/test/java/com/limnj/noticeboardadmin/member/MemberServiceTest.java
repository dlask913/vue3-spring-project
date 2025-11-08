package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.exception.PasswordMismatchException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired MemberService memberServiceImpl;
    @Autowired MemberMapper memberMapper;

    @Test
    @DisplayName("관리자로 회원 가입을 한다.")
    void saveAdminMember_success() {
        // given
        AdminMemberRequestDto requestDto = AdminMemberRequestDto.builder()
                .email("limnj@test.com")
                .username("adminUser")
                .password("user1234")
                .build();

        // when
        memberServiceImpl.saveAdminMember(requestDto);

        // then
        AdminMemberResponseDto findMember = memberMapper.findMemberById(requestDto.getId()).orElseThrow();
        Assertions.assertThat(findMember.getUsername()).isEqualTo(requestDto.getUsername());
        Assertions.assertThat(findMember.getPassword()).isEqualTo(requestDto.getPassword());
    }

    @Test
    @DisplayName("회원 PK 로 회원을 단일 조회한다")
    public void findMemberById_success(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        memberServiceImpl.saveAdminMember(requestDto);

        // when
        AdminMemberResponseDto findMember = memberServiceImpl.findMemberById(requestDto.getId());

        // then
        Assertions.assertThat(findMember.getUsername()).isEqualTo(requestDto.getUsername());
        Assertions.assertThat(findMember.getEmail()).isEqualTo(requestDto.getEmail());
        Assertions.assertThat(findMember.getPassword()).isEqualTo(requestDto.getPassword());
    }

    @Test
    @DisplayName("존재하지 않는 회원 PK 로 회원을 조회할 경우 예외가 발생한다.")
    public void findMemberById_failure(){
        // given
        Long memberId = 1L;

        // when // then
        assertThatThrownBy(() -> memberServiceImpl.findMemberById(memberId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("관리자 회원 로그인에 성공한다.")
    public void loginAdminMember_success(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        memberServiceImpl.saveAdminMember(requestDto);

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email("limnj@test.com")
                .username("limnj")
                .password("1234")
                .build();

        // when
        LoginResponseDto loginResponseDto = memberServiceImpl.loginAdminMember(loginDto);

        // then
        Assertions.assertThat(loginResponseDto.getUsername()).isEqualTo(requestDto.getUsername());
        Assertions.assertThat(loginResponseDto.getEmail()).isEqualTo(requestDto.getEmail());
    }

    @Test
    @DisplayName("관리자 회원의 비밀번호가 잘못된 경우 예외가 발생한다.")
    public void loginAdminMember_failure(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        memberServiceImpl.saveAdminMember(requestDto);

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email("limnj@test.com")
                .username("limnj")
                .password("12345")
                .build();

        // when // then
        assertThatThrownBy(() -> memberServiceImpl.loginAdminMember(loginDto))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessage("비밀번호가 맞지 않습니다");
    }

    @Test
    @DisplayName("관리자 2FA QR 코드 생성 시 secretKey 를 저장한다")
    public void updateSecretKeyByEmail_success(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        String secretKey = gAuth.createCredentials().getKey();
        memberServiceImpl.saveAdminMember(requestDto);

        // when
        memberServiceImpl.updateSecretKeyByEmail(requestDto.getEmail(), secretKey);

        // then
        String findSecretKey = memberMapper.findSecretKeyByEmail(requestDto.getEmail());
        Assertions.assertThat(findSecretKey).isEqualTo(secretKey);
    }



    private AdminMemberRequestDto getMember(String username, String email, String password) {
        return AdminMemberRequestDto.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
    }
}