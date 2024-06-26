package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.CommentMapper;
import com.example.noticeboardservice.mapper.HeartMapper;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.NoticeMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class HeartServiceTest {

    @Autowired
    HeartService heartServiceImpl;
    @Autowired
    HeartMapper heartMapper;
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    NoticeMapper noticeMapper;
    @Autowired
    CommentMapper commentMapper;

    @AfterEach
    void tearDown() {
        heartMapper.deleteAll();
        memberMapper.deleteAll();
    }

    @Test
    @DisplayName("댓글의 좋아요 상태를 확인한다.")
    void findHeartTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        HeartDto heartDto = HeartDto.builder()
                .commentId(getCommentId("댓글 내용", memberId))
                .memberId(memberId)
                .build();
        heartMapper.saveHeart(heartDto);

        // when
        HeartDto findHeartDto = heartServiceImpl.findHeart(heartDto.getMemberId(), heartDto.getCommentId());

        // then
        Assertions.assertThat(findHeartDto.getCommentId()).isEqualTo(heartDto.getCommentId());
        Assertions.assertThat(findHeartDto.getMemberId()).isEqualTo(heartDto.getMemberId());
        Assertions.assertThat(findHeartDto.getCnt()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아하는 댓글에 좋아요를 누른다.")
    void saveHeartTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        HeartDto heartDto = HeartDto.builder()
                .commentId(getCommentId("댓글 내용", memberId))
                .memberId(memberId)
                .build();

        // when
        heartServiceImpl.saveHeart(heartDto);

        // then
        HeartDto findHeartDto = heartMapper.findHeart(heartDto);
        Assertions.assertThat(findHeartDto.getCommentId()).isEqualTo(heartDto.getCommentId());
        Assertions.assertThat(findHeartDto.getMemberId()).isEqualTo(heartDto.getMemberId());
    }

    @Test
    @DisplayName("좋아요를 눌렀던 댓글에 좋아요를 취소한다.")
    void deleteHeartTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        HeartDto heartDto = HeartDto.builder()
                .commentId(getCommentId("댓글 내용", memberId))
                .memberId(memberId)
                .build();
        heartMapper.saveHeart(heartDto);
        HeartDto findHeartDto = heartMapper.findHeart(heartDto);

        // when
        heartServiceImpl.deleteHeart(findHeartDto.getId());

        // then
        List<HeartDto> heartDtoList = heartMapper.findAllHearts();
        Assertions.assertThat(heartDtoList).hasSize(0);
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

    Long getCommentId(String content, Long memberId) {
        NoticeRequestDto noticeRequestDto = NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(memberId)
                .build();
        noticeMapper.insertNotice(noticeRequestDto);
        List<NoticeResponseDto> notices = noticeMapper.findAllNotices();

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