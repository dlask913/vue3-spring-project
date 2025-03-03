package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.config.filter.JwtTokenFilter;
import com.example.noticeboardservice.dto.MessageRequestDto;
import com.example.noticeboardservice.dto.MessageResponseDto;
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

import java.util.List;

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
    @DisplayName("메시지를 전송한다")
    void sendMessageTest() throws Exception {
        // given
        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .senderId(1L)
                .receiverId(2L)
                .content("메시지입니다")
                .build();
        Mockito.when(messageServiceImpl.sendMessage(any(MessageRequestDto.class))).thenReturn(1);

        // when // then
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("메시지를 전송하였습니다."));

        Mockito.verify(messageServiceImpl, Mockito.times(1)).sendMessage(any(MessageRequestDto.class));
    }

    @Test
    @DisplayName("메시지를 삭제한다")
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
    @DisplayName("메시지를 상세 조회한다")
    void findMessageByMessageIdTest() throws Exception {
        // given
        MessageResponseDto responseDto
                = new MessageResponseDto(1L, 1L, 2L, "메시지 조회", "2025-02-23 00:00:00", "N", 1L);
        Mockito.when(messageServiceImpl.findMessageByMessageId(responseDto.id())).thenReturn(responseDto);

        // when // then
        mockMvc.perform(get("/message/{messageId}", responseDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.id()))
                .andExpect(jsonPath("$.senderId").value(responseDto.senderId()))
                .andExpect(jsonPath("$.receiverId").value(responseDto.receiverId()))
                .andExpect(jsonPath("$.content").value(responseDto.content()))
                .andExpect(jsonPath("$.isRead").value("N"));

        Mockito.verify(messageServiceImpl, Mockito.times(1)).findMessageByMessageId(responseDto.id());
    }

    @Test
    @DisplayName("사용자가 받은 모든 메시지를 조회한다")
    void findReceivedMessagesByMemberIdTest() throws Exception {
        // given
        Long memberId = 1L;
        List<MessageResponseDto> messages = List.of(
                new MessageResponseDto(1L, 2L, memberId, "받은 메시지1", "2025-02-23 00:00:00", "N", 1L),
                new MessageResponseDto(2L, 3L, memberId, "받은 메시지2", "2025-02-23 00:00:00", "N", 1L)
        );
        Mockito.when(messageServiceImpl.findReceivedMessagesByMemberId(memberId)).thenReturn(messages);

        // when // then
        mockMvc.perform(get("/member/{memberId}/messages/received", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(messages.get(0).id()))
                .andExpect(jsonPath("$[0].senderId").value(messages.get(0).senderId()))
                .andExpect(jsonPath("$[0].receiverId").value(messages.get(0).receiverId()))
                .andExpect(jsonPath("$[0].content").value(messages.get(0).content()))
                .andExpect(jsonPath("$[0].isRead").value("N"))
                .andExpect(jsonPath("$[1].id").value(messages.get(1).id()))
                .andExpect(jsonPath("$[1].senderId").value(messages.get(1).senderId()))
                .andExpect(jsonPath("$[1].receiverId").value(messages.get(1).receiverId()))
                .andExpect(jsonPath("$[1].content").value(messages.get(1).content()))
                .andExpect(jsonPath("$[1].isRead").value("N"));

        Mockito.verify(messageServiceImpl, Mockito.times(1)).findReceivedMessagesByMemberId(memberId);
    }

    @Test
    @DisplayName("사용자가 보낸 모든 메시지를 조회한다")
    void findSentMessagesByMemberIdTest() throws Exception {
        // given
        Long memberId = 1L;
        List<MessageResponseDto> messages = List.of(
                new MessageResponseDto(1L, memberId, 2L, "보낸 메시지1", "2025-02-23 00:00:00", "N", 1L),
                new MessageResponseDto(2L, memberId, 3L, "보낸 메시지2", "2025-02-23 00:00:00", "N", 1L)
        );
        Mockito.when(messageServiceImpl.findSentMessagesByMemberId(memberId)).thenReturn(messages);

        // when // then
        mockMvc.perform(get("/member/{memberId}/messages/sent", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(messages.get(0).id()))
                .andExpect(jsonPath("$[0].senderId").value(messages.get(0).senderId()))
                .andExpect(jsonPath("$[0].receiverId").value(messages.get(0).receiverId()))
                .andExpect(jsonPath("$[0].content").value(messages.get(0).content()))
                .andExpect(jsonPath("$[0].isRead").value("N"))
                .andExpect(jsonPath("$[1].id").value(messages.get(1).id()))
                .andExpect(jsonPath("$[1].senderId").value(messages.get(1).senderId()))
                .andExpect(jsonPath("$[1].receiverId").value(messages.get(1).receiverId()))
                .andExpect(jsonPath("$[1].content").value(messages.get(1).content()))
                .andExpect(jsonPath("$[1].isRead").value("N"));

        Mockito.verify(messageServiceImpl, Mockito.times(1)).findSentMessagesByMemberId(memberId);
    }

    @Test
    @DisplayName("받은 메시지를 읽는다.")
    void updateReadStatusOfMessage() throws Exception {
        // given
        Mockito.when(messageServiceImpl.updateReadStatus(anyLong())).thenReturn(1);

        // when // then
        mockMvc.perform(patch("/message/{messageId}/read", anyLong()))
                .andExpect(status().isOk())
                .andExpect(content().string("메시지 읽음 처리가 완료되었습니다."));
        Mockito.verify(messageServiceImpl, Mockito.times(1)).updateReadStatus(anyLong());
    }
}