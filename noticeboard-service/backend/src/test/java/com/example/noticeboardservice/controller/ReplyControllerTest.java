package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.ReplyRequestDto;
import com.example.noticeboardservice.dto.ReplyResponseDto;
import com.example.noticeboardservice.service.ReplyService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReplyController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class ReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReplyService replyServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("대댓글을 저장한다")
    void saveReplyTest() throws Exception {
        // given
        ReplyRequestDto requestDto = ReplyRequestDto.builder()
                .content("content")
                .memberId(1L)
                .commentId(1L)
                .build();

        Mockito.when(replyServiceImpl.insertReply(any(ReplyRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(post("/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("대댓글을 저장하였습니다."));

        Mockito.verify(replyServiceImpl, Mockito.times(1)).insertReply(any(ReplyRequestDto.class));
    }

    @Test
    @DisplayName("특정 댓글의 대댓글을 조회한다")
    void findRepliesByCommentIdTest() throws Exception {
        // given
        Long commentId = 1L;
        List<ReplyResponseDto> responseDtos = List.of(
                new ReplyResponseDto(1L, "content", "limnj", 1L, commentId, "2024-12-20", "2024-12-20", "imgUrl")
        );

        Mockito.when(replyServiceImpl.findRepliesByCommentId(commentId)).thenReturn(responseDtos);

        // when // then
        mockMvc.perform(get("/replies/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtos.get(0).id()))
                .andExpect(jsonPath("$[0].content").value(responseDtos.get(0).id()));

        Mockito.verify(replyServiceImpl, Mockito.times(1)).findRepliesByCommentId(commentId);
    }

    @Test
    @DisplayName("대댓글을 수정한다")
    void updateReplyTest() throws Exception {
        // given
        ReplyRequestDto requestDto = ReplyRequestDto.builder()
                .id(1L)
                .content("content")
                .memberId(1L)
                .commentId(1L)
                .build();

        Mockito.when(replyServiceImpl.updateReply(any(ReplyRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(put("/reply/{replyId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("수정 완료되었습니다."));

        Mockito.verify(replyServiceImpl, Mockito.times(1)).updateReply(any(ReplyRequestDto.class));
    }

    @Test
    @DisplayName("대댓글을 삭제한다")
    void deleteReplyTest() throws Exception {
        // given
        Long replyId = 1L;

        Mockito.when(replyServiceImpl.deleteReply(replyId)).thenReturn(1);

        // when // then
        mockMvc.perform(delete("/reply/{replyId}", replyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료되었습니다."));

        Mockito.verify(replyServiceImpl, Mockito.times(1)).deleteReply(replyId);
    }

}