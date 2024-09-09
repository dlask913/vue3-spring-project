package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.CommentMapper;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.NoticeMapper;
import com.example.noticeboardservice.mapper.ReplyMapper;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReplyServiceTest {

    @Autowired
    ReplyService replyServiceImpl;
    @Autowired
    ReplyMapper replyMapper;

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    NoticeMapper noticeMapper;
    @Autowired
    MemberMapper memberMapper;

    @AfterEach
    void tearDown() {
        replyMapper.deleteAll();
    }

    @Test
    @DisplayName("댓글에 대한 하위 댓글을 작성한다.")
    void insertReply() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        ReplyRequestDto requestDto = ReplyRequestDto.builder()
                .content("댓글의 하위 댓글입니다.")
                .memberId(memberId)
                .commentId(getCommentId("상위 댓글 입니다.", memberId))
                .build();

        // when
        replyServiceImpl.insertReply(requestDto);

        // then
        ReplyResponseDto responseDto = replyMapper.findAllReplies().get(0);
        Assertions.assertThat(requestDto.getContent()).isEqualTo(responseDto.getContent());
        Assertions.assertThat(requestDto.getMemberId()).isEqualTo(responseDto.getMemberId());
        Assertions.assertThat(requestDto.getCommentId()).isEqualTo(responseDto.getCommentId());

    }

    @Test
    @DisplayName("하나의 상위 댓글에 달린 하위 댓글들을 모두 조회한다.")
    void findRepliesByCommentId() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        Long commentId = getCommentId("상위 댓글 입니다.", memberId);
        for (int i = 1; i <= 3; i++) {
            ReplyRequestDto requestDto = ReplyRequestDto.builder()
                    .content("requestDto" + i)
                    .memberId(memberId)
                    .commentId(commentId)
                    .build();
            replyServiceImpl.insertReply(requestDto);
        }

        // when
        List<ReplyResponseDto> replies = replyServiceImpl.findRepliesByCommentId(commentId);

        // then
        List<String> tmp = new ArrayList<>();
        tmp.add("requestDto1");
        tmp.add("requestDto2");
        tmp.add("requestDto3");
        assertThat(replies.stream().map(ReplyResponseDto::getContent).toList())
                .containsExactlyInAnyOrderElementsOf(tmp);
    }

    @Test
    @DisplayName("댓글에 대한 하위 댓글을 수정한다.")
    void updateReply() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        Long commentId = getCommentId("상위 댓글 입니다.", memberId);
        ReplyRequestDto requestDto = ReplyRequestDto.builder()
                .content("댓글의 하위 댓글입니다.")
                .memberId(memberId)
                .commentId(commentId)
                .build();
        replyServiceImpl.insertReply(requestDto);

        ReplyResponseDto findReply = replyMapper.findAllReplies().get(0);
        ReplyRequestDto updateDto = ReplyRequestDto.builder()
                .id(findReply.getId())
                .content("댓글의 하위 수정 댓글입니다.")
                .memberId(memberId)
                .commentId(commentId)
                .build();

        // when
        replyServiceImpl.updateReply(updateDto);

        // then
        ReplyResponseDto responseDto = replyMapper.findReply(findReply.getId());
        Assertions.assertThat(updateDto.getContent()).isEqualTo(responseDto.getContent());
    }

    @Test
    @DisplayName("댓글에 대한 하위 댓글을 삭제한다.")
    void deleteReply() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        ReplyRequestDto requestDto = ReplyRequestDto.builder()
                .content("댓글의 하위 댓글입니다.")
                .memberId(memberId)
                .commentId(getCommentId("상위 댓글 입니다.", memberId))
                .build();
        replyServiceImpl.insertReply(requestDto);
        ReplyResponseDto findReply = replyMapper.findAllReplies().get(0);

        // when
        replyServiceImpl.deleteReply(findReply.getId());

        // then
        assertThat(replyMapper.findAllReplies().size()).isEqualTo(0);

    }

    private Long getMemberId(String email){
        MemberResponseDto findMember = memberMapper.findByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findByEmail(email).getId();
        }
        return findMember.getId();
    }

    Long getCommentId(String content, Long memberId) {
        NoticeRequestDto noticeRequestDto = NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(memberId)
                .build();
        noticeMapper.insertNotice(noticeRequestDto);
        List<NoticeResponseDto> notices = noticeMapper.findAllNotices(Map.of());

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .content(content)
                .noticeId(notices.get(0).getId())
                .memberId(memberId)
                .build();
        commentMapper.insertComment(commentRequestDto);
        List<CommentResponseDto> comments = commentMapper.findAllComments();
        return comments.get(0).getId();
    }
}