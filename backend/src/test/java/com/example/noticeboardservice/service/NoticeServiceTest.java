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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용",
                getMemberId("limnj1@test.com", "limnj"));

        // when
        noticeServiceImpl.saveNotice(noticeRequestDto);

        // then
        NoticeResponseDto findNotice = noticeMapper.findAllNotices(Map.of()).get(0);
        assertThat(findNotice.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(noticeRequestDto.getContent());
    }

    @Test
    @DisplayName("게시글의 제목과 내용을 수정한다.")
    void updateNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용",
                getMemberId("limnj1@test.com", "limnj"));
        noticeMapper.insertNotice(noticeRequestDto);
        NoticeResponseDto savedNotice = noticeMapper.findAllNotices(Map.of()).get(0);
        NoticeRequestDto updateNotice = createNoticeRequestDto(savedNotice.getId(),"제목1", "내용1", noticeRequestDto.getMemberId());

        // when
        noticeServiceImpl.updateNotice(savedNotice.getId(), updateNotice);

        // then
        NoticeResponseDto findNotice = noticeMapper.findAllNotices(Map.of()).get(0);
        assertThat(findNotice.getId()).isEqualTo(updateNotice.getId());
        assertThat(findNotice.getTitle()).isEqualTo(updateNotice.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(updateNotice.getContent());
        assertThat(findNotice.getMemberId()).isEqualTo(updateNotice.getMemberId());
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용",
                getMemberId("limnj1@test.com", "limnj"));
        noticeMapper.insertNotice(noticeRequestDto);
        NoticeResponseDto findNotice = noticeMapper.findAllNotices(Map.of()).get(0);

        // when
        noticeServiceImpl.deleteNotice(findNotice.getId());

        // then
        assertThat(noticeMapper.findAllNotices(Map.of()).size()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글 한 개를 상세 조회한다.")
    void findNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용",
                getMemberId("limnj1@test.com", "limnj"));
        noticeMapper.insertNotice(noticeRequestDto);

        // when
        NoticeResponseDto findNotice = noticeServiceImpl.findAllNotices(Map.of()).get(0);

        // then
        assertThat(findNotice.getMemberId()).isEqualTo(noticeRequestDto.getMemberId());
        assertThat(findNotice.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(noticeRequestDto.getContent());
        assertThat(findNotice.getViewCount()).isEqualTo(0); // 조회수 초기값 0
    }

    @Test
    @DisplayName("게시글 조회수를 올린다.")
    void incrementViewCountOfNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용",
                getMemberId("limnj1@test.com", "limnj"));
        noticeMapper.insertNotice(noticeRequestDto);
        NoticeResponseDto savedNotice = noticeMapper.findAllNotices(Map.of()).get(0); // 저장한 게시글 조회

        // when
        NoticeResponseDto findNotice = noticeServiceImpl.findNotice(savedNotice.getId(), "test");

        // then
        assertThat(findNotice.getViewCount()).isEqualTo(1); // 조회수 초기값 0
    }

    @Test
    @DisplayName("내가 쓴 글은 조회수가 올라가지 않는다.")
    void incrementViewCountOfMyNoticeTest() {
        // given
        NoticeRequestDto noticeRequestDto = createNoticeRequestDto(0L,"제목", "내용",
                getMemberId("limnj1@test.com", "limnj"));
        noticeMapper.insertNotice(noticeRequestDto);
        NoticeResponseDto savedNotice = noticeMapper.findAllNotices(Map.of()).get(0); // 저장한 게시글 조회

        // when
        NoticeResponseDto findNotice = noticeServiceImpl.findNotice(savedNotice.getId(), "limnj1@test.com");

        // then
        assertThat(findNotice.getViewCount()).isEqualTo(0); // 조회수 초기값 0 유지
    }

    @Test
    @DisplayName("모든 게시글을 조회한다.")
    void findAllNoticesTest() {
        // given
        NoticeRequestDto noticeRequestDto1 = createNoticeRequestDto(0L,"제목1", "내용1", getMemberId("limnj1@test.com", "limnj"));
        NoticeRequestDto noticeRequestDto2 = createNoticeRequestDto(0L,"제목2", "내용2", getMemberId("limnj1@test.com", "limnj"));
        noticeMapper.insertNotice(noticeRequestDto1);
        noticeMapper.insertNotice(noticeRequestDto2);

        // when
        List<NoticeResponseDto> notices = noticeServiceImpl.findAllNotices(Map.of());

        // then
        assertThat(notices.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 회원이 작성한 게시글을 모두 조회한다.")
    void findNoticeByMemberIdTest() {
        // given
        NoticeRequestDto noticeRequestDto1 = createNoticeRequestDto(0L,"제목1", "내용1", getMemberId("limnj1@test.com", "limnj1"));
        NoticeRequestDto noticeRequestDto2 = createNoticeRequestDto(0L,"제목2", "내용2", getMemberId("limnj1@test.com", "limnj1"));
        NoticeRequestDto noticeRequestDto3 = createNoticeRequestDto(0L,"제목3", "내용3", getMemberId("limnj2@test.com", "limnj2"));
        noticeMapper.insertNotice(noticeRequestDto1);
        noticeMapper.insertNotice(noticeRequestDto2);
        noticeMapper.insertNotice(noticeRequestDto3);

        // when
        List<NoticeResponseDto> findNotices = noticeServiceImpl.findNoticeByMemberId(getMemberId("limnj1@test.com", "limnj1"));

        // then
        assertThat(findNotices.get(0).getTitle()).isEqualTo(noticeRequestDto1.getTitle());
        assertThat(findNotices.get(0).getContent()).isEqualTo(noticeRequestDto1.getContent());
        assertThat(findNotices.get(1).getTitle()).isEqualTo(noticeRequestDto2.getTitle());
        assertThat(findNotices.get(1).getContent()).isEqualTo(noticeRequestDto2.getContent());
    }

    @Test
    @DisplayName("3페이지의 게시물 5개를 조회한다.")
    void findNoticesByPageTest() {
        // given
        for (int i = 0; i < 17; i++) {
            NoticeRequestDto noticeRequestDto =
                    createNoticeRequestDto(0L,"제목"+i, "내용"+i, getMemberId("limnj1@test.com", "limnj"));
            noticeMapper.insertNotice(noticeRequestDto);
        }

        // when
        List<NoticeResponseDto> findNotices =
                noticeServiceImpl.searchNoticeByPage(3,5, Map.of());

        // then
        assertThat(findNotices)
                .hasSize(5)
                .extracting(NoticeResponseDto::getTitle)
                .containsExactly("제목10", "제목11", "제목12", "제목13", "제목14");
    }

    @Test
    @DisplayName("저장된 게시물들 중 작성자의 username 에 limnj 가 포함된 게시물들을 검색한다.")
    void searchNoticesByUsernameTest() {
        // given
        for (int i = 0; i < 3; i++) { // username 에 limnj 가 포함되는 경우
            NoticeRequestDto noticeRequestDto =
                    createNoticeRequestDto(0L,"제목"+i, "내용"+i, getMemberId("limnj"+i+"@test.com", "limnj"+i));
            noticeMapper.insertNotice(noticeRequestDto);
        }
        for (int i = 0; i < 6; i++) { // username 에 limnj 가 포함되지 않는 경우
            NoticeRequestDto noticeRequestDto =
                    createNoticeRequestDto(0L,"제목"+i, "내용"+i, getMemberId("happy"+i+"@test.com", "happy"+i));
            noticeMapper.insertNotice(noticeRequestDto);
        }
        Map<String, String> params = new HashMap<>();
        params.put("username","limnj");

        // when - page 및 limit 은 default 값
        List<NoticeResponseDto> findNotices = noticeServiceImpl.searchNoticeByPage(1,5, params);

        // then
        assertThat(findNotices)
                .hasSize(3)
                .extracting(NoticeResponseDto::getUsername)
                .containsExactly("limnj0", "limnj1", "limnj2");
    }

    @Test
    @DisplayName("저장된 게시물들 중 게시글 제목에 '인사' 가 포함된 게시물들을 검색한다.")
    void searchNoticesByTitleTest() {
        // given
        for (int i = 4; i < 7; i++) { // 게시글 제목에 '인사'가 포함되는 경우
            NoticeRequestDto noticeRequestDto =
                    createNoticeRequestDto(0L,"인사합니다."+i, "내용"+i, getMemberId("limnj"+i+"@test.com", "limnj"+i));
            noticeMapper.insertNotice(noticeRequestDto);
        }
        for (int i = 0; i < 6; i++) { // 게시글 제목에 '인사'가 포함되지 않는 경우
            NoticeRequestDto noticeRequestDto =
                    createNoticeRequestDto(0L,"제목"+i, "내용"+i, getMemberId("happy"+i+"@test.com", "happy"+i));
            noticeMapper.insertNotice(noticeRequestDto);
        }
        Map<String, String> params = new HashMap<>();
        params.put("title","인사");

        // when - page 및 limit 은 default 값
        List<NoticeResponseDto> findNotices = noticeServiceImpl.searchNoticeByPage(1,5, params);

        // then
        assertThat(findNotices)
                .hasSize(3)
                .extracting(NoticeResponseDto::getTitle)
                .containsExactly("인사합니다.4", "인사합니다.5", "인사합니다.6");
    }

    private Long getMemberId(String email, String username){
        MemberDto findMember = memberMapper.findByEmail(email);
        if (findMember == null) {
            MemberDto memberDto = MemberDto.builder()
                    .email(email)
                    .password("1234")
                    .username(username)
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