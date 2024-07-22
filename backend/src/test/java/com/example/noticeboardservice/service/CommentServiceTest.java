package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.CommentMapper;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.NoticeMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    private CommentService commentServiceImpl;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private NoticeMapper noticeMapper;

    @AfterEach
    void tearDown() {
        commentMapper.deleteAll();
    }

    @Test
    @DisplayName("댓글을 생성한다")
    void savedCommentTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        Long noticeId = getNoticeId(NoticeRequestDto.builder()
                                            .title("게시글")
                                            .content("게시글 내용")
                                            .memberId(memberId)
                                            .build());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", noticeId, memberId);

        // when
        commentServiceImpl.insertComment(commentDto);

        // then
        CommentResponseDto savedComment = commentMapper.findAllComments().get(0);
        assertThat(savedComment.getContent()).isEqualTo(commentDto.getContent());
        assertThat(savedComment.getMemberId()).isEqualTo(commentDto.getMemberId());
        assertThat(savedComment.getNoticeId()).isEqualTo(commentDto.getNoticeId());
    }

    @Test
    @DisplayName("댓글을 수정한다")
    void updateCommentTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        Long noticeId = getNoticeId(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(memberId)
                .build());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", noticeId, memberId);
        commentServiceImpl.insertComment(commentDto);
        CommentResponseDto savedComment = commentMapper.findAllComments().get(0);
        CommentRequestDto updateRequestDto =
                createCommentDto(savedComment.getId(),"내용22", noticeId, memberId);

        // when
        commentServiceImpl.updateComment(updateRequestDto);

        // then
        CommentResponseDto updateResponseDto = commentMapper.findAllComments().get(0);
        assertThat(updateResponseDto.getContent()).isEqualTo(updateRequestDto.getContent());
        assertThat(updateResponseDto.getMemberId()).isEqualTo(updateRequestDto.getMemberId());
        assertThat(updateResponseDto.getNoticeId()).isEqualTo(updateRequestDto.getNoticeId());
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteCommentTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        Long noticeId = getNoticeId(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(memberId)
                .build());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", noticeId, memberId);
        commentServiceImpl.insertComment(commentDto);
        CommentResponseDto savedComment = commentMapper.findAllComments().get(0);

        // when
        commentServiceImpl.deleteComment(savedComment.getId());

        // then
        assertThat(commentMapper.findAllComments().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 게시물에 저장되어있는 댓글들을 모두 조회한다.")
    void getCommentsByNoticeTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        Long noticeId = getNoticeId(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(memberId)
                .build());
        CommentRequestDto commentDto1 = createCommentDto(0L,"내용1", noticeId, memberId);
        CommentRequestDto commentDto2 = createCommentDto(0L,"내용2", noticeId, memberId);
        CommentRequestDto commentDto3 = createCommentDto(0L,"내용3", noticeId, memberId);
        commentServiceImpl.insertComment(commentDto1);
        commentServiceImpl.insertComment(commentDto2);
        commentServiceImpl.insertComment(commentDto3);

        // when
        List<CommentResponseDto> comments = commentServiceImpl.findCommentsByNoticeId(noticeId);

        // then
        List<String> tmp = new ArrayList<>();
        tmp.add(commentDto1.getContent());
        tmp.add(commentDto2.getContent());
        tmp.add(commentDto3.getContent());
        assertThat(comments.stream().map(CommentResponseDto::getContent).toList())
                .containsExactlyInAnyOrderElementsOf(tmp);
    }

    @Test
    @DisplayName("내가 작성한 댓글들을 모두 조회한다.")
    void getCommentsByUserTest() {
        // given
        String email = "limnj2@test.com";
        Long memberId = getMemberId("limnj@test.com");
        Long memberId2 = getMemberId(email);
        Long noticeId = getNoticeId(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(memberId)
                .build());
        CommentRequestDto commentDto1 = createCommentDto(0L,"내용1", noticeId, memberId);
        CommentRequestDto commentDto2 = createCommentDto(0L,"내용2", noticeId, memberId2);
        CommentRequestDto commentDto3 = createCommentDto(0L,"내용3", noticeId, memberId2);
        CommentRequestDto commentDto4 = createCommentDto(0L,"내용4", noticeId, memberId);
        CommentRequestDto commentDto5 = createCommentDto(0L,"내용5", noticeId, memberId);

        commentServiceImpl.insertComment(commentDto1);
        commentServiceImpl.insertComment(commentDto2);
        commentServiceImpl.insertComment(commentDto3);
        commentServiceImpl.insertComment(commentDto4);
        commentServiceImpl.insertComment(commentDto5);

        // when
        List<CommentResponseDto> comments = commentServiceImpl.findCommentsByUser(email);

        // then
        List<String> tmp = new ArrayList<>();
        tmp.add(commentDto2.getContent());
        tmp.add(commentDto3.getContent());
        assertThat(comments.stream().map(CommentResponseDto::getContent).toList())
                .containsExactlyInAnyOrderElementsOf(tmp);
    }

    CommentRequestDto createCommentDto(Long id, String content, Long noticeId, Long memberId) {
        return CommentRequestDto.builder()
                .id(id)
                .content(content)
                .noticeId(noticeId)
                .memberId(memberId)
                .build();
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

    private Long getNoticeId(NoticeRequestDto noticeRequestDto){
        noticeMapper.insertNotice(noticeRequestDto);
        List<NoticeResponseDto> notices = noticeMapper.findAllNotices(Map.of());
        return notices.get(0).getId();
    }


}