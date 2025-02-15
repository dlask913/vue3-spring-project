package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.CommentRequestDto;
import com.example.noticeboardservice.dto.CommentResponseDto;
import com.example.noticeboardservice.service.CommentService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글을 저장한다")
    void saveCommentTest() throws Exception {
        // given
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .content("content")
                .memberId(1L)
                .noticeId(1L)
                .build();
        Mockito.when(commentServiceImpl.insertComment(any(CommentRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("댓글을 저장하였습니다."));

        Mockito.verify(commentServiceImpl, Mockito.times(1)).insertComment(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("특정 게시글의 댓글을 조회한다")
    void findCommentsByNoticeIdTest() throws Exception {
        // given
        CommentResponseDto responseDto =
                new CommentResponseDto(1L, "content", "2024-12-19", "limnj", "memberImg", 1L, 1L, 0L);
        Mockito.when(commentServiceImpl.findCommentsByNoticeId(anyLong())).thenReturn(List.of(responseDto));

        // when // then
        mockMvc.perform(get("/comment/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto.id()))
                .andExpect(jsonPath("$[0].content").value(responseDto.content()))
                .andExpect(jsonPath("$[0].username").value(responseDto.username()));

        Mockito.verify(commentServiceImpl, Mockito.times(1)).findCommentsByNoticeId(anyLong());
    }

    @Test
    @DisplayName("댓글을 수정한다")
    void updateCommentTest() throws Exception {
        // given
        CommentRequestDto requestDto = new CommentRequestDto(1L, "Updated content", 1L, 1L);
        Mockito.when(commentServiceImpl.updateComment(any(CommentRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(put("/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("수정 완료되었습니다."));

        Mockito.verify(commentServiceImpl, Mockito.times(1)).updateComment(any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("댓글을 삭제한다")
    void deleteCommentTest() throws Exception {
        // given
        Mockito.when(commentServiceImpl.deleteComment(anyLong())).thenReturn(1);

        // when // then
        mockMvc.perform(delete("/comment/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료되었습니다."));

        Mockito.verify(commentServiceImpl, Mockito.times(1)).deleteComment(anyLong());
    }

    @Test
    @DisplayName("내가 쓴 댓글을 조회한다")
    void findCommentsByUserTest() throws Exception {
        // given
        CommentResponseDto responseDto = new CommentResponseDto(1L, "content", "2024-12-19", "limnj", "memberImg", 1L, 1L, 0L);
        Mockito.when(commentServiceImpl.findCommentsByUser(any(String.class))).thenReturn(List.of(responseDto));

        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getName()).thenReturn(responseDto.username());

        // when // then
        mockMvc.perform(get("/member/comments")
                        .principal(mockAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto.id()))
                .andExpect(jsonPath("$[0].content").value(responseDto.content()))
                .andExpect(jsonPath("$[0].username").value(responseDto.username()));

        Mockito.verify(commentServiceImpl, Mockito.times(1)).findCommentsByUser(any(String.class));
    }
}
