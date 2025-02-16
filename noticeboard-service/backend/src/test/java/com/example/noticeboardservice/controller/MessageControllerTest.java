package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.MessageDto;
import com.example.noticeboardservice.service.MessageService;
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

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageServiceImpl;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("쪽지를 전송한다")
    void sendMessageTest() throws Exception {
        // given
        MessageDto messageDto = MessageDto.builder()
                .senderId(1L)
                .receiverId(2L)
                .content("쪽지입니다")
                .build();
        Mockito.when(messageServiceImpl.sendMessage(any(MessageDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("메시지를 전송하였습니다."));

        Mockito.verify(messageServiceImpl, Mockito.times(1)).sendMessage(any(MessageDto.class));
    }

    @Test
    @DisplayName("쪽지를 삭제한다")
    void deleteMessageTest() throws Exception {
        // given
        Mockito.when(messageServiceImpl.deleteMessage(anyLong())).thenReturn(1);

        // when // then
        mockMvc.perform(delete("/message/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("메시지 삭제 완료되었습니다."));

        Mockito.verify(messageServiceImpl, Mockito.times(1)).deleteMessage(anyLong());
    }

    @Test
    @DisplayName("쪽지를 상세 조회한다")
    void findMessageByMessageIdTest() throws Exception {
        // given
        MessageDto responseDto = MessageDto.builder()
                .id(1L)
                .senderId(1L)
                .receiverId(2L)
                .content("쪽지입니다")
                .build();
        Mockito.when(messageServiceImpl.findMessageByMessageId(responseDto.getId())).thenReturn(responseDto);

        // when // then
        mockMvc.perform(get("/message/{messageId}", responseDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.senderId").value(responseDto.getSenderId()))
                .andExpect(jsonPath("$.receiverId").value(responseDto.getReceiverId()))
                .andExpect(jsonPath("$.content").value(responseDto.getContent()));

        Mockito.verify(messageServiceImpl, Mockito.times(1)).findMessageByMessageId(responseDto.getId());
    }

}