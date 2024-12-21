package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.ProductDetailsResponseDto;
import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.service.CategoryService;
import com.example.noticeboardservice.service.ProductService;
import com.example.noticeboardservice.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productServiceImpl;
    @MockBean
    private CategoryService categoryServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품을 등록한다")
    void createProductTest() throws Exception {
        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .id(1L)
                .title("상품 제목")
                .content("상품 내용")
                .category("전자기기")
                .standardPrice(10000)
                .ownerId(1L)
                .build();
        MockMultipartFile productDtoFile =
                new MockMultipartFile("productDto","","application/json", objectMapper.writeValueAsBytes(requestDto));

        Mockito.when(productServiceImpl.insertProduct(any(ProductRequestDto.class), any())).thenReturn(1L);

        // when // then
        mockMvc.perform(multipart("/product")
                        .file(productDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("상품 등록에 성공하였습니다."));

        Mockito.verify(productServiceImpl, Mockito.times(1)).insertProduct(any(ProductRequestDto.class), any());
    }

    @Test
    @DisplayName("상품 상세 정보를 조회한다")
    void findProductByIdTest() throws Exception {
        // given
        ProductDetailsResponseDto responseDto =
                new ProductDetailsResponseDto(1L, "상품 제목", "상품 내용","FURNITURE",10000, 9500,"productImg",1L,2L,"2024-12-21");

        Mockito.when(productServiceImpl.findProduct(1L)).thenReturn(responseDto);

        // when // then
        mockMvc.perform(get("/product/{productId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("상품 제목"))
                .andExpect(jsonPath("$.content").value("상품 내용"))
                .andExpect(jsonPath("$.category").value("FURNITURE"))
                .andExpect(jsonPath("$.standardPrice").value(10000))
                .andExpect(jsonPath("$.latestPrice").value(9500))
                .andExpect(jsonPath("$.imgUrl").value("productImg"))
                .andExpect(jsonPath("$.ownerId").value(1L))
                .andExpect(jsonPath("$.customerId").value(2L))
                .andExpect(jsonPath("$.postDate").value("2024-12-21"));

        Mockito.verify(productServiceImpl, Mockito.times(1)).findProduct(1L);
    }

    @Test
    @DisplayName("상품 정보를 수정한다")
    void updateProductTest() throws Exception {
        // given
        ProductRequestDto updateDto = ProductRequestDto.builder()
                .id(1L)
                .title("수정된 제목")
                .content("수정된 내용")
                .category("FURNITURE")
                .standardPrice(12000)
                .ownerId(1L)
                .build();

        Mockito.when(productServiceImpl.updateProduct(any(ProductRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(put("/product/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("상품 수정에 성공하였습니다."));

        Mockito.verify(productServiceImpl, Mockito.times(1)).updateProduct(any(ProductRequestDto.class));
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void deleteProductTest() throws Exception {
        // given
        Mockito.when(productServiceImpl.deleteProduct(1L)).thenReturn(1);

        // when // then
        mockMvc.perform(delete("/productId/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료되었습니다."));

        Mockito.verify(productServiceImpl, Mockito.times(1)).deleteProduct(1L);
    }
}