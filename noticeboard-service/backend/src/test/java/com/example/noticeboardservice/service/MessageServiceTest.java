package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import com.example.noticeboardservice.dto.message.MessageRequestDto;
import com.example.noticeboardservice.dto.message.MessageResponseDto;
import com.example.noticeboardservice.dto.message.RoomDto;
import com.example.noticeboardservice.mapper.member.MemberMapper;
import com.example.noticeboardservice.mapper.message.MessageMapper;
import com.example.noticeboardservice.mapper.message.RoomMapper;
import com.example.noticeboardservice.service.message.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MessageServiceTest {
    @Autowired
    private MessageService messageServiceImpl;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private RoomMapper roomMapper;

    @Test
    @DisplayName("처음 상대방에게 메시지를 전송한다")
    void sendMessageTest() {
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageRequestDto requestDto = MessageRequestDto.builder()
                .content("메시지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();

        // when
        messageServiceImpl.sendMessage(requestDto);

        // then
        MessageResponseDto savedMessage = messageMapper.findMessageById(requestDto.getId());
        assertThat(savedMessage.content()).isEqualTo(requestDto.getContent());
        assertThat(savedMessage.senderId()).isEqualTo(requestDto.getSenderId());
        assertThat(savedMessage.receiverId()).isEqualTo(requestDto.getReceiverId());
        assertThat(savedMessage.isRead()).isEqualTo("N");

        RoomDto findRoom = roomMapper.findRoomByMembers(sender.id(), receiver.id());
        assertThat(findRoom.getLowerId()).isEqualTo(min(sender.id(), receiver.id()));
        assertThat(findRoom.getHigherId()).isEqualTo(max(sender.id(), receiver.id()));
    }

    @Test
    @DisplayName("메시지를 삭제한다")
    void deleteMessageTest(){
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .content("메시지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        messageServiceImpl.sendMessage(messageRequestDto);
        MessageResponseDto savedMessage = messageMapper.findMessageById(messageRequestDto.getId());

        // when
        messageServiceImpl.deleteMessage(savedMessage.id());

        // then
        assertThat(messageMapper.findAllMessages().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("메시지를 상세 조회한다")
    void findMessageByIdTest(){
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .content("메시지 보내요")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        messageServiceImpl.sendMessage(messageRequestDto);
        MessageResponseDto savedMessage = messageMapper.findMessageById(messageRequestDto.getId());

        // when
        MessageResponseDto findMessage = messageServiceImpl.findMessageByMessageId(savedMessage.id());

        // then
        assertThat(findMessage.content()).isEqualTo(savedMessage.content());
        assertThat(findMessage.senderId()).isEqualTo(savedMessage.senderId());
        assertThat(findMessage.receiverId()).isEqualTo(savedMessage.receiverId());
        assertThat(findMessage.isRead()).isEqualTo("N");
    }

    @Test
    @DisplayName("사용자가 받은 모든 메시지를 조회한다")
    void findReceivedMessagesByMemberIdTest() {
        // given
        MemberResponseDto receiver = getMember("limnjReceiver@test.com");
        MemberResponseDto sender1 = getMember("limnj1@test.com");
        MemberResponseDto sender2 = getMember("limnj2@test.com");

        MessageRequestDto message1 = MessageRequestDto.builder().content("받은 메시지 1").senderId(sender1.id()).receiverId(receiver.id()).build();
        MessageRequestDto message2 = MessageRequestDto.builder().content("받은 메시지 2").senderId(sender2.id()).receiverId(receiver.id()).build();
        messageServiceImpl.sendMessage(message1);
        messageServiceImpl.sendMessage(message2);

        // when
        List<MessageResponseDto> receivedMessages = messageServiceImpl.findReceivedMessagesByMemberId(receiver.id());

        // then
        assertThat(receivedMessages).hasSize(2);
        assertThat(receivedMessages.get(0).receiverId()).isEqualTo(receiver.id());
        assertThat(receivedMessages.get(0).content()).isEqualTo(message1.getContent());
        assertThat(receivedMessages.get(0).isRead()).isEqualTo("N");
        assertThat(receivedMessages.get(1).receiverId()).isEqualTo(receiver.id());
        assertThat(receivedMessages.get(1).content()).isEqualTo(message2.getContent());
        assertThat(receivedMessages.get(1).isRead()).isEqualTo("N");
    }

    @Test
    @DisplayName("사용자가 보낸 모든 메시지를 조회한다")
    void findSentMessagesByMemberIdTest() {
        // given
        MemberResponseDto sender = getMember("limnjSender@test.com");
        MemberResponseDto receiver1 = getMember("limnj1@test.com");
        MemberResponseDto receiver2 = getMember("limnj2@test.com");

        MessageRequestDto message1 = MessageRequestDto.builder().content("보낸 메시지 1").senderId(sender.id()).receiverId(receiver1.id()).build();
        MessageRequestDto message2 = MessageRequestDto.builder().content("보낸 메시지 2").senderId(sender.id()).receiverId(receiver2.id()).build();
        messageServiceImpl.sendMessage(message1);
        messageServiceImpl.sendMessage(message2);

        // when
        List<MessageResponseDto> sentMessages = messageServiceImpl.findSentMessagesByMemberId(sender.id());

        // then
        assertThat(sentMessages).hasSize(2);
        assertThat(sentMessages.get(0).senderId()).isEqualTo(sender.id());
        assertThat(sentMessages.get(0).content()).isEqualTo(message1.getContent());
        assertThat(sentMessages.get(0).isRead()).isEqualTo("N");
        assertThat(sentMessages.get(1).senderId()).isEqualTo(sender.id());
        assertThat(sentMessages.get(1).content()).isEqualTo(message2.getContent());
        assertThat(sentMessages.get(1).isRead()).isEqualTo("N");
    }

    @Test
    @DisplayName("받은 메시지를 읽는다")
    void updateReadStatusTest(){
        // given
        MemberResponseDto sender = getMember("sender@test.com");
        MemberResponseDto receiver = getMember("receiver@test.com");
        MessageRequestDto message1 = MessageRequestDto.builder()
                .content("메시지1")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        MessageRequestDto message2 = MessageRequestDto.builder()
                .content("메시지2")
                .senderId(sender.id())
                .receiverId(receiver.id())
                .build();
        messageServiceImpl.sendMessage(message1);
        messageServiceImpl.sendMessage(message2);
        MessageResponseDto savedMessage = messageMapper.findMessageById(message1.getId());

        // when
        messageServiceImpl.updateReadStatus(savedMessage.receiverId(), savedMessage.senderId());

        // then
        List<MessageResponseDto> messages = messageMapper.findReceivedMessagesByMemberId(receiver.id());
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).isRead()).isEqualTo("Y");
        assertThat(messages.get(1).isRead()).isEqualTo("Y");
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