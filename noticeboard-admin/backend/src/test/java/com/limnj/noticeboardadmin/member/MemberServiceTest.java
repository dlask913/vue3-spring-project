package com.limnj.noticeboardadmin.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired MemberService memberServiceImpl;
    @Autowired MemberMapper memberMapper;

    @Test
    @DisplayName("관리자로 회원 가입을 한다.")
    void saveAdminMember() {
        // given
        AdminMemberRequestDto requestDto = AdminMemberRequestDto.builder()
                .username("adminUser")
                .password("user1234")
                .build();

        // when
        memberServiceImpl.saveAdminMember(requestDto);

        // then
        AdminMemberResponseDto findMember = memberMapper.findMemberById(requestDto.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(requestDto.getUsername());
        Assertions.assertThat(findMember.getPassword()).isEqualTo(requestDto.getPassword());
    }
}