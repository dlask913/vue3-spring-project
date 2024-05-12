package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.NoticeMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NoticeServiceTest {

    @Autowired
    NoticeService noticeServiceImpl;
    @Autowired
    NoticeMapper noticeMapper;
    @Autowired
    MemberMapper memberMapper;


    @AfterEach
    void tearDown() {
        noticeMapper.deleteAll();
        memberMapper.deleteAll();
    }

    @Test
    @DisplayName("게시글을 저장한다.")
    void saveNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용", getMemberId("limnj1@test.com"));

        // when
        noticeServiceImpl.saveNotice(noticeRequestDto);

        // then
        NoticeResponseDto findNotice = noticeMapper.findAllNotices().get(0);
        Assertions.assertThat(findNotice.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        Assertions.assertThat(findNotice.getContent()).isEqualTo(noticeRequestDto.getContent());
    }

    @Test
    @DisplayName("게시글의 제목과 내용을 수정한다.")
    void updateNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용", getMemberId("limnj1@test.com"));
        noticeServiceImpl.saveNotice(noticeRequestDto);
        NoticeResponseDto savedNotice = noticeMapper.findAllNotices().get(0);
        NoticeRequestDto updateNotice = createNoticeRequestDto(savedNotice.getId(),"제목1", "내용1", noticeRequestDto.getMemberId());

        // when
        noticeServiceImpl.updateNotice(updateNotice);

        // then
        NoticeResponseDto findNotice = noticeMapper.findAllNotices().get(0);
        Assertions.assertThat(findNotice.getId()).isEqualTo(updateNotice.getId());
        Assertions.assertThat(findNotice.getTitle()).isEqualTo(updateNotice.getTitle());
        Assertions.assertThat(findNotice.getContent()).isEqualTo(updateNotice.getContent());
        Assertions.assertThat(findNotice.getMemberId()).isEqualTo(updateNotice.getMemberId());
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용", getMemberId("limnj1@test.com"));
        noticeServiceImpl.saveNotice(noticeRequestDto);
        NoticeResponseDto findNotice = noticeMapper.findAllNotices().get(0);

        // when
        noticeServiceImpl.deleteNotice(findNotice.getId());

        // then
        Assertions.assertThat(noticeMapper.findAllNotices().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글 한 개를 상세 조회한다.")
    void findNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용", getMemberId("limnj1@test.com"));
        noticeServiceImpl.saveNotice(noticeRequestDto);

        // when
        NoticeResponseDto findNotice = noticeMapper.findAllNotices().get(0);

        // then
        Assertions.assertThat(findNotice.getMemberId()).isEqualTo(noticeRequestDto.getMemberId());
        Assertions.assertThat(findNotice.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        Assertions.assertThat(findNotice.getContent()).isEqualTo(noticeRequestDto.getContent());
    }

    @Test
    @DisplayName("모든 게시글을 조회한다.")
    void findAllNoticesTest() {
        // given
        NoticeRequestDto noticeRequestDto1 = createNoticeRequestDto(0L,"제목1", "내용1", getMemberId("limnj1@test.com"));
        NoticeRequestDto noticeRequestDto2 = createNoticeRequestDto(0L,"제목2", "내용2", getMemberId("limnj1@test.com"));
        noticeServiceImpl.saveNotice(noticeRequestDto1);
        noticeServiceImpl.saveNotice(noticeRequestDto2);

        // when
        List<NoticeResponseDto> notices = noticeServiceImpl.findAllNotices();

        // then
        Assertions.assertThat(notices.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 회원이 작성한 게시글을 모두 조회한다.")
    void findNoticeByMemberId() {
        // given
        NoticeRequestDto noticeRequestDto1 = createNoticeRequestDto(0L,"제목1", "내용1", getMemberId("limnj1@test.com"));
        NoticeRequestDto noticeRequestDto2 = createNoticeRequestDto(0L,"제목2", "내용2", getMemberId("limnj1@test.com"));
        NoticeRequestDto noticeRequestDto3 = createNoticeRequestDto(0L,"제목3", "내용3", getMemberId("limnj2@test.com"));
        noticeServiceImpl.saveNotice(noticeRequestDto1);
        noticeServiceImpl.saveNotice(noticeRequestDto2);
        noticeServiceImpl.saveNotice(noticeRequestDto3);

        // when
        List<NoticeResponseDto> findNotices = noticeServiceImpl.findNoticeByMemberId(getMemberId("limnj1@test.com"));

        // then
        Assertions.assertThat(findNotices.get(0).getTitle()).isEqualTo(noticeRequestDto1.getTitle());
        Assertions.assertThat(findNotices.get(0).getContent()).isEqualTo(noticeRequestDto1.getContent());
        Assertions.assertThat(findNotices.get(1).getTitle()).isEqualTo(noticeRequestDto2.getTitle());
        Assertions.assertThat(findNotices.get(1).getContent()).isEqualTo(noticeRequestDto2.getContent());
    }

    private Long getMemberId(String email){
        MemberDto findMember = memberMapper.findByEmail(email);
        if (findMember == null) {
            MemberDto memberDto = MemberDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberDto);
            return memberMapper.findByEmail("limnj1@test.com").getId();
        }
        return findMember.getId();
    }


    private static NoticeRequestDto createNoticeRequestDto(Long id,String title, String content, Long memberId) {
        return NoticeRequestDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .memberId(memberId)
                .build();
    }
}