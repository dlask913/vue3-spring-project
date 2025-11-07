package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.comment.CommentRequestDto;
import com.example.noticeboardservice.dto.heart.HeartDto;
import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import com.example.noticeboardservice.dto.notice.NoticeRequestDto;
import com.example.noticeboardservice.mapper.comment.CommentMapper;
import com.example.noticeboardservice.mapper.heart.HeartMapper;
import com.example.noticeboardservice.mapper.member.MemberMapper;
import com.example.noticeboardservice.mapper.notice.NoticeMapper;
import com.example.noticeboardservice.service.heart.HeartService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
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
    @DisplayName("로그인 하지 않은 사용자가 댓글(좋아요)를 조회한다.")
    void findHeartWithoutLogin() {
        // given
        Long originMemberId = getMemberId("limnj@test.com");
        Long commentId = getCommentId("댓글 내용", originMemberId);
        for (int i = 0; i < 3; i++) {
            Long memberId = getMemberId("limnj"+i+"@test.com");
            HeartDto heartDto = HeartDto.builder()
                    .commentId(commentId)
                    .memberId(memberId)
                    .build();
            heartMapper.saveHeart(heartDto);
        }

        // when
        HeartDto findHeartDto = heartServiceImpl.findHeart(null, commentId);

        // then
        Assertions.assertThat(findHeartDto.getCommentId()).isEqualTo(commentId);
        Assertions.assertThat(findHeartDto.getCnt()).isEqualTo(3);
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