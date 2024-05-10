package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.LoginDto;
import com.example.noticeboardservice.dto.LoginResponseDto;
import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.exception.MemberNotFoundException;
import com.example.noticeboardservice.exception.PasswordMismatchException;
import com.example.noticeboardservice.mapper.MemberMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

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
        MemberDto memberDto = createMemberDto(0L,"register@test.com", "1234", "limnj1");

        // when
        memberServiceImpl.registerMember(memberDto);

        // then
        MemberDto findMember = memberMapper.findByEmail(memberDto.getEmail());
        Assertions.assertThat(findMember.getEmail()).isEqualTo(memberDto.getEmail());
        Assertions.assertThat(findMember.getPassword()).isEqualTo(memberDto.getPassword());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(memberDto.getUsername());
    }

    @Test
    @DisplayName("로그인 시 회원 정보에 대한 비밀번호가 같아야 한다.")
    void loginWithPasswordMismatchExceptionTest() {
        // given
        MemberDto memberDto = createMemberDto(0L,"register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberDto);
        LoginDto loginDto = LoginDto.builder()
                .email(memberDto.getEmail())
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
        MemberDto memberDto = createMemberDto(0L, "register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberDto);
        MemberDto findMember = memberServiceImpl.findByEmail(memberDto.getEmail()).orElseThrow();
        MemberDto updateDto = createMemberDto(findMember.getId(), findMember.getEmail(), "12345", "limnj2");

        // when
        memberServiceImpl.updateMember(updateDto);

        // then
        MemberDto updateMember = memberMapper.findByEmail(memberDto.getEmail());
        Assertions.assertThat(updateMember.getPassword()).isEqualTo(updateDto.getPassword());
        Assertions.assertThat(updateMember.getUsername()).isEqualTo(updateDto.getUsername());
    }

    @Test
    @DisplayName("회원 탈퇴 한다.")
    void deleteMemberTest() {
        // given
        MemberDto memberDto = createMemberDto(0L,"register@test.com", "1234", "limnj1");
        memberServiceImpl.registerMember(memberDto);
        MemberDto findMember = memberMapper.findByEmail(memberDto.getEmail());

        // when
        memberServiceImpl.deleteMember(findMember.getId());

        // then
        Assertions.assertThat(memberMapper.findAllMembers().size()).isEqualTo(0);
    }

    private static MemberDto createMemberDto(Long memberId, String email, String password, String username) {
        return MemberDto.builder()
                .id(memberId)
                .email(email)
                .password(password)
                .username(username)
                .build();
    }
}