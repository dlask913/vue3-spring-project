package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.LoginDto;
import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import com.example.noticeboardservice.exception.MemberNotFoundException;
import com.example.noticeboardservice.exception.PasswordMismatchException;
import com.example.noticeboardservice.mapper.MemberMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    MemberMapper memberMapper;
    @Autowired
    MemberService memberServiceImpl;

    @AfterEach
    void tearDown() {
        memberMapper.deleteAll();
    }

    @Test
    @DisplayName("회원 가입을 한다.")
    void memberRegisterTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1");

        // when
        memberServiceImpl.registerMember(memberRequestDto);

        // then
        MemberResponseDto findMember = memberMapper.findByEmail(memberRequestDto.getEmail());
        Assertions.assertThat(findMember.getEmail()).isEqualTo(memberRequestDto.getEmail());
        Assertions.assertThat(findMember.getPassword()).isEqualTo(memberRequestDto.getPassword());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(memberRequestDto.getUsername());
    }

    @Test
    @DisplayName("로그인 시 회원 정보에 대한 비밀번호가 같아야 한다.")
    void loginWithPasswordMismatchExceptionTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberRequestDto);
        LoginDto loginDto = LoginDto.builder()
                .email(memberRequestDto.getEmail())
                .password("123X")
                .build();

        // when // then
        assertThrows(PasswordMismatchException.class, () -> {
            memberServiceImpl.login(loginDto);
        }, "비밀번호를 확인해주세요.");
    }

    @Test
    @DisplayName("로그인은 회원 가입을 한 유저만 가능하다.")
    void findMemberTest() {
        // given
        LoginDto loginDto = LoginDto.builder()
                .email("register@test.com")
                .password("1234")
                .build();

        // when // then
        assertThrows(MemberNotFoundException.class, () -> {
            memberServiceImpl.login(loginDto);
        }, "회원 정보가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 비밀번호와 이름을 수정한다.")
    void updateMemberTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L, "register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto findMember = memberServiceImpl.findByEmail(memberRequestDto.getEmail()).orElseThrow();
        MemberRequestDto updateDto = createMemberDto(findMember.getId(), findMember.getEmail(), "12345", "limnj2");

        // when
        memberServiceImpl.updateMember(updateDto, null);

        // then
        MemberResponseDto updateMember = memberMapper.findByEmail(memberRequestDto.getEmail());
        Assertions.assertThat(updateMember.getPassword()).isEqualTo(updateDto.getPassword());
        Assertions.assertThat(updateMember.getUsername()).isEqualTo(updateDto.getUsername());
    }

    @Test
    @DisplayName("회원 탈퇴 한다.")
    void deleteMemberTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto findMember = memberMapper.findByEmail(memberRequestDto.getEmail());

        // when
        memberServiceImpl.deleteMember(findMember.getId());

        // then
        Assertions.assertThat(memberMapper.findAllMembers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("회원 PK로 디폴트 이미지가 저장되어있는 회원 정보를 조회한다")
    void findByMemberIdTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto member = memberMapper.findByEmail(memberRequestDto.getEmail());

        // when
        MemberResponseDto findMember = memberServiceImpl.findMember(member.getId());

        // then
        Assertions.assertThat(findMember.getEmail()).isEqualTo(memberRequestDto.getEmail());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(memberRequestDto.getUsername());
        Assertions.assertThat(findMember.getPassword()).isEqualTo(memberRequestDto.getPassword());
        Assertions.assertThat(findMember.getImgUrl()).isEqualTo("/image/memberDefaultImg.jpg");
    }

    @Test
    @DisplayName("회원 이미지를 수정한다.")
    void updateMemberImageTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L, "register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto findMember = memberServiceImpl.findByEmail(memberRequestDto.getEmail()).orElseThrow();
        MemberRequestDto updateDto = createMemberDto(findMember.getId(), memberRequestDto.getEmail(), memberRequestDto.getPassword(), memberRequestDto.getUsername());
        MockMultipartFile memberImg = new MockMultipartFile(
                "회원 이미지",
                "memberImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "memberImg!".getBytes()
        );

        // when
        memberServiceImpl.updateMember(updateDto, memberImg);

        // then
        MemberResponseDto updateMember = memberMapper.findMember(findMember.getId());
        Assertions.assertThat(updateMember.getImgUrl()).startsWith("/images/member/");
    }

    private static MemberRequestDto createMemberDto(Long memberId, String email, String password, String username) {
        return MemberRequestDto.builder()
                .id(memberId)
                .email(email)
                .password(password)
                .username(username)
                .build();
    }
}