package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.exception.PasswordMismatchException;
import com.limnj.noticeboardadmin.exception.QrNotGenerateException;
import com.limnj.noticeboardadmin.exception.UserEmailNotFoundException;
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
    public void loginWithCredentials_success(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        memberServiceImpl.saveAdminMember(requestDto);

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email("limnj@test.com")
                .username("limnj")
                .password("1234")
                .build();

        // when
        LoginResponseDto loginResponseDto = memberServiceImpl.loginWithCredentials(loginDto);

        // then
        Assertions.assertThat(loginResponseDto.getUsername()).isEqualTo(requestDto.getUsername());
        Assertions.assertThat(loginResponseDto.getEmail()).isEqualTo(requestDto.getEmail());
    }

    @Test
    @DisplayName("관리자 회원의 비밀번호가 잘못된 경우 예외가 발생한다.")
    public void loginWithCredentials_failure(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        memberServiceImpl.saveAdminMember(requestDto);

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email("limnj@test.com")
                .username("limnj")
                .password("12345")
                .build();

        // when // then
        assertThatThrownBy(() -> memberServiceImpl.loginWithCredentials(loginDto))
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
        String findSecretKey = memberMapper.findSecretKeyByEmail(requestDto.getEmail()).orElseThrow();
        Assertions.assertThat(findSecretKey).isEqualTo(secretKey);
    }


    @Test
    @DisplayName("존재하지 않는 회원 이메일로 2FA QR 코드를 생성한다")
    public void updateSecretKeyByEmail_failure(){
        // given
        String email = "limnj@test.com";
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        String secretKey = gAuth.createCredentials().getKey();

        // when // then
        assertThatThrownBy(() -> memberServiceImpl.updateSecretKeyByEmail(email, secretKey))
                .isInstanceOf(UserEmailNotFoundException.class)
                .hasMessage("존재하지 않는 회원 이메일입니다.");
    }

    @Test
    @DisplayName("회원 이메일로 저장되어있는 2FA QR secretKey 를 조회한다")
    public void findSecretKeyByEmail_success(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        String secretKey = gAuth.createCredentials().getKey();
        memberServiceImpl.saveAdminMember(requestDto);
        memberServiceImpl.updateSecretKeyByEmail(requestDto.getEmail(), secretKey);

        // when
        String secretKeyByEmail = memberServiceImpl.findSecretKeyByEmail(requestDto.getEmail());

        // then
        Assertions.assertThat(secretKeyByEmail).isEqualTo(secretKey);
    }

    @Test
    @DisplayName("QR 을 생성하지 않은 회원이 QR 검증을 요청하면 예외가 발생한다")
    public void findSecretKeyByEmail_failure(){
        // given
        AdminMemberRequestDto requestDto = getMember("limnj", "limnj@test.com", "1234");
        memberServiceImpl.saveAdminMember(requestDto);

        // when // then
        assertThatThrownBy(() -> memberServiceImpl.findSecretKeyByEmail(requestDto.getEmail()))
                .isInstanceOf(QrNotGenerateException.class)
                .hasMessage("QR 생성을 하지 않은 회원입니다.");
    }

    private AdminMemberRequestDto getMember(String username, String email, String password) {
        return AdminMemberRequestDto.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
    }
}