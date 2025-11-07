package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.controller.product.ProductBidController;
import com.example.noticeboardservice.dto.product.ProductBidDto;
import com.example.noticeboardservice.mapper.common.AccessLogMapper;
import com.example.noticeboardservice.service.product.ProductBidService;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductBidController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class ProductBidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductBidService productBidServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;
    @MockBean
    private AccessLogMapper accessLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("입찰 가격을 등록한다.")
    void createProductBidPriceSuccessTest() throws Exception {
        // given
        ProductBidDto requestDto = ProductBidDto.builder()
                .bidPrice(10000)
                .customerId(1L)
                .productId(1L)
                .build();

        Mockito.when(productBidServiceImpl.addBidHistory(any(ProductBidDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("가격 등록에 성공하였습니다."));

        Mockito.verify(productBidServiceImpl, Mockito.times(1)).addBidHistory(any(ProductBidDto.class));
    }

    @Test
    @DisplayName("입찰 가격 등록에 실패한다.")
    void createProductBidPriceFailureTest() throws Exception {
        // given
        ProductBidDto requestDto = ProductBidDto.builder()
                .bidPrice(10000)
                .customerId(1L)
                .productId(1L)
                .build();

        Mockito.when(productBidServiceImpl.addBidHistory(any(ProductBidDto.class))).thenReturn(0);

        // when // then
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("가격 등록에 실패하였습니다."));

        Mockito.verify(productBidServiceImpl, Mockito.times(1)).addBidHistory(any(ProductBidDto.class));
    }
}