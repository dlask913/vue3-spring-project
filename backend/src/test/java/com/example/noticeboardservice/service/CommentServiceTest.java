package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.CommentMapper;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.NoticeMapper;
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
        noticeMapper.deleteAll();
        memberMapper.deleteAll();
    }

    @Test
    @DisplayName("댓글을 생성한다")
    void savedCommentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        NoticeResponseDto notice = getNotice(NoticeRequestDto.builder()
                                            .title("게시글")
                                            .content("게시글 내용")
                                            .memberId(member.id())
                                            .build());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", notice.id(), member.id());

        // when
        commentServiceImpl.insertComment(commentDto);

        // then
        CommentResponseDto savedComment = commentMapper.findAllComments().get(0);
        assertThat(savedComment.content()).isEqualTo(commentDto.getContent());
        assertThat(savedComment.memberId()).isEqualTo(commentDto.getMemberId());
        assertThat(savedComment.noticeId()).isEqualTo(commentDto.getNoticeId());
    }

    @Test
    @DisplayName("댓글을 수정한다")
    void updateCommentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        NoticeResponseDto notice = getNotice(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(member.id())
                .build());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", notice.id(), member.id());
        commentServiceImpl.insertComment(commentDto);
        CommentResponseDto savedComment = commentMapper.findAllComments().get(0);
        CommentRequestDto updateRequestDto =
                createCommentDto(savedComment.id(),"내용22", notice.id(), member.id());

        // when
        commentServiceImpl.updateComment(updateRequestDto);

        // then
        CommentResponseDto updateResponseDto = commentMapper.findAllComments().get(0);
        assertThat(updateResponseDto.content()).isEqualTo(updateRequestDto.getContent());
        assertThat(updateResponseDto.memberId()).isEqualTo(updateRequestDto.getMemberId());
        assertThat(updateResponseDto.noticeId()).isEqualTo(updateRequestDto.getNoticeId());
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteCommentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        NoticeResponseDto notice = getNotice(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(member.id())
                .build());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", notice.id(), member.id());
        commentServiceImpl.insertComment(commentDto);
        CommentResponseDto savedComment = commentMapper.findAllComments().get(0);

        // when
        commentServiceImpl.deleteComment(savedComment.id());

        // then
        assertThat(commentMapper.findAllComments().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 게시물에 저장되어있는 댓글들을 모두 조회한다.")
    void getCommentsByNoticeTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        NoticeResponseDto notice = getNotice(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(member.id())
                .build());
        CommentRequestDto commentDto1 = createCommentDto(0L,"내용1", notice.id(), member.id());
        CommentRequestDto commentDto2 = createCommentDto(0L,"내용2", notice.id(), member.id());
        CommentRequestDto commentDto3 = createCommentDto(0L,"내용3", notice.id(), member.id());
        commentServiceImpl.insertComment(commentDto1);
        commentServiceImpl.insertComment(commentDto2);
        commentServiceImpl.insertComment(commentDto3);

        // when
        List<CommentResponseDto> comments = commentServiceImpl.findCommentsByNoticeId(notice.id());

        // then
        List<String> tmp = new ArrayList<>();
        tmp.add(commentDto1.getContent());
        tmp.add(commentDto2.getContent());
        tmp.add(commentDto3.getContent());
        assertThat(comments.stream().map(CommentResponseDto::content).toList())
                .containsExactlyInAnyOrderElementsOf(tmp);
    }

    @Test
    @DisplayName("특정 게시물에 저장되어있는 댓글 조회 시 작성자의 프로필 이미지도 출력한다.")
    void getCommentsByNoticeWithMemberImgTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        NoticeResponseDto notice = getNotice(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(member.id())
                .build());
        CommentRequestDto commentDto = createCommentDto(0L,"내용1", notice.id(), member.id());
        commentServiceImpl.insertComment(commentDto);

        // when
        List<CommentResponseDto> comments = commentServiceImpl.findCommentsByNoticeId(notice.id());

        // then
        CommentResponseDto findComment = comments.get(0);
        assertThat(findComment.content()).isEqualTo(commentDto.getContent());
        assertThat(findComment.memberId()).isEqualTo(commentDto.getMemberId());
        assertThat(findComment.noticeId()).isEqualTo(commentDto.getNoticeId());
        assertThat(findComment.username()).isEqualTo(member.username());
        assertThat(findComment.memberImgUrl()).isEqualTo(member.imgUrl());
    }

    @Test
    @DisplayName("내가 작성한 댓글들을 모두 조회한다.")
    void getCommentsByUserTest() {
        // given
        String email = "limnj2@test.com";
        MemberResponseDto member1 = getMember("limnj@test.com");
        MemberResponseDto member2 = getMember(email);
        NoticeResponseDto notice = getNotice(NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(member1.id())
                .build());
        CommentRequestDto commentDto1 = createCommentDto(0L,"내용1", notice.id(), member1.id());
        CommentRequestDto commentDto2 = createCommentDto(0L,"내용2", notice.id(), member2.id());
        CommentRequestDto commentDto3 = createCommentDto(0L,"내용3", notice.id(), member2.id());
        CommentRequestDto commentDto4 = createCommentDto(0L,"내용4", notice.id(), member1.id());
        CommentRequestDto commentDto5 = createCommentDto(0L,"내용5", notice.id(), member1.id());

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
        assertThat(comments.stream().map(CommentResponseDto::content).toList())
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

    private MemberResponseDto getMember(String email){
        MemberResponseDto findMember = memberMapper.findByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findByEmail(email);
        }
        return findMember;
    }

    private NoticeResponseDto getNotice(NoticeRequestDto noticeRequestDto){
        noticeMapper.insertNotice(noticeRequestDto);
        List<NoticeResponseDto> notices = noticeMapper.findAllNotices(Map.of());
        return notices.get(0);
    }


}