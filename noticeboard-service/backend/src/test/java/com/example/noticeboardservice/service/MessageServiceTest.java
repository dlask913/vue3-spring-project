package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import com.example.noticeboardservice.dto.MessageDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.mapper.MessageMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MessageServiceTest {
    @Autowired
    private MessageService messageServiceImpl;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MemberMapper memberMapper;

    @AfterEach
    void tearDown() {
        messageMapper.deleteAllMessages();
    }

    @Test
    @DisplayName("쪽지를 전송한다")
    void sendMessageTest() {
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageDto requestDto = MessageDto.builder()
                .content("쪽지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();

        // when
        messageServiceImpl.sendMessage(requestDto);

        // then
        MessageDto savedMessage = messageMapper.findAllMessages().get(0);
        assertThat(savedMessage.getContent()).isEqualTo(requestDto.getContent());
        assertThat(savedMessage.getSenderId()).isEqualTo(requestDto.getSenderId());
        assertThat(savedMessage.getReceiverId()).isEqualTo(requestDto.getReceiverId());
    }

    @Test
    @DisplayName("쪽지를 삭제한다")
    void deleteMessageTest(){
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageDto messageDto = MessageDto.builder()
                .content("쪽지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        messageServiceImpl.sendMessage(messageDto);
        MessageDto savedMessage = messageMapper.findAllMessages().get(0);

        // when
        messageServiceImpl.deleteMessage(savedMessage.getId());

        // then
        assertThat(messageMapper.findAllMessages().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("쪽지를 상세 조회한다")
    void findMessageByIdTest(){
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageDto messageDto = MessageDto.builder()
                .content("쪽지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        messageServiceImpl.sendMessage(messageDto);
        MessageDto savedMessage = messageMapper.findAllMessages().get(0);

        // when
        MessageDto findMessage = messageServiceImpl.findMessageByMessageId(savedMessage.getId());

        // then
        assertThat(findMessage.getContent()).isEqualTo(savedMessage.getContent());
        assertThat(findMessage.getSenderId()).isEqualTo(savedMessage.getSenderId());
        assertThat(findMessage.getReceiverId()).isEqualTo(savedMessage.getReceiverId());
    }

    @Test
    @DisplayName("사용자가 받은 모든 메시지를 조회한다")
    void findReceivedMessagesByMemberIdTest() {
        // given
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MemberResponseDto sender1 = getMember("limnj1@test.com");
        MemberResponseDto sender2 = getMember("limnj2@test.com");

        MessageDto message1 = MessageDto.builder().content("받은 메시지 1").senderId(sender1.id()).receiverId(receiver.id()).build();
        MessageDto message2 = MessageDto.builder().content("받은 메시지 2").senderId(sender2.id()).receiverId(receiver.id()).build();
        messageServiceImpl.sendMessage(message1);
        messageServiceImpl.sendMessage(message2);

        // when
        List<MessageDto> receivedMessages = messageServiceImpl.findReceivedMessagesByMemberId(receiver.id());

        // then
        assertThat(receivedMessages).hasSize(2);
        assertThat(receivedMessages.get(0).getReceiverId()).isEqualTo(receiver.id());
        assertThat(receivedMessages.get(0).getContent()).isEqualTo(message1.getContent());
        assertThat(receivedMessages.get(1).getReceiverId()).isEqualTo(receiver.id());
        assertThat(receivedMessages.get(1).getContent()).isEqualTo(message2.getContent());
    }

    @Test
    @DisplayName("사용자가 보낸 모든 메시지를 조회한다")
    void findSentMessagesByMemberIdTest() {
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver1 = getMember("limnj1@test.com");
        MemberResponseDto receiver2 = getMember("limnj2@test.com");

        MessageDto message1 = MessageDto.builder().content("보낸 메시지 1").senderId(sender.id()).receiverId(receiver1.id()).build();
        MessageDto message2 = MessageDto.builder().content("보낸 메시지 2").senderId(sender.id()).receiverId(receiver2.id()).build();
        messageServiceImpl.sendMessage(message1);
        messageServiceImpl.sendMessage(message2);

        // when
        List<MessageDto> sentMessages = messageServiceImpl.findSentMessagesByMemberId(sender.id());

        // then
        assertThat(sentMessages).hasSize(2);
        assertThat(sentMessages.get(0).getSenderId()).isEqualTo(sender.id());
        assertThat(sentMessages.get(0).getContent()).isEqualTo(message1.getContent());
        assertThat(sentMessages.get(1).getSenderId()).isEqualTo(sender.id());
        assertThat(sentMessages.get(1).getContent()).isEqualTo(message2.getContent());
    }

    private MemberResponseDto getMember(String email){
        MemberResponseDto findMember = memberMapper.findMemberByEmail(email);
        if (findMember == null) {
            MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .username("limnj1")
                    .build();
            memberMapper.insertMember(memberRequestDto);
            return memberMapper.findMemberByEmail(email);
        }
        return findMember;
    }

}