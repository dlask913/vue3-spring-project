package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.FileInfoMapper;
import com.example.noticeboardservice.mapper.MemberMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class FileInfoServiceTest {

    @Autowired
    FileInfoService fileInfoServiceImpl;
    @Autowired
    FileInfoMapper fileInfoMapper;
    @Autowired
    MemberMapper memberMapper;
    @Value("${member-img-location}")
    String memberImgLocation;

    @Test
    @DisplayName("회원 타입의 이미지를 저장한다.")
    void saveImageTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        MockMultipartFile memberImg = new MockMultipartFile(
                "회원 이미지",
                "memberImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "memberImg!".getBytes()
        );
        FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                .fileType(FileType.MEMBER)
                .typeId(memberId)
                .build();

        // when
        fileInfoServiceImpl.saveFile(fileInfoRequestDto, memberImg, memberImgLocation);

        // then
        FileInfoResponseDto findFile = fileInfoMapper.findByType(memberId, FileType.MEMBER);
        Assertions.assertThat(findFile.getFileType()).isEqualTo(fileInfoRequestDto.getFileType().toString());
        Assertions.assertThat(findFile.getTypeId()).isEqualTo(fileInfoRequestDto.getTypeId());
    }

    @Test
    @DisplayName("특정 이미지를 삭제한다.")
    void deleteImageTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        MockMultipartFile memberImg = new MockMultipartFile(
                "회원 이미지",
                "memberImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "memberImg!".getBytes()
        );
        FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                .fileType(FileType.MEMBER)
                .typeId(memberId)
                .build();

        fileInfoServiceImpl.saveFile(fileInfoRequestDto, memberImg, memberImgLocation); // 비즈니스 로직대로 저장
        FileInfoResponseDto findFile = fileInfoMapper.findByType(memberId, FileType.MEMBER);

        // when
        fileInfoServiceImpl.deleteFile(findFile.getId(), memberImgLocation, findFile.getFileName());

        // then
        Assertions.assertThat(fileInfoMapper.findByType(memberId, FileType.MEMBER)).isNull();
    }

    @Test
    @DisplayName("회원 PK 로 이미지를 조회한다.")
    void findByTypeIdTest() {
        // given
        Long memberId = getMemberId("limnj@test.com");
        MockMultipartFile memberImg = new MockMultipartFile(
                "회원 이미지",
                "memberImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "memberImg!".getBytes()
        );
        FileInfoRequestDto fileInfoRequestDto = FileInfoRequestDto.builder()
                .fileType(FileType.MEMBER)
                .typeId(memberId)
                .build();

        fileInfoServiceImpl.saveFile(fileInfoRequestDto, memberImg, memberImgLocation); // 비즈니스 로직대로 저장

        // when
        FileInfoResponseDto findImage = fileInfoServiceImpl
                .findByTypeId(memberId, FileType.MEMBER)
                .orElseThrow();

        // then
        Assertions.assertThat(findImage.getFileType()).isEqualTo(fileInfoRequestDto.getFileType().toString());
        Assertions.assertThat(findImage.getTypeId()).isEqualTo(fileInfoRequestDto.getTypeId());

    }

    private Long getMemberId(String email){
        MemberResponseDto findMember = memberMapper.findMemberByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username(email.split("@")[0])
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findMemberByEmail(email).id();
        }
        return findMember.id();
    }
}