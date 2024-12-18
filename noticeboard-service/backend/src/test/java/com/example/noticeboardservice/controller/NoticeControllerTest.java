package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import com.example.noticeboardservice.service.NoticeService;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoticeController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class NoticeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글을 단일 조회한다")
    void findNoticeTest() throws Exception {
        // given
        NoticeResponseDto responseDto =
                new NoticeResponseDto(1L, "title", "content", "2024-12-18", "2024-12-18", 1L, "limnj", "limnj@test.com", 10L);
        Mockito.when(noticeServiceImpl.findNotice(anyLong(), anyString())).thenReturn(responseDto);

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getName()).thenReturn("limnj@test.com");

        // when // then
        mockMvc.perform(get("/notice/1")
                        .principal(mockAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"));

        Mockito.verify(noticeServiceImpl, Mockito.times(1)).findNotice(anyLong(), anyString());
    }

    @Test
    @DisplayName("게시글을 저장한다")
    void saveNoticeTest() throws Exception {
        // given
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("title")
                .content("content")
                .memberId(1L)
                .build();
        Mockito.when(noticeServiceImpl.saveNotice(any(NoticeRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(post("/notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글 저장이 완료되었습니다."));

        Mockito.verify(noticeServiceImpl, Mockito.times(1)).saveNotice(any(NoticeRequestDto.class));
    }

    @Test
    @DisplayName("게시글을 수정한다")
    void updateNoticeTest() throws Exception {
        // given
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("update title")
                .content("update content")
                .memberId(1L)
                .build();
        Mockito.when(noticeServiceImpl.updateNotice(anyLong(), any(NoticeRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(put("/notice/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("수정이 완료되었습니다."));

        Mockito.verify(noticeServiceImpl, Mockito.times(1)).updateNotice(anyLong(), any(NoticeRequestDto.class));
    }

    @Test
    @DisplayName("게시글을 삭제한다")
    void deleteNoticeTest() throws Exception {
        // given
        Mockito.when(noticeServiceImpl.deleteNotice(anyLong())).thenReturn(1);

        // when // then
        mockMvc.perform(delete("/notice/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글 삭제가 완료되었습니다."));

        Mockito.verify(noticeServiceImpl, Mockito.times(1)).deleteNotice(anyLong());
    }

    @Test
    @DisplayName("모든 게시글을 조회한다")
    void getAllNoticesTest() throws Exception {
        // given
        List<NoticeResponseDto> mockResponse = Collections.singletonList(
                new NoticeResponseDto(1L, "title", "content", "2024-12-18", "2024-12-18", 1L, "limnj", "limnj@test.com", 10L));
        Mockito.when(noticeServiceImpl.findAllNotices(anyMap())).thenReturn(mockResponse);

        // when // then
        mockMvc.perform(get("/notices/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].content").value("content"));

        Mockito.verify(noticeServiceImpl, Mockito.times(1)).findAllNotices(anyMap());
    }

    @Test
    @DisplayName("내가 쓴 게시글을 조회한다")
    void findCommentsByUserTest() throws Exception {
        // given
        List<NoticeResponseDto> mockResponse = Collections.singletonList(
                new NoticeResponseDto(1L, "title", "content", "2024-12-18", "2024-12-18", 1L, "limnj", "limnj@test.com", 10L));
        Mockito.when(noticeServiceImpl.findNoticesByUser(anyString())).thenReturn(mockResponse);

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getName()).thenReturn("limnj@test.com");

        // when // then
        mockMvc.perform(get("/member/notices")
                        .principal(mockAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].content").value("content"));

        Mockito.verify(noticeServiceImpl, Mockito.times(1)).findNoticesByUser(anyString());
    }

    // todo: 페이지네이션 및 키워드 검색 테스트

}