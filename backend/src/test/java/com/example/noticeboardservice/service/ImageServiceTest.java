package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ImageDto;
import com.example.noticeboardservice.dto.ImageResponseDto;
import com.example.noticeboardservice.dto.ImageType;
import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.mapper.ImageMapper;
import com.example.noticeboardservice.mapper.MemberMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ImageServiceTest {

    @Autowired
    ImageService imageServiceImpl;
    @Autowired
    ImageMapper imageMapper;
    @Autowired
    MemberMapper memberMapper;
    @Value("${memberImgLocation}")
    String memberImgLocation;

    @AfterEach
    void tearDown() {
        imageMapper.deleteAll();
    }

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
        ImageDto imageDto = ImageDto.builder()
                .imageType(ImageType.MEMBER)
                .typeId(memberId)
                .build();

        // when
        imageServiceImpl.saveImage(imageDto, memberImg, memberImgLocation);

        // then
        ImageResponseDto findImage = imageMapper.findByType(memberId, ImageType.MEMBER);
        Assertions.assertThat(findImage.getImageType()).isEqualTo(imageDto.getImageType().toString());
        Assertions.assertThat(findImage.getTypeId()).isEqualTo(imageDto.getTypeId());
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
        ImageDto imageDto = ImageDto.builder()
                .imageType(ImageType.MEMBER)
                .typeId(memberId)
                .build();

        imageServiceImpl.saveImage(imageDto, memberImg, memberImgLocation); // 비즈니스 로직대로 저장
        ImageResponseDto findImage = imageMapper.findByType(memberId, ImageType.MEMBER);

        // when
        imageServiceImpl.deleteImage(findImage.getId(), memberImgLocation, findImage.getImgName());

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
        ImageDto imageDto = ImageDto.builder()
                .imageType(ImageType.MEMBER)
                .typeId(memberId)
                .build();

        imageServiceImpl.saveImage(imageDto, memberImg, memberImgLocation); // 비즈니스 로직대로 저장

        // when
        ImageResponseDto findImage = imageServiceImpl
                .findByTypeId(memberId, ImageType.MEMBER)
                .orElseThrow();

        // then
        Assertions.assertThat(findImage.getImageType()).isEqualTo(imageDto.getImageType().toString());
        Assertions.assertThat(findImage.getTypeId()).isEqualTo(imageDto.getTypeId());

    }

    private Long getMemberId(String email){
        MemberDto findMember = memberMapper.findByEmail(email);
        if (findMember == null) {
            MemberDto memberDto = MemberDto.builder()
                    .email(email)
                    .password("1234")
                    .username(email.split("@")[0])
                    .build();
            memberMapper.insertMember(memberDto);
            return memberMapper.findByEmail(email).getId();
        }
        return findMember.getId();
    }
}