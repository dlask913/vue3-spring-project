package com.limnj.noticeboardadmin.notice;

import com.limnj.noticeboardadmin.file.FileInfoMapper;
import com.limnj.noticeboardadmin.file.FileInfoResponseDto;
import com.limnj.noticeboardadmin.file.FileInfoService;
import com.limnj.noticeboardadmin.file.FileType;
import com.limnj.noticeboardadmin.member.AdminMemberRequestDto;
import com.limnj.noticeboardadmin.member.MemberMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NoticeServiceTest {
    @Autowired
    NoticeService noticeService;
    @Autowired
    NoticeMapper noticeMapper;
//    @Autowired
//    FileInfoService fileInfoService;
    @Autowired
    FileInfoMapper fileInfoMapper;

    @Autowired
    MemberMapper memberMapper;

    @Test
    @DisplayName("공지사항 게시글을 첨부파일 없이 저장한다.")
    void saveNotice_withoutFile_success() {
        // given
        Long memberId = saveMemberAndGetMemberId();
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("제목입니다.")
                .content("내용이에요~~")
                .memberId(memberId)
                .build();

        // when
        noticeService.saveNotice(requestDto, null);

        // then
        NoticeResponseDto findNotice = noticeMapper.findNoticeById(requestDto.getId());
        assertThat(findNotice.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(requestDto.getContent());
        assertThat(findNotice.getPostType()).isEqualTo(NoticeRequestDto.PostType.NOTICE);
        assertThat(findNotice.getHideYn()).isEqualTo("N");
    }

    @Test
    @DisplayName("공지사항 게시글에 PDF 파일을 첨부하여 저장한다.")
    void saveNotice_withFile_success() {
        // given
        Long memberId = saveMemberAndGetMemberId();
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("제목입니다.")
                .content("내용이에요~~")
                .memberId(memberId)
                .build();
        MockMultipartFile noticeFile = new MockMultipartFile(
                "첨부파일",
                "test.pdf",
                "application/pdf",
                "<<pdf content>>".getBytes()
        );

        // when
        noticeService.saveNotice(requestDto, noticeFile);

        // then
        NoticeResponseDto findNotice = noticeMapper.findNoticeById(requestDto.getId());
        assertThat(findNotice.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(requestDto.getContent());
        assertThat(findNotice.getPostType()).isEqualTo(NoticeRequestDto.PostType.NOTICE);
        assertThat(findNotice.getHideYn()).isEqualTo("N");

        FileInfoResponseDto findFile = fileInfoMapper.findByType(requestDto.getId(), FileType.NOTICE);
        assertThat(findFile).isNotNull(); // UUID 로 저장되어 비교 불가
    }

    @Test
    @DisplayName("공지사항 게시글을 수정한다.")
    void updateNotice_withoutFile_success() {
        // given
        Long memberId = saveMemberAndGetMemberId();
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("제목입니다.")
                .content("내용이에요~~")
                .memberId(memberId)
                .build();
        noticeService.saveNotice(requestDto, null);

        NoticeRequestDto updateDto = NoticeRequestDto.builder()
                .id(requestDto.getId())
                .title("수정된 제목입니다.")
                .content("수정된 내용이에요~~")
                .memberId(memberId)
                .build();

        // when
        noticeService.updateNotice(updateDto, null);

        // then
        NoticeResponseDto findNotice = noticeMapper.findNoticeById(requestDto.getId());
        assertThat(findNotice.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(updateDto.getContent());
        assertThat(findNotice.getPostType()).isEqualTo(NoticeRequestDto.PostType.NOTICE);
        assertThat(findNotice.getHideYn()).isEqualTo("N");
    }

    @Test
    @DisplayName("기존 공지사항 게시글에 첨부파일을 추가하여 저장한다.")
    void updateNotice_addFile_success() {
        // given
        Long memberId = saveMemberAndGetMemberId();
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("제목입니다.")
                .content("내용이에요~~")
                .memberId(memberId)
                .build();
        noticeService.saveNotice(requestDto, null);

        MockMultipartFile noticeFile = new MockMultipartFile(
                "첨부파일",
                "test.pdf",
                "application/pdf",
                "<<pdf content>>".getBytes()
        );

        // when
        noticeService.updateNotice(requestDto, noticeFile);

        // then
        FileInfoResponseDto findFile = fileInfoMapper.findByType(requestDto.getId(), FileType.NOTICE);
        assertThat(findFile).isNotNull(); // UUID 로 저장되어 비교 불가
    }

    @Test
    @DisplayName("기존 공지사항 게시글에 첨부파일을 제거하여 저장한다.")
    void updateNotice_deleteFile_success() {
        // given
        Long memberId = saveMemberAndGetMemberId();
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("제목입니다.")
                .content("내용이에요~~")
                .memberId(memberId)
                .build();
        MockMultipartFile noticeFile = new MockMultipartFile(
                "첨부파일",
                "test.pdf",
                "application/pdf",
                "<<pdf content>>".getBytes()
        );
        noticeService.saveNotice(requestDto, noticeFile);

        // when
        noticeService.updateNotice(requestDto, null);

        // then
        FileInfoResponseDto findFile = fileInfoMapper.findByType(requestDto.getId(), FileType.NOTICE);
        assertThat(findFile).isNull();
    }


    @Test
    @DisplayName("공지사항 게시글을 삭제한다.")
    void deleteNotice_success() {
        // given
        Long memberId = saveMemberAndGetMemberId();
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("제목입니다.")
                .content("내용이에요~~")
                .memberId(memberId)
                .build();
        noticeService.saveNotice(requestDto, null);

        // when
        noticeService.deleteNotice(requestDto.getId());

        // then
        NoticeResponseDto findNotice = noticeMapper.findNoticeById(requestDto.getId());
        assertThat(findNotice).isNull();
    }

    @Test
    @DisplayName("공지사항 게시글을 상세 조회한다.")
    void findNoticeByNoticeId() {
        // given
        Long memberId = saveMemberAndGetMemberId();
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("제목입니다.")
                .content("내용이에요~~")
                .memberId(memberId)
                .build();
        noticeService.saveNotice(requestDto, null);

        // when
        NoticeResponseDto findNotice = noticeService.findNoticeByNoticeId(requestDto.getId());

        // then
        assertThat(findNotice.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(findNotice.getContent()).isEqualTo(requestDto.getContent());
        assertThat(findNotice.getPostType()).isEqualTo(NoticeRequestDto.PostType.NOTICE);
        assertThat(findNotice.getHideYn()).isEqualTo("N");

    }

    @Test
    @DisplayName("공지사항 게시글을 모두 조회한다.")
    void findAllNotices() {
        // given
        Long memberId = saveMemberAndGetMemberId();

        NoticeRequestDto requestDto1 = NoticeRequestDto.builder()
                .title("제목1")
                .content("내용1")
                .memberId(memberId)
                .build();
        noticeService.saveNotice(requestDto1, null);

        NoticeRequestDto requestDto2 = NoticeRequestDto.builder()
                .title("제목2")
                .content("내용2")
                .memberId(memberId)
                .build();
        noticeService.saveNotice(requestDto2, null);

        // when
        List<NoticeResponseDto> noticeList = noticeService.findAllNotices();

        // then
        assertThat(noticeList).hasSize(2);
        assertThat(noticeList)
                .extracting("title")
                .containsExactly("제목1", "제목2");

        assertThat(noticeList)
                .extracting("content")
                .containsExactly("내용1", "내용2");
    }

    Long saveMemberAndGetMemberId(){
        AdminMemberRequestDto memberRequestDto = AdminMemberRequestDto.builder()
                .email("limnj@test.com")
                .username("adminUser")
                .password("user1234")
                .build();
        memberMapper.saveAdminMember(memberRequestDto);
        return memberRequestDto.getId();
    }
}