package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.HeartDto;
import com.example.noticeboardservice.service.HeartService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeartController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class HeartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HeartService heartServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("하트 상태를 조회한다")
    void getHeartStatusTest() throws Exception {
        // given
        Long memberId = 1L;
        Long commentId = 1L;
        HeartDto responseDto = HeartDto.builder()
                .memberId(memberId)
                .commentId(commentId)
                .build();

        Mockito.when(heartServiceImpl.findHeart(memberId, commentId)).thenReturn(responseDto);

        // when // then
        mockMvc.perform(get("/heart")
                        .param("memberId", String.valueOf(memberId))
                        .param("commentId", String.valueOf(commentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.commentId").value(commentId));

        Mockito.verify(heartServiceImpl, Mockito.times(1)).findHeart(memberId, commentId);
    }

    @Test
    @DisplayName("하트를 저장한다")
    void saveHeartSuccessTest() throws Exception {
        // given
        HeartDto requestDto = HeartDto.builder()
                .memberId(1L)
                .commentId(1L)
                .build();

        Mockito.when(heartServiceImpl.saveHeart(any(HeartDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(post("/heart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        Mockito.verify(heartServiceImpl, Mockito.times(1)).saveHeart(any(HeartDto.class));
    }

    @Test
    @DisplayName("하트를 저장하지 못한다")
    void saveHeartFailTest() throws Exception {
        // given
        HeartDto requestDto = HeartDto.builder()
                .memberId(1L)
                .commentId(1L)
                .build();

        Mockito.when(heartServiceImpl.saveHeart(any(HeartDto.class))).thenReturn(0);

        // when // then
        mockMvc.perform(post("/heart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("하트 저장에 실패하였습니다."));

        Mockito.verify(heartServiceImpl, Mockito.times(1)).saveHeart(any(HeartDto.class));
    }

    @Test
    @DisplayName("하트를 취소한다")
    void deleteHeartSuccessTest() throws Exception {
        // given
        Long heartId = 1L;

        Mockito.when(heartServiceImpl.deleteHeart(heartId)).thenReturn(1);

        // when // then
        mockMvc.perform(delete("/heart/{heartId}", heartId))
                .andExpect(status().isOk());

        Mockito.verify(heartServiceImpl, Mockito.times(1)).deleteHeart(heartId);
    }

    @Test
    @DisplayName("하트를 취소하지 못한다")
    void deleteHeartFailTest() throws Exception {
        // given
        Long heartId = 1L;
        Mockito.when(heartServiceImpl.deleteHeart(heartId)).thenReturn(0);

        // when // then
        mockMvc.perform(delete("/heart/{heartId}", heartId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("하트 취소에 실패하였습니다."));

        Mockito.verify(heartServiceImpl, Mockito.times(1)).deleteHeart(heartId);
    }

}