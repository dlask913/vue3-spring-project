package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.CategoryDto;
import com.example.noticeboardservice.mapper.CategoryMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryServiceImpl;
    @Autowired
    CategoryMapper categoryMapper;

    @AfterEach
    void tearDown() {
        categoryMapper.deleteAll();
    }

    @Test
    @DisplayName("이름, 설명, 이미지를 입력하여 카테고리를 생성한다.")
    void insertCategoryWithImageTest() {
        // given
        CategoryDto requestDto = CategoryDto.builder()
                .name("FURNITURE")
                .description("가구")
                .build();
        MockMultipartFile categoryImg = new MockMultipartFile(
                "카테고리 이미지",
                "categoryImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "categoryImg!".getBytes()
        );

        // when
        categoryServiceImpl.insertCategory(requestDto, categoryImg);

        // then
        CategoryDto findCategory = categoryMapper.findByCategoryId(requestDto.getId());
        assertThat(findCategory.getName()).isEqualTo(requestDto.getName());
        assertThat(findCategory.getDescription()).isEqualTo(requestDto.getDescription());
    }

    @Test
    @DisplayName("카테고리를 삭제한다.")
    void deleteCategory() {
        // given
        CategoryDto requestDto = CategoryDto.builder()
                .name("FURNITURE")
                .description("가구")
                .build();
        MockMultipartFile categoryImg = new MockMultipartFile(
                "카테고리 이미지",
                "categoryImg.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "categoryImg!".getBytes()
        );
        categoryServiceImpl.insertCategory(requestDto, categoryImg);

        // when
        categoryServiceImpl.deleteCategory(requestDto.getId());

        // then
        assertThat(categoryMapper.findByCategoryId(requestDto.getId())).isNull();
    }
}