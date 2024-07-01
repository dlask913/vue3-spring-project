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

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(findNotice.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(noticeRequestDto.getContent());
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
        noticeServiceImpl.updateNotice(savedNotice.getId(), updateNotice);

        // then
        NoticeResponseDto findNotice = noticeMapper.findAllNotices().get(0);
        assertThat(findNotice.getId()).isEqualTo(updateNotice.getId());
        assertThat(findNotice.getTitle()).isEqualTo(updateNotice.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(updateNotice.getContent());
        assertThat(findNotice.getMemberId()).isEqualTo(updateNotice.getMemberId());
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
        assertThat(noticeMapper.findAllNotices().size()).isEqualTo(0);
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
        assertThat(findNotice.getMemberId()).isEqualTo(noticeRequestDto.getMemberId());
        assertThat(findNotice.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(noticeRequestDto.getContent());
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
        assertThat(notices.size()).isEqualTo(2);
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
        assertThat(findNotices.get(0).getTitle()).isEqualTo(noticeRequestDto1.getTitle());
        assertThat(findNotices.get(0).getContent()).isEqualTo(noticeRequestDto1.getContent());
        assertThat(findNotices.get(1).getTitle()).isEqualTo(noticeRequestDto2.getTitle());
        assertThat(findNotices.get(1).getContent()).isEqualTo(noticeRequestDto2.getContent());
    }

    @Test
    @DisplayName("3페이지의 게시물 5개를 조회한다.")
    void findNoticesByPage() {
        // given
        for (int i = 0; i < 17; i++) {
            NoticeRequestDto noticeRequestDto =
                    createNoticeRequestDto(0L,"제목"+i, "내용"+i, getMemberId("limnj1@test.com"));
            noticeServiceImpl.saveNotice(noticeRequestDto);
        }

        // when
        List<NoticeResponseDto> findNotices =
                noticeServiceImpl.findNoticesByPage(3,5);

        // then
        assertThat(findNotices)
                .hasSize(5)
                .extracting(NoticeResponseDto::getTitle)
                .containsExactly("제목10", "제목11", "제목12", "제목13", "제목14");
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
            return memberMapper.findByEmail(email).getId();
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