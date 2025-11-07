package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.controller.product.ProductController;
import com.example.noticeboardservice.dto.product.ProductDetailsResponseDto;
import com.example.noticeboardservice.dto.product.ProductRequestDto;
import com.example.noticeboardservice.dto.product.ProductResponseDto;
import com.example.noticeboardservice.mapper.common.AccessLogMapper;
import com.example.noticeboardservice.service.product.CategoryService;
import com.example.noticeboardservice.service.product.ProductService;
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

import java.util.List;

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
    @MockBean
    private AccessLogMapper accessLogMapper;

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
                new ProductDetailsResponseDto(1L, "상품 제목", "상품 내용","FURNITURE",10000, 9500,"productImg",1L,2L,"2024-12-21", "2025-03-25");
        Mockito.when(productServiceImpl.findProduct(anyLong())).thenReturn(responseDto);

        // when // then
        mockMvc.perform(get("/product/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.id()))
                .andExpect(jsonPath("$.title").value(responseDto.title()))
                .andExpect(jsonPath("$.content").value(responseDto.content()))
                .andExpect(jsonPath("$.category").value(responseDto.category()))
                .andExpect(jsonPath("$.standardPrice").value(responseDto.standardPrice()))
                .andExpect(jsonPath("$.latestPrice").value(responseDto.latestPrice()))
                .andExpect(jsonPath("$.imgUrl").value(responseDto.imgUrl()))
                .andExpect(jsonPath("$.ownerId").value(responseDto.ownerId()))
                .andExpect(jsonPath("$.customerId").value(responseDto.customerId()))
                .andExpect(jsonPath("$.postDate").value(responseDto.postDate()))
                .andExpect(jsonPath("$.deadline").value(responseDto.deadline()));

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

    @Test
    @DisplayName("회원이 등록한 상품 게시물들을 모두 조회한다")
    void findProductsByMemberIdTest() throws Exception {
        // given
        Long memberId = 1L;
        List<ProductResponseDto> products = List.of(
                new ProductResponseDto(1L, "제목1", "내용1", "FURNITURE", 10000, "productImg1", memberId, "2025-03-19"),
                new ProductResponseDto(2L, "제목2", "내용2", "CLOTHES", 8000, "productImg2", memberId, "2025-03-19")
        );
        Mockito.when(productServiceImpl.findProductsByMemberId(memberId)).thenReturn(products);

        // when // then
        mockMvc.perform(get("/products/member/{memberId}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(products.get(0).id()))
                .andExpect(jsonPath("$[0].title").value(products.get(0).title()))
                .andExpect(jsonPath("$[0].content").value(products.get(0).content()))
                .andExpect(jsonPath("$[0].category").value(products.get(0).category()))
                .andExpect(jsonPath("$[0].standardPrice").value(products.get(0).standardPrice()))
                .andExpect(jsonPath("$[0].ownerId").value(products.get(0).ownerId()))
                .andExpect(jsonPath("$[1].id").value(products.get(1).id()))
                .andExpect(jsonPath("$[1].title").value(products.get(1).title()))
                .andExpect(jsonPath("$[1].content").value(products.get(1).content()))
                .andExpect(jsonPath("$[1].category").value(products.get(1).category()))
                .andExpect(jsonPath("$[1].standardPrice").value(products.get(1).standardPrice()))
                .andExpect(jsonPath("$[1].ownerId").value(products.get(1).ownerId()));

        Mockito.verify(productServiceImpl, Mockito.times(1)).findProductsByMemberId(memberId);
    }
}