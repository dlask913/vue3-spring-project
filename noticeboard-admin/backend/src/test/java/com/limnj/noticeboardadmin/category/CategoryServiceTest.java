package com.limnj.noticeboardadmin.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
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
    void deleteCategoryTest() {
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

    @Test
    @DisplayName("등록된 카테고리를 모두 조회한다.")
    void findAllCategoriesTest(){
        // given
        for (int i = 0; i < 5; i++) {
            CategoryDto requestDto = CategoryDto.builder()
                    .name("CATEGORY"+i)
                    .description("카테고리 설졍"+i)
                    .build();
            MockMultipartFile categoryImg = new MockMultipartFile(
                    "카테고리 이미지",
                    "categoryImg.jpg",
                    String.valueOf(MediaType.IMAGE_JPEG),
                    "categoryImg!".getBytes()
            );
            categoryServiceImpl.insertCategory(requestDto, categoryImg);
        }

        // when
        List<CategoryDto> categories = categoryServiceImpl.findAllCategories();

        // then
        assertThat(categories).isNotNull();
        assertThat(categories).hasSize(5);
    }
}