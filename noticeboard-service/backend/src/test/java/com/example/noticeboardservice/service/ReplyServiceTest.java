package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.comment.CommentRequestDto;
import com.example.noticeboardservice.dto.comment.ReplyRequestDto;
import com.example.noticeboardservice.dto.comment.ReplyResponseDto;
import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import com.example.noticeboardservice.dto.notice.NoticeRequestDto;
import com.example.noticeboardservice.mapper.comment.CommentMapper;
import com.example.noticeboardservice.mapper.member.MemberMapper;
import com.example.noticeboardservice.mapper.notice.NoticeMapper;
import com.example.noticeboardservice.mapper.comment.ReplyMapper;
import com.example.noticeboardservice.service.comment.ReplyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
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
        ReplyResponseDto responseDto = replyMapper.findReply(requestDto.getId());
        Assertions.assertThat(requestDto.getContent()).isEqualTo(responseDto.content());
        Assertions.assertThat(requestDto.getMemberId()).isEqualTo(responseDto.memberId());
        Assertions.assertThat(requestDto.getCommentId()).isEqualTo(responseDto.commentId());

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
        assertThat(replies.stream().map(ReplyResponseDto::content).toList())
                .containsExactlyInAnyOrderElementsOf(tmp);
        assertThat(replies.get(0).memberImgUrl()).isEqualTo("/image/memberDefaultImg.jpg");
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

        ReplyResponseDto findReply = replyMapper.findReply(requestDto.getId());
        ReplyRequestDto updateDto = ReplyRequestDto.builder()
                .id(findReply.id())
                .content("댓글의 하위 수정 댓글입니다.")
                .memberId(memberId)
                .commentId(commentId)
                .build();

        // when
        replyServiceImpl.updateReply(updateDto);

        // then
        ReplyResponseDto responseDto = replyMapper.findReply(findReply.id());
        Assertions.assertThat(updateDto.getContent()).isEqualTo(responseDto.content());
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
        ReplyResponseDto findReply = replyMapper.findReply(requestDto.getId());

        // when
        replyServiceImpl.deleteReply(findReply.id());

        // then
        assertThat(replyMapper.findAllReplies().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글에 달린 대댓글 수를 계산한다.")
    void calculateReplyCount() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        Long commentId = getCommentId("상위 댓글 입니다.", memberId);
        final long REPLY_NUMBER = 5;

        for (int i = 0; i < REPLY_NUMBER; i++) {
            ReplyRequestDto requestDto = ReplyRequestDto.builder()
                    .content("댓글의 하위 댓글입니다.")
                    .memberId(memberId)
                    .commentId(commentId)
                    .build();
            replyServiceImpl.insertReply(requestDto);
        }

        // when
        Long replyCount = replyServiceImpl.calculateRepliesByCommentId(commentId);

        // then
        assertThat(replyCount).isEqualTo(REPLY_NUMBER);
    }

    private Long getMemberId(String email){
        MemberResponseDto findMember = memberMapper.findMemberByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberRequestDto.getId();
        }
        return findMember.id();
    }

    Long getCommentId(String content, Long memberId) {
        NoticeRequestDto noticeRequestDto = NoticeRequestDto.builder()
                .title("게시글")
                .content(content)
                .memberId(memberId)
                .build();
        noticeMapper.insertNotice(noticeRequestDto);

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .content(content)
                .noticeId(noticeRequestDto.getId())
                .memberId(memberId)
                .build();
        commentMapper.insertComment(commentRequestDto);
        return commentRequestDto.getId();
    }
}