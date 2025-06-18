package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.CommentMapper;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.NoticeMapper;
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
class CommentServiceTest {

    @Autowired
    private CommentService commentServiceImpl;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private NoticeMapper noticeMapper;

    @Test
    @DisplayName("댓글을 생성한다")
    void savedCommentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        Long noticeId = getNoticeId(member.id());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", noticeId, member.id());

        // when
        commentServiceImpl.insertComment(commentDto);

        // then
        CommentResponseDto savedComment = commentMapper.findCommentById(commentDto.getId());
        assertThat(savedComment.content()).isEqualTo(commentDto.getContent());
        assertThat(savedComment.memberId()).isEqualTo(commentDto.getMemberId());
        assertThat(savedComment.noticeId()).isEqualTo(commentDto.getNoticeId());
    }

    @Test
    @DisplayName("댓글을 수정한다")
    void updateCommentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        Long noticeId = getNoticeId(member.id());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", noticeId, member.id());
        commentServiceImpl.insertComment(commentDto);
        CommentResponseDto savedComment = commentMapper.findCommentById(commentDto.getId());
        CommentRequestDto updateRequestDto =
                createCommentDto(savedComment.id(),"내용22", noticeId, member.id());

        // when
        commentServiceImpl.updateComment(updateRequestDto);

        // then
        CommentResponseDto updateResponseDto = commentMapper.findCommentById(commentDto.getId());
        assertThat(updateResponseDto.content()).isEqualTo(updateRequestDto.getContent());
        assertThat(updateResponseDto.memberId()).isEqualTo(updateRequestDto.getMemberId());
        assertThat(updateResponseDto.noticeId()).isEqualTo(updateRequestDto.getNoticeId());
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteCommentTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        Long noticeId = getNoticeId(member.id());
        CommentRequestDto commentDto = createCommentDto(0L,"내용", noticeId, member.id());
        commentServiceImpl.insertComment(commentDto);
        CommentResponseDto savedComment = commentMapper.findCommentById(commentDto.getId());

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
        Long noticeId = getNoticeId(member.id());
        CommentRequestDto commentDto1 = createCommentDto(0L,"내용1", noticeId, member.id());
        CommentRequestDto commentDto2 = createCommentDto(0L,"내용2", noticeId, member.id());
        CommentRequestDto commentDto3 = createCommentDto(0L,"내용3", noticeId, member.id());
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
        assertThat(comments.stream().map(CommentResponseDto::content).toList())
                .containsExactlyInAnyOrderElementsOf(tmp);
    }

    @Test
    @DisplayName("특정 게시물에 저장되어있는 댓글 조회 시 작성자의 프로필 이미지도 출력한다.")
    void getCommentsByNoticeWithMemberImgTest() {
        // given
        MemberResponseDto member = getMember("limnj@test.com");
        Long noticeId = getNoticeId(member.id());
        CommentRequestDto commentDto = createCommentDto(0L,"내용1", noticeId, member.id());
        commentServiceImpl.insertComment(commentDto);

        // when
        List<CommentResponseDto> comments = commentServiceImpl.findCommentsByNoticeId(noticeId);

        // then
        CommentResponseDto findComment = comments.get(0);
        assertThat(findComment.content()).isEqualTo(commentDto.getContent());
        assertThat(findComment.memberId()).isEqualTo(commentDto.getMemberId());
        assertThat(findComment.noticeId()).isEqualTo(commentDto.getNoticeId());
        assertThat(findComment.username()).isEqualTo(member.username());
        assertThat(findComment.memberImgUrl()).isEqualTo("/image/memberDefaultImg.jpg"); // 초기 디폴트 이미지
    }

    @Test
    @DisplayName("내가 작성한 댓글들을 모두 조회한다.")
    void getCommentsByUserTest() {
        // given
        String email = "limnj2@test.com";
        MemberResponseDto member1 = getMember("limnj@test.com");
        MemberResponseDto member2 = getMember(email);
        Long noticeId = getNoticeId(member1.id());
        CommentRequestDto commentDto1 = createCommentDto(0L,"내용1", noticeId, member1.id());
        CommentRequestDto commentDto2 = createCommentDto(0L,"내용2", noticeId, member2.id());
        CommentRequestDto commentDto3 = createCommentDto(0L,"내용3", noticeId, member2.id());
        CommentRequestDto commentDto4 = createCommentDto(0L,"내용4", noticeId, member1.id());
        CommentRequestDto commentDto5 = createCommentDto(0L,"내용5", noticeId, member1.id());

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
        MemberResponseDto findMember = memberMapper.findMemberByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findMemberByEmail(email);
        }
        return findMember;
    }

    private Long getNoticeId(Long memberId){
        NoticeRequestDto noticeRequestDto = NoticeRequestDto.builder()
                .title("게시글")
                .content("게시글 내용")
                .memberId(memberId)
                .build();
        noticeMapper.insertNotice(noticeRequestDto);
        return noticeRequestDto.getId();
    }


}