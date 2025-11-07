package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.member.AddressRequestDto;
import com.example.noticeboardservice.dto.member.LoginRequestDto;
import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import com.example.noticeboardservice.exception.member.MemberDuplicateException;
import com.example.noticeboardservice.exception.member.MemberNotFoundException;
import com.example.noticeboardservice.exception.member.PasswordMismatchException;
import com.example.noticeboardservice.mapper.member.MemberMapper;
import com.example.noticeboardservice.service.member.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberMapper memberMapper;
    @Autowired
    MemberService memberServiceImpl;

    @Test
    @DisplayName("회원 가입을 한다.")
    void memberRegisterTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1", "서울 특별시");

        // when
        memberServiceImpl.registerMember(memberRequestDto);

        // then
        MemberResponseDto findMember = memberMapper.findMemberByEmail(memberRequestDto.getEmail());
        Assertions.assertThat(findMember.email()).isEqualTo(memberRequestDto.getEmail());
        Assertions.assertThat(findMember.password()).isEqualTo(memberRequestDto.getPassword());
        Assertions.assertThat(findMember.username()).isEqualTo(memberRequestDto.getUsername());
    }

    @Test
    @DisplayName("로그인 시 회원 정보에 대한 비밀번호가 같아야 한다.")
    void loginWithPasswordMismatchExceptionTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1", "서울 특별시");
        memberServiceImpl.registerMember(memberRequestDto);
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(memberRequestDto.getEmail())
                .password("123X")
                .build();

        // when // then
        assertThrows(PasswordMismatchException.class, () -> {
            memberServiceImpl.login(loginRequestDto);
        }, "비밀번호를 확인해주세요.");
    }

    @Test
    @DisplayName("로그인은 회원 가입을 한 유저만 가능하다.")
    void findMemberTest() {
        // given
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("register@test.com")
                .password("1234")
                .build();

        // when // then
        assertThrows(MemberNotFoundException.class, () -> {
            memberServiceImpl.login(loginRequestDto);
        }, "회원 정보가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원 비밀번호와 이름을 수정한다.")
    void updateMemberTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L, "register@test.com", "1234", "limnj1", "서울 특별시");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto findMember = memberServiceImpl.findMemberByEmail(memberRequestDto.getEmail()).orElseThrow();
        MemberRequestDto updateDto = createMemberDto(findMember.id(), findMember.email(), "12345", "limnj2", "서울 특별시");

        // when
        memberServiceImpl.updateMember(updateDto, null);

        // then
        MemberResponseDto updateMember = memberMapper.findMemberByEmail(memberRequestDto.getEmail());
        Assertions.assertThat(updateMember.password()).isEqualTo(updateDto.getPassword());
        Assertions.assertThat(updateMember.username()).isEqualTo(updateDto.getUsername());
    }

    @Test
    @DisplayName("회원 탈퇴 한다.")
    void deleteMemberTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1", "서울 특별시");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto findMember = memberMapper.findMemberByEmail(memberRequestDto.getEmail());

        // when
        memberServiceImpl.deleteMember(findMember.id());

        // then
        Assertions.assertThat(memberMapper.findAllMembers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("회원 PK로 디폴트 이미지가 저장되어있는 회원 정보를 조회한다")
    void findByMemberIdTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L,"register@test.com", "1234", "limnj1", "서울 특별시");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto member = memberMapper.findMemberByEmail(memberRequestDto.getEmail());

        // when
        MemberResponseDto findMember = memberServiceImpl.findMember(member.id());

        // then
        Assertions.assertThat(findMember.email()).isEqualTo(memberRequestDto.getEmail());
        Assertions.assertThat(findMember.username()).isEqualTo(memberRequestDto.getUsername());
        Assertions.assertThat(findMember.password()).isEqualTo(memberRequestDto.getPassword());
        Assertions.assertThat(findMember.imgUrl()).isEqualTo("/image/memberDefaultImg.jpg");
    }

    @Test
    @DisplayName("회원 이미지를 수정한다.")
    void updateMemberImageTest() {
        // given
        MemberRequestDto memberRequestDto = createMemberDto(0L, "register@test.com", "1234", "limnj1", "서울 특별시");
        memberServiceImpl.registerMember(memberRequestDto);
        MemberResponseDto findMember = memberServiceImpl.findMemberByEmail(memberRequestDto.getEmail()).orElseThrow();
        MemberRequestDto updateDto = createMemberDto(findMember.id(), memberRequestDto.getEmail(), memberRequestDto.getPassword(), memberRequestDto.getUsername(), "서울 특별시");
        MockMultipartFile memberImg = new MockMultipartFile(
                "회원 이미지",
                "memberImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "memberImg!".getBytes()
        );

        // when
        memberServiceImpl.updateMember(updateDto, memberImg);

        // then
        MemberResponseDto updateMember = memberMapper.findMember(findMember.id());
        Assertions.assertThat(updateMember.imgUrl()).startsWith("/files/member/");
    }

    @Test
    @DisplayName("회원 가입 시 이미 사용중인 이메일은 사용할 수 없다.")
    void MemberDuplicateExceptionTest() {
        // given
        MemberRequestDto memberRequestDto1 = createMemberDto(0L,"register1@test.com", "1234", "limnj1", "서울 특별시");
        MemberRequestDto memberRequestDto2 = createMemberDto(0L,"register1@test.com", "1234", "limnj1", "서울 특별시");
        memberMapper.insertMember(memberRequestDto1);

        // when // then
        assertThrows(MemberDuplicateException.class, () -> {
            memberServiceImpl.registerMember(memberRequestDto2);
        }, "이미 사용중인 이메일입니다.");
    }

    private static MemberRequestDto createMemberDto(Long memberId, String email, String password, String username, String address) {
        AddressRequestDto addressDto = AddressRequestDto.builder()
                .roadAddressName("roadAddressName")
                .latitude("11")
                .longitude("11")
                .addressName(address)
                .build();
        return MemberRequestDto.builder()
                .id(memberId)
                .email(email)
                .password(password)
                .username(username)
                .address(addressDto)
                .build();
    }
}