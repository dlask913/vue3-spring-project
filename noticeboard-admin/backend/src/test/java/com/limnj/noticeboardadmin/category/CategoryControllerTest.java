package com.limnj.noticeboardadmin.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    CategoryDto createCategoryDto (String name, String decription){
        return CategoryDto.builder()
                .name(name)
                .description(decription)
                .build();
    }

    @Test
    @DisplayName("관리자가 이미지와 함께 카테고리를 생성한다.")
    void createCategoryTest() throws Exception {
        // given

        CategoryDto category = createCategoryDto("limnj@test.com", "limnjlimnjlimnjlimnj");
//        String token = "Bearer "+jwtTokenUtil.generateToken(userRequest.getEmail());

        // when // then
//        mockMvc.perform(MockMvcRequestBuilders.post("/user")
//                        .content(objectMapper.writeValueAsString(userRequest))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization",token)
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(jsonPath("$.email").value(userRequest.getEmail()))
//                .andExpect(jsonPath("$.nickname").value(userRequest.getNickname()))
//                .andExpect(jsonPath("$.categories[0]").value(userRequest.getCategories().get(0).getValue()))
        ;
    }
}