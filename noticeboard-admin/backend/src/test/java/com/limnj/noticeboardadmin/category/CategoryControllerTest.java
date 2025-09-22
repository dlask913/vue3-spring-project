package com.limnj.noticeboardadmin.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("관리자가 이미지와 함께 카테고리를 생성한다.")
    void createCategory_withImage_200OkAndSuccessMessage() throws Exception {
        // given
        CategoryDto category = createCategoryDto("FURNITURE", "가구");
        MockMultipartFile categoryDto = new MockMultipartFile(
                "categoryDto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(category)
        );
        MockMultipartFile categoryImg = new MockMultipartFile(
                "categoryImg", "image.jpg", "image/jpeg", "image data".getBytes());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(categoryDto)
                        .file(categoryImg)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().string("카테고리 생성이 완료되었습니다."));
    }

    @Test
    @DisplayName("카테고리 생성 시 이미지를 넣지 않으면 실패한다.")
    void createCategory_withoutImage_400BadRequest() throws Exception {
        // given
        CategoryDto category = createCategoryDto("FURNITURE", "가구");
        MockMultipartFile categoryDto = new MockMultipartFile(
                "categoryDto",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(category)
        );

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/category")
                        .file(categoryDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MissingServletRequestPartException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("categoryImg",
                        ((MissingServletRequestPartException) Objects.requireNonNull(result.getResolvedException())).getRequestPartName()));
    }

    CategoryDto createCategoryDto (String name, String decription){
        return CategoryDto.builder()
                .name(name)
                .description(decription)
                .build();
    }
}