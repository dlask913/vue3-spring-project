package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.ImageMapper;
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
class ImageServiceTest {

    @Autowired
    ImageService imageServiceImpl;
    @Autowired
    ImageMapper imageMapper;
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
        ImageRequestDto imageRequestDto = ImageRequestDto.builder()
                .imageType(ImageType.MEMBER)
                .typeId(memberId)
                .build();

        // when
        imageServiceImpl.saveImage(imageRequestDto, memberImg, memberImgLocation);

        // then
        ImageResponseDto findImage = imageMapper.findByType(memberId, ImageType.MEMBER);
        Assertions.assertThat(findImage.imageType()).isEqualTo(imageRequestDto.getImageType().toString());
        Assertions.assertThat(findImage.typeId()).isEqualTo(imageRequestDto.getTypeId());
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
        ImageRequestDto imageRequestDto = ImageRequestDto.builder()
                .imageType(ImageType.MEMBER)
                .typeId(memberId)
                .build();

        imageServiceImpl.saveImage(imageRequestDto, memberImg, memberImgLocation); // 비즈니스 로직대로 저장
        ImageResponseDto findImage = imageMapper.findByType(memberId, ImageType.MEMBER);

        // when
        imageServiceImpl.deleteImage(findImage.id(), memberImgLocation, findImage.imgName());

        // then
        Assertions.assertThat(imageMapper.findByType(memberId, ImageType.MEMBER)).isNull();
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
        ImageRequestDto imageRequestDto = ImageRequestDto.builder()
                .imageType(ImageType.MEMBER)
                .typeId(memberId)
                .build();

        imageServiceImpl.saveImage(imageRequestDto, memberImg, memberImgLocation); // 비즈니스 로직대로 저장

        // when
        ImageResponseDto findImage = imageServiceImpl
                .findByTypeId(memberId, ImageType.MEMBER)
                .orElseThrow();

        // then
        Assertions.assertThat(findImage.imageType()).isEqualTo(imageRequestDto.getImageType().toString());
        Assertions.assertThat(findImage.typeId()).isEqualTo(imageRequestDto.getTypeId());

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